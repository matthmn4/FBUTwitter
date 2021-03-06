package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetClickListener, ComposeFragment.ComposeFragmentDialogListener {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;
    //MenuItem miActionProgressItem;
    ProgressBar pb;
    TwitterClient client;
    RecyclerView rvTweets;
    private FloatingActionButton fab;
    TweetsAdapter adapter;
    List<Tweet> tweets;

    EndlessRecyclerViewScrollListener scrollListener;

    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        client = TwitterApp.getRestClient(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.reuse_toolbar);
        setSupportActionBar(myToolbar);


        rvTweets = findViewById(R.id.rvTweets);
        fab = findViewById(R.id.fab);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);



        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore" + page);
                loadMoreData();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.addItemDecoration(new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance(false);
                composeFragment.show(fm, "Compose a tweet");
            }
        });

        populateHomeTimeline();
    }

    private void loadMoreData() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loadMoreData!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure for loadMoreData!", throwable);
            }
        }, tweets.get(tweets.size() -1 ).id);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + response);
            }
        });
    }


    private void populateHomeTimeline() {
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //Log.d(TAG, "onSuccess!");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("TAG", "Tweet getting error");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + response, throwable);
            }
        });
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            //Intent i = new Intent(this, ComposeActivity.class);
            //i.putExtra("reply", false);
            //startActivityForResult(i, REQUEST_CODE);
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance(false);
            composeFragment.show(fm, "Compose a tweet");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFloatingComposeClick() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(false);
        composeFragment.show(fm, "Compose a tweet");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onProfileImageClick(Tweet tweet) {
        Intent pi = new Intent(TimelineActivity.this, FollowActivity.class);
        pi.putExtra("user", Parcels.wrap(tweet));
        startActivity(pi);
    }

    @Override
    public void onFavoriteClick(final int pos, boolean isChecked) {
        if (!isChecked) {
            client.favoriteTweet(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "Tweet was liked.");
                    Log.d("test", "test2: " + tweets.get(pos));
                    tweets.get(pos).liked = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "Tweet had a problem liking.");
                }
            });
        } else {
            client.unfavoriteTweet(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "Tweet was unliked.");
                    tweets.get(pos).liked = false;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "Tweet had a problem liking.");
                }
            });
        }
    }

    @Override
    public void onReplyClick(int pos) {
        Intent i = new Intent(this, ComposeActivity.class);
//        i.putExtra("user", tweets.get(pos).user.screenName);
//        i.putExtra("reply", true);
//        startActivityForResult(i, REQUEST_CODE);
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(true, tweets.get(pos).user.screenName);
        composeFragment.show(fm, "Reply to a tweet");
    }

    @Override
    public void onRetweet(final int pos, boolean isChecked) {
        if (!isChecked) {
            client.retweet(tweets.get(pos).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "Tweet was retweeted.");
                    tweets.get(pos).retweeted = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "Tweet had a problem liking.");
                }
            });
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
        Intent j = new Intent(TimelineActivity.this, DetailActivity.class);
        j.putExtra("tweet", Parcels.wrap(tweets.get(position)));
        startActivity(j);
    }


    @Override
    public void onFinishCompose(Tweet tweet) {
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);
    }
}
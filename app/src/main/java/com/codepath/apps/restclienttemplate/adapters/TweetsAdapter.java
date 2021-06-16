package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private OnTweetClickListener listener;

    public interface OnTweetClickListener {
        void onProfileImageClick(Tweet tweet);
        void onFavoriteClick(int pos, boolean isChecked);
        void onReplyClick(int pos);
        void onRetweet(int pos, boolean isChecked);
    }

    public TweetsAdapter(OnTweetClickListener listener, Context context, List<Tweet> tweets) {
        this.listener = listener;
        this.context = context;
        this.tweets = tweets;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    Context context;
    List<Tweet> tweets;


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvDate;
        ImageView ivMedia;
        ImageButton btnRetweet;
        ImageButton btnLike;
        ImageButton btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvHandle);
            tvName = itemView.findViewById(R.id.tvScreenName);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnMessage = itemView.findViewById(R.id.btnMessage);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(String.format("@%s", tweet.user.screenName));
            tvName.setText(tweet.user.name);
            tvDate.setText(getRelativeTimeAgo(tweet.createdAt));
            if(tweet.liked) {
                btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }
            if(tweet.retweeted) {
                btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .circleCrop()
                    .into(ivProfileImage);
            Glide.with(context)
                    .load(tweet.media_url)
                    .into(ivMedia);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfileImageClick(tweet);
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tweet.liked) {
                        listener.onFavoriteClick(getLayoutPosition(), false);
                        Log.d("test", "hello :" + getLayoutPosition());
                        Toast.makeText(context,"Tweet was liked!", Toast.LENGTH_SHORT).show();
                        btnLike.setImageResource(R.drawable.ic_vector_heart);
                    } else {
                        listener.onFavoriteClick(getLayoutPosition(), true);
                        Toast.makeText(context,"Tweet was unliked!", Toast.LENGTH_SHORT).show();
                        btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                    }
                }
            });

            btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Tweet was retweeted!", Toast.LENGTH_SHORT).show();
                    if (!tweet.retweeted) {
                        listener.onRetweet(getLayoutPosition(), false);
                        Log.d("test", "hello :" + getLayoutPosition());
                        Toast.makeText(context,"Tweet was retweeted!", Toast.LENGTH_SHORT).show();
                        btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                    }
                }
            });

            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReplyClick(getLayoutPosition());
                }
            });
        }
    }



    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}

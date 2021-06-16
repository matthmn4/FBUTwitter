package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    ImageButton btnRetweet;
    ImageButton btnLike;
    ImageButton btnMessage;
    ImageView ivMain;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent j = getIntent();
        tweet = Parcels.unwrap(j.getParcelableExtra("tweet"));
        Log.d("pos + tweet: ", " tweet name " + tweet.user.name);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.reuse_toolbar);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivProfileImage = findViewById(R.id.ivTweetProfile);
        tvBody = findViewById(R.id.tvCaption);
        tvScreenName = findViewById(R.id.tvTweetHandle);
        tvName = findViewById(R.id.tvTweetName);
        btnRetweet = findViewById(R.id.btnRetweet);
        btnLike = findViewById(R.id.btnLike);
        btnMessage = findViewById(R.id.btnMessage);
        ivMain = findViewById(R.id.imageView);

        tvBody.setText(tweet.body);
        tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .circleCrop()
                .into(ivProfileImage);
        Glide.with(this)
                .load(tweet.media_url)
                .into(ivMain);




    }
}
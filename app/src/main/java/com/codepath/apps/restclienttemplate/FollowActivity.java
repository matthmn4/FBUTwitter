package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codepath.apps.restclienttemplate.adapters.FollowAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

public class FollowActivity extends AppCompatActivity {

    User user;
    TwitterClient client;
    FollowAdapter followAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem followers = findViewById(R.id.action_followers);
        TabItem following = findViewById(R.id.action_following);
        final ViewPager viewPager = findViewById(R.id.navigation);


        Intent r = getIntent();
        Tweet tweet = Parcels.unwrap(r.getParcelableExtra("user"));

        client = TwitterApp.getRestClient(this);

        //getFollowers();
        //getFollowing();

        followAdapter = new FollowAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), tweet);
        viewPager.setAdapter(followAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }





}
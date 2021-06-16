package com.codepath.apps.restclienttemplate.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.fragments.FollowerFragment;
import com.codepath.apps.restclienttemplate.fragments.FollowingFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class FollowAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private Tweet tweet;

    public FollowAdapter(FragmentManager fm, int numOfTabs, Tweet tweet) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.tweet = tweet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
            return new FollowingFragment(tweet);
            case 1:
            return new FollowerFragment(tweet);
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

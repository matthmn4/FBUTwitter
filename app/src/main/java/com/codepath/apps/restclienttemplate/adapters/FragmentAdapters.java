package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

public class FragmentAdapters extends RecyclerView.Adapter<FragmentAdapters.ViewHolder>{

    Context context;
    List<User> users;

    public FragmentAdapters(List<User> users, Context context) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false);
        return new FragmentAdapters.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        Log.d("fragmentadapter", String.valueOf(users.size()));
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFollowImage;
        TextView tvHandle;
        TextView tvScreenName;
        TextView tvBio;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFollowImage = itemView.findViewById(R.id.ivFollowImage);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBio = itemView.findViewById(R.id.tvBio);
        }

        public void bind(User user) {
            Glide.with(context)
                    .load(user.profileImageUrl)
                    .circleCrop()
                    .into(ivFollowImage);
            tvHandle.setText(String.format("@%s", user.screenName));
            tvScreenName.setText(user.name);
            tvBio.setText(user.description);
        }
    }
}

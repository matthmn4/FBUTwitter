package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;


public class ComposeFragment extends DialogFragment {

    EditText etCompose;
    Button btnTweet;
    User user;
    TextView tvTweetHandle;
    TextView tvName;
    ImageView ivTweetProfile;
    Boolean reply;
    TwitterClient client;
    ComposeFragmentDialogListener listener;

    public interface ComposeFragmentDialogListener {
        void onFinishCompose(Tweet tweet);
    }

    public ComposeFragment() {
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ComposeFragmentDialogListener) {
            listener = (ComposeFragmentDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ComposeFragmentDialogListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient(getContext());
        tvName = view.findViewById(R.id.tvTweetName);
        tvTweetHandle = view.findViewById(R.id.tvTweetHandle);
        ivTweetProfile = view.findViewById(R.id.ivTweetProfile);
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);

        getUser();

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(getContext(), "Sorry, tweet cannot be empty", Toast.LENGTH_SHORT).show();
                }
                if (tweetContent.length() > 140) {
                    Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                }
                client.sendTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.d("fragment", tweet.toString());
                            listener.onFinishCompose(tweet);
                            getDialog().dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {

                    }
                });
            }
        });

        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public static ComposeFragment newInstance(Boolean reply) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putBoolean("reply", reply);
        frag.setArguments(args);
        return frag;
    }

    public static ComposeFragment newInstance(Boolean reply, String handle) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putBoolean("reply", reply);
        args.putString("user", handle);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reply = getArguments().getBoolean("reply");
    }

    private void getUser() {
        String scrname = "";
        if (reply) {
            scrname = getArguments().getString("user");
        }
        final String finalScrname = scrname;
        client.getProfile(new JsonHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject JSONUser = json.jsonObject;
                Log.d("Compose", JSONUser.toString());
                try {
                    user = User.fromJson(JSONUser);
                    tvTweetHandle.setText(String.format("@%s", user.screenName));
                    tvName.setText(user.name);
                    Glide.with(getActivity())
                            .load(user.profileImageUrl)
                            .circleCrop()
                            .into(ivTweetProfile);
                    if (reply) {
                        etCompose.setText(String.format("@%s", finalScrname));
                    }
                    Log.d("Compose", user.name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener= null;
        Log.d("fragment", "on detached");
    }

}
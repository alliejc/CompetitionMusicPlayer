package com.example.alisonjc.compmusicplayer;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.Item;
import com.example.alisonjc.compmusicplayer.spotify.service.SpotifyServiceInterface;
import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.SpotifyUser;
import com.example.alisonjc.compmusicplayer.spotify.service.model.playlists.UserPlaylists;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "comp-music-player-login://callback";
    private static PlaylistAdapter mPlaylistItem;
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public String mToken = "";
    public String mUserId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mUserId = getPreferences(Context.MODE_PRIVATE).getString("user","");
        mToken = getPreferences(Context.MODE_PRIVATE).getString("token","");

        if(mUserId == "" || mToken == "" ){

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = MySignInDialog.newInstance();
            newFragment.show(ft, "dialog");


        } else {

            updateCurrentUserPlaylists(mToken);

        }

        mPlaylistItem = new PlaylistAdapter(this,R.layout.playlist_item, new ArrayList<Item>());

        final ListView listView = (ListView) findViewById(R.id.playlistview);
        listView.setAdapter(mPlaylistItem);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item mItem = (Item) parent.getAdapter().getItem(position);
                String playlistId = mItem.getId();
                String ownerId = mItem.getOwner().getId();
                String playlistName = mItem.getName();

                Bundle b = new Bundle();
                b.putString("playlistId", playlistId);
                b.putString("spotifyToken", mToken);
                b.putString("ownerId", ownerId);
                b.putString("playlistName", playlistName);

                Animation animation1 = new AlphaAnimation(0.1f, 0.3f);
                animation1.setDuration(1000);
                view.startAnimation(animation1);

                Intent intent = new Intent(getApplicationContext(), PlaylistTracksActivity.class);
                intent.putExtras(b);

                startActivity(intent);
            }
            });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(R.string.app_subtitle);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_music_1);
    }

        private void updateUserInfo(final String token) {

        getSpotifyService().getCurrentUser("Bearer " + token).enqueue(new Callback<SpotifyUser>() {
            @Override
            public void onResponse(Call<SpotifyUser> call, Response<SpotifyUser> response) {

                if(response.body() != null) {
                    mUserId = response.body().getId();
                    getPreferences(Context.MODE_PRIVATE).edit().putString("user", mUserId).apply();
                    getPreferences(Context.MODE_PRIVATE).edit().putString("token", mToken).apply();
                }
            }

            @Override
            public void onFailure(Call<SpotifyUser> call, Throwable t) {

            }
        });
    }

    private void updateCurrentUserPlaylists(String token) {
        getSpotifyService().getCurrentUserPlaylists("Bearer " + token).enqueue(new Callback<UserPlaylists>() {
            @Override
            public void onResponse(Call<UserPlaylists> call, Response<UserPlaylists> response) {

                if(response.body() != null) {
                    updateListView(response.body().getItems());
                    }
            }

            @Override
            public void onFailure(Call<UserPlaylists> call, Throwable t) {

            }
        });

    }

    private void updateListView(List<Item> items) {
        mPlaylistItem.clear();
        mPlaylistItem.addAll(items);
        mPlaylistItem.notifyDataSetChanged();
    }


    private SpotifyServiceInterface getSpotifyService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

                return retrofit.create(SpotifyServiceInterface.class);
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {

            case R.id.one_minute_thirty:
                if (checked){
                    Toast.makeText(this, "Please select a playlist", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.two_minutes:
                if (checked) {
                    Toast.makeText(this, "Please select a playlist", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }


    public static class MySignInDialog extends DialogFragment {

        static MySignInDialog newInstance() {
            return new MySignInDialog();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_login, container, false);

            getDialog().setTitle("Login");
            getDialog().setCanceledOnTouchOutside(false);

            View mLoginButton = v.findViewById(R.id.spotifyLoginButton);
            mLoginButton.setVisibility(View.VISIBLE);

            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
                    builder.setScopes(new String[]{"playlist-read-private", "playlist-read-collaborative", "playlist-modify-public", "playlist-modify-private", "streaming",
                            "user-follow-modify", "user-follow-read", "user-library-read", "user-library-modify", "user-read-private", "user-read-birthdate", "user-read-email"});
                    builder.setShowDialog(true);
                    AuthenticationRequest request = builder.build();

                    AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);

                    onDestroyView();
                }
            });

            return v;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

                switch (response.getType()) {
                    case TOKEN:
                        mToken = response.getAccessToken();
                        updateCurrentUserPlaylists(mToken);
                        updateUserInfo(mToken);
                            break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        break;

                    // Most likely auth flow was cancelled
                    default:
                }
            }
            }

        @Override
        public void onLoggedIn() {
            Log.d("PlaylistActivity", "User logged in");
        }

        @Override
        public void onLoggedOut() {
            Log.d("PlaylistActivity", "User logged out");
        }

        @Override
        public void onLoginFailed(Throwable error) {
            Log.d("PlaylistActivity", "Login failed");
        }

        @Override
        public void onTemporaryError() {
            Log.d("PlaylistActivity", "Temporary error occurred");
        }

        @Override
        public void onConnectionMessage(String message) {
            Log.d("PlaylistActivity", "Received connection message: " + message);
        }

        @Override
        public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
            Log.d("PlaylistActivity", "Playback event received: " + eventType.name());
        }

        @Override
        public void onPlaybackError(ErrorType errorType, String errorDetails) {
            Log.d("PlaylistActivity", "Playback error received: " + errorType.name());
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }


}





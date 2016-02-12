package com.example.alisonjc.compmusicplayer;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alisonjc.compmusicplayer.spotify.Item;
import com.example.alisonjc.compmusicplayer.spotify.SpotifyService;
import com.example.alisonjc.compmusicplayer.spotify.SpotifyUser;
import com.example.alisonjc.compmusicplayer.spotify.UserPlaylists;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements PlayerNotificationCallback, ConnectionStateCallback {


    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "comp-music-player-login://callback";
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private Player mPlayer;
    private List<Item> mItems;

    private static ArrayAdapter<String> mArrayAdapter;

    private SpotifyService token = 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = MySignInDialog.newInstance();
            newFragment.show(ft, "dialog");
        } else {
            Toast.makeText(MainActivity.this,
                    "Your Message", Toast.LENGTH_LONG).show();
        }

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.playlistview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlistId = mItems.get(position).getId();
                String playlistUri = mItems.get(position).getUri();


                Bundle b = new Bundle();
                b.putString("playlistId", playlistId);
                b.putString("playlistUri", playlistUri);



                Intent intent = new Intent(getApplicationContext(), PlaylistTracksActivity.class);
                intent.putExtras(b);

                startActivity(intent);
            }
        });

        listView.setAdapter(mArrayAdapter);

    }


    private void getUserInfo(final String token) {

        getSpotifyService().getCurrentUser("Bearer " + token).enqueue(new Callback<SpotifyUser>() {
            @Override
            public void onResponse(Call<SpotifyUser> call, Response<SpotifyUser> response) {
                getUserPlaylists(token, response.body().getId());

            }

            @Override
            public void onFailure(Call<SpotifyUser> call, Throwable t) {

            }
        });
    }


    private void getCurrentUserPlaylists(String token) {
        getSpotifyService().getCurrentUserPlaylists("Bearer " + token).enqueue(new Callback<UserPlaylists>() {
            @Override
            public void onResponse(Call<UserPlaylists> call, Response<UserPlaylists> response) {

                mArrayAdapter.clear();
                mItems = response.body().getItems();
                for(Item item : response.body().getItems()) {
                    mArrayAdapter.add(item.getName());
                }
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<UserPlaylists> call, Throwable t) {

            }
        });

    }

    private void getUserPlaylists(String token, String userId){
        getSpotifyService().getUserPlayLists("Bearer " + token, userId).enqueue(new Callback<UserPlaylists>() {
            @Override
            public void onResponse(Call<UserPlaylists> call, Response<UserPlaylists> response) {

                mArrayAdapter.clear();
                for(Item item : response.body().getItems()) {
                    mArrayAdapter.add(item.getName());
                }
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<UserPlaylists> call, Throwable t) {

            }
        });
    }
    private SpotifyService getSpotifyService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


                return retrofit.create(SpotifyService.class);
    }


    public static class MySignInDialog extends DialogFragment {

        static MySignInDialog newInstance() {
            return new MySignInDialog();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_login, container, false);

            TextView tv = (TextView) v.findViewById(R.id.text);
            getDialog().setTitle("Login Using Spotify");


            View mLoginButton = v.findViewById(R.id.spotifyLoginButton);
            mLoginButton.setVisibility(View.VISIBLE);

            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                            AuthenticationResponse.Type.TOKEN,
                            REDIRECT_URI);
                    builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-read-collaborative", "user-library-read" , });
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
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                switch (response.getType()) {
                    // Response was successful and contains auth token
                    case TOKEN:
                        String token = response.getAccessToken();
                        getCurrentUserPlaylists(token);
                        getUserInfo(token);

//                        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
//                        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
//                            @Override
//                            public void onInitialized(Player player) {
//                                mPlayer = player;
//                                mPlayer.addConnectionStateCallback(MainActivity.this);
//                                mPlayer.addPlayerNotificationCallback(MainActivity.this);
//                                //mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
//                            }
//
//                            @Override
//                            public void onError(Throwable throwable) {
//                                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
//                            }
//                        });

                            break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        break;

                    // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                }
            }

            }

        }



        @Override
        public void onLoggedIn() {
            Log.d("MainActivity", "User logged in");
        }

        @Override
        public void onLoggedOut() {
            Log.d("MainActivity", "User logged out");
        }

        @Override
        public void onLoginFailed(Throwable error) {
            Log.d("MainActivity", "Login failed");
        }

        @Override
        public void onTemporaryError() {
            Log.d("MainActivity", "Temporary error occurred");
        }

        @Override
        public void onConnectionMessage(String message) {
            Log.d("MainActivity", "Received connection message: " + message);
        }

        @Override
        public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
            Log.d("MainActivity", "Playback event received: " + eventType.name());
        }

        @Override
        public void onPlaybackError(ErrorType errorType, String errorDetails) {
            Log.d("MainActivity", "Playback error received: " + errorType.name());
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }





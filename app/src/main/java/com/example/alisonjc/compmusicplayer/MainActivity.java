package com.example.alisonjc.compmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {


    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "comp-music-player-login://callback";
    private static final String CLIENT_ID = "fea06d390d9848c3b5c0ff43bbe0b2d0";
    private Player mPlayer;
    private View mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (View) findViewById(R.id.spotifyLoginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClicked();
            }
        });

    }

    public void onLoginClicked () {

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-read-collaborative", "user-library-read" , });
        builder.setShowDialog(true);
        AuthenticationRequest request = builder.build();

       AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

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

                        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                            @Override
                            public void onInitialized(Player player) {
                                mPlayer = player;
                                mPlayer.addConnectionStateCallback(MainActivity.this);
                                mPlayer.addPlayerNotificationCallback(MainActivity.this);
                                //mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                            }
                        });

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





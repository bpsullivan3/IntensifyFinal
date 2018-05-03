package edu.estrauchvillanova.intensify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.InputStream;
import java.net.URL;

public class ThirdActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback{

    private static final String CLIENT_ID = "9cde138e795740dd8974ee43da45dfe8";
    private static final String REDIRECT_URI = "intensify://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1842;
    private int intensityVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-read-private", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String data_two = bundle.getString("value_button2");
        Bundle bundleOne = getIntent().getExtras();
        String data_one = bundleOne.getString("value_button1");
        intensityVal = Integer.parseInt(data_one) + Integer.parseInt(data_two);
        TextView intensityReading = findViewById(R.id.intensityValue);
        intensityReading.append(" " + Integer.toString(intensityVal));

        Button playPause = findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                playOrPauseSong(v);
            }
        });

        Button skipSong = findViewById(R.id.skipNext);
        skipSong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.skipToNext(null);
            }
        });

        Button prevSong = findViewById(R.id.skipPrev);
        prevSong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.skipToPrevious(null);
            }
        });



    }

    public void playOrPauseSong(View view){
        Button playPause = findViewById(R.id.playPause);
        if(mPlayer.getPlaybackState().isPlaying){
            mPlayer.pause(null);
            playPause.setText(getResources().getString(R.string.play));
        } else {
            mPlayer.resume(null);
            playPause.setText(getResources().getString(R.string.pause));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if(response.getType() == AuthenticationResponse.Type.TOKEN){
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(ThirdActivity.this);
                        mPlayer.addNotificationCallback(ThirdActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("ThirdActivity", "Could not initialize player: " + throwable.getMessage());

                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("ThirdActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            case kSpPlaybackNotifyTrackChanged:
                TextView songName = findViewById(R.id.songTitle);
                TextView artistName = findViewById(R.id.artistName);
                TextView albumName = findViewById(R.id.albumName);
                ImageView albumArt = findViewById(R.id.albumArt);

                new DownLoadImageTask(albumArt).execute(mPlayer.getMetadata().currentTrack.albumCoverWebUrl);
                songName.setText(mPlayer.getMetadata().currentTrack.name);
                artistName.setText(mPlayer.getMetadata().currentTrack.artistName);
                albumName.setText(mPlayer.getMetadata().currentTrack.albumName);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("ThirdActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("ThirdActivity", "User logged in");
        beginPlayer(intensityVal);


    }

    @Override
    public void onLoggedOut() {
        Log.d("ThirdActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("ThirdActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("ThirdActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("ThirdActivity", "Received connection message: " + message);
    }

    private static class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView imageView;

        private DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    public void beginPlayer(int intensityVal){
        switch(intensityVal){
            case 0:
                mPlayer.playUri(null, "spotify:track:3G6xjB96azqSKV3uU44Bmt", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0JR1UPExSwwEvTRub1SZI3");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0AUp2LQdUSJRtQa5rTx3fJ");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5HNCy40Ni5BZJFw1TKzRsC");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:632wpQN4nWLEvxUyGZxyLM");
                break;
            case 1:
                mPlayer.playUri(null, "spotify:track:2IJzqbcbQZjcca53yGkX7t", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6wvR0jv72FM0kwg1000cq9");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1mdlNDjI26EaMUNA1Ti9Xy");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:48UPSzbZjgc449aqz8bxox");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:7ArHv3Ry2dWHFwQ8b5BoON");
                break;
            case 2:
                mPlayer.playUri(null, "spotify:track:5ceGigL7CZQ3Ih6W8SIbv8", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1lguQJjlNrIOoOylYVZN3M");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:3d8y0t70g7hw2FOWl9Z4Fm");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:11IzgLRXV7Cgek3tEgGgjw");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5KXdflxoxCcE9m5F2QoAUY");
                break;
            case 3:
                mPlayer.playUri(null, "spotify:track:24NwBd5vZ2CK8VOQVnqdxr", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:70C4NyhjD5OZUMzvWZ3njJ");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6HSXNV0b4M4cLJ7ljgVVeh");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5PHGTeEdFxTEb4hepxug86");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5UWwZ5lm5PKu6eKsHAGxOk");
                break;
            case 4:
                mPlayer.playUri(null, "spotify:track:4nmne9J3YCEdhvjTzwiAgu", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1bp2IO61zbQrbWNmKKxg3f");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:29AqPjeqZcXpGvdxLchZoP");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6QZo2TgclkUMwJgggi8QSQ");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:2hdNya0b6Cc2YJ8IyaQIWp");
                break;
            case 5:
                mPlayer.playUri(null, "spotify:track:07KHJvlYBeQVqrmifTEqEp", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:3fkPMWQ6cBNBLuFcPyMS8s");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1QEEqeFIZktqIpPI4jSVSF");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6hnCx2jmD0RPkbtzUXSBn4");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6qd8ZujxMAQXQeVuQBFHTZ");
                break;
            case 6:
                mPlayer.playUri(null, "spotify:track:2oSpQ7QtIKTNFfA08Cy0ku", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5SZ6zX4rOrEQferfFC2MfP");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5XcZRgJv3zMhTqCyESjQrF");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:6NTqBHONQqmud0ONBzsLfZ");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5u5qlnyVaewWugJIjzilIc");
                break;
            case 7:
                mPlayer.playUri(null, "spotify:track:61BywQA7q8KjpGjp3rHuRv", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:57bgtoPSgt236HzfBOd8kj");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:3dix9ONSeCaR05vk8K2kEQ");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:5e3YOg6fIkP0wD5TyxcHOH");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:7dlvtSJgkXSOKtTH8OAHDY");
                break;
            case 8:
                mPlayer.playUri(null, "spotify:track:2HwtiwV8OgIwNuj1Wq9e0B", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:2dbOYFfS7r7NAzquRvji9A");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:2pxAohyJptQWTQ5ZRWYijN");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1LDAh7QQnWvp3GJ9QDa1Pj");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0i1RTnH2Lj5gTDRU5wtyT2");
                break;
            case 9:
                mPlayer.playUri(null, "spotify:track:5KjubyAZtsbXKuIy02Qo3N", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0E0bZtTG39K95uRjqBo1Mx");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0L7zm6afBEtrNKo6C6Gj08");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:0EYOdF5FCkgOJJla8DI2Md");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1m9uKIXsCb9nFFKNDPKuBP");
                break;
            case 10:
                mPlayer.playUri(null, "spotify:track:5AdoS3gS47x40nBNlNmPQ8", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:1aznwS1YqoNU9sugWBXOif");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:7wVdKwd0CZkzLT2cRcTSqz");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:72AJK9Ei8Xw3V66ET9sc6k");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:734y7nJZ0cPOACIoT9kWyV");
                break;
            default:
                mPlayer.playUri(null, "spotify:track:57bgtoPSgt236HzfBOd8kj", 0, 0);
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:57bgtoPSgt236HzfBOd8kj");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:57bgtoPSgt236HzfBOd8kj");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:57bgtoPSgt236HzfBOd8kj");
                try{
                    Thread.sleep(300);
                } catch(InterruptedException ignored) { }
                mPlayer.queue(null, "spotify:track:57bgtoPSgt236HzfBOd8kj");
                break;
        }
/*
        TextView songName = findViewById(R.id.songTitle);
        TextView artistName = findViewById(R.id.artistName);
        TextView albumName = findViewById(R.id.albumName);
        ImageView albumArt = findViewById(R.id.albumArt);

        new DownLoadImageTask(albumArt).execute(mPlayer.getMetadata().currentTrack.albumCoverWebUrl);
        songName.append(mPlayer.getMetadata().currentTrack.name);
        artistName.append(mPlayer.getMetadata().currentTrack.artistName);
        albumName.append(mPlayer.getMetadata().currentTrack.albumName);
*/
    }
}




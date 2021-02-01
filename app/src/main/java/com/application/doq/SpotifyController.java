package com.application.doq;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Item;
import com.spotify.protocol.types.PlayerState;

interface SpotifyRemoteInterface {
    void onSpotifyConnected();
    void spotifyNotInstalled();
    void spotifyNotLogged();
    void appNotAuthorized();
    void onPlaybackStateChange(PlayerState playerState);
};

public abstract class SpotifyController implements SpotifyRemoteInterface {

    private static final String TAG = SpotifyController.class.getSimpleName();
    private static final String CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    private static final String REDIRECT_URI = BuildConfig.SPOTIFY_REDIRECT_URI;

    private SpotifyAppRemote spotifyAppRemote;
    private final ConnectionParams connectionParams;
    private final Context context;

    public SpotifyController(Context context) {
        this.context = context;
        SpotifyAppRemote.setDebugMode(BuildConfig.DEBUG);
        connectionParams = new ConnectionParams
                .Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .build();
    }

    Connector.ConnectionListener spotifyConnector = new Connector.ConnectionListener() {
        @Override
        public void onConnected(SpotifyAppRemote _spotifyAppRemote) {
            spotifyAppRemote = _spotifyAppRemote;
            subscribeToPlayerState();
            onSpotifyConnected();
        }
        @Override
        public void onFailure(Throwable throwable) {
            if (throwable instanceof CouldNotFindSpotifyApp) spotifyNotInstalled();
            if (throwable instanceof NotLoggedInException) spotifyNotLogged();
            if (throwable instanceof UserNotAuthorizedException) appNotAuthorized();
            Log.e(TAG, throwable.getClass().getSimpleName(), throwable);
        }
    };

    private void subscribeToPlayerState() {
        spotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(this::onPlaybackStateChange);
    }

    public void connect() {
        SpotifyAppRemote.connect(this.context, connectionParams, spotifyConnector);
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }
}
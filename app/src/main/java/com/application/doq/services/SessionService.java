package com.application.doq.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.se.omapi.Session;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.application.doq.R;
import com.application.doq.SessionSocket;
import com.application.doq.SpotifyController;
import com.application.doq.notifications.session.SessionNotification;
import com.spotify.protocol.types.PlayerState;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SessionService extends Service {

    private static final String TAG = SessionService.class.getSimpleName();
    public static final String STOP_ACTION = "session.service.stop";
    public static final String OPEN_SHARESHEET = "session.service.sharesheet";

    private final Socket socketIo = SessionSocket.getInstance();
    private SessionNotification sessionNotification;
    private Handler handler;

    SpotifyController spotifyRemoteService = new SpotifyController(this) {
        @Override
        public void onSpotifyConnected() {
            startSession();
        }
        @Override
        public void spotifyNotInstalled() {
            Toast.makeText(getApplicationContext(), "Spotify is not installed", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        @Override
        public void spotifyNotLogged() {
            Toast.makeText(getApplicationContext(), "User not logged in Spotify", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        @Override
        public void appNotAuthorized() {
            Toast.makeText(getApplicationContext(), "User didn't authorized app", Toast.LENGTH_LONG).show();
            stopSelf();
        }
        @Override
        public void onPlaybackStateChange(PlayerState playerState) {
            socketIo.emit("playbackChanged", playerState.toString());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(this.getMainLooper());
        //SessionSocket.addQueryParam("sessionCode", "QWEQWE");
        SessionSocket.addQueryParam("authentication", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjEyMzQ1Njc4OTAiLCJpYXQiOjE1MTYyMzkwMjJ9.9Cv_tnOFTX2Y0L8ct7mZDlwrBu_Nz6QhTlXi25vbMRk");
        socketIo.on(Socket.EVENT_CONNECT, onConnect);
        socketIo.on(SessionSocket.EVENT_SESSION_JOINED, onSessionJoined);
        socketIo.on(SessionSocket.EVENT_SESSION_ERROR, onSessionError);
        socketIo.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socketIo.on(Socket.EVENT_ERROR, onError);
        socketIo.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STOP_ACTION.equals(intent.getAction())) this.closeSession();
        if (OPEN_SHARESHEET.equals(intent.getAction())) this.openSharesheet();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.socketIo.off();
        this.socketIo.disconnect();
        this.spotifyRemoteService.disconnect();
        this.stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final Emitter.Listener onConnect = (args) -> {
        handler.post(() -> {});
    };

    private final Emitter.Listener onConnectError = (args) -> {
        handler.post(()-> {
            Toast.makeText(this, getString(R.string.session_connection_error), Toast.LENGTH_SHORT).show();
            this.stopSelf();
        });
    };

    private final Emitter.Listener onError = (args) -> {
        handler.post(() -> {
            Toast.makeText(this, getString(R.string.session_socket_error), Toast.LENGTH_SHORT).show();
            this.stopSelf();
        });
    };

    private final Emitter.Listener onSessionJoined = (args) -> {
        handler.post(() -> { spotifyRemoteService.connect(); });
    };

    private final Emitter.Listener onSessionError = (args) -> {
        handler.post(() -> {
            Toast.makeText(this, args[0].toString(), Toast.LENGTH_SHORT).show();
            this.stopSelf();
        });
    };

    private void startSession() {
        this.openSharesheet();
        this.sessionNotification = new SessionNotification(this);
        this.sessionNotification.updateContentText("You, cloud_io, drew.");
        this.startForeground(sessionNotification.getNotificationId(), sessionNotification.getNotification());
    }

    private void closeSession(){
        this.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        this.stopSelf();
    }

    private void openSharesheet() {
        this.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        this.startActivity(getOpenSharesheetIntent(getUri()));
    }

    private Intent getOpenSharesheetIntent(String uri){
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, uri)
                .setType("text/plain");
        return Intent.createChooser(sendIntent, null)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private String getUri() {
        return "Link";
    }
}

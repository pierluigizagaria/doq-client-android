package com.application.doq;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SessionSocket {

    public static final String EVENT_SESSION_ERROR = "sessionError";
    public static final String EVENT_SESSION_JOINED = "sessionJoined";

    private static final String TAG = SessionSocket.class.getSimpleName();
    private static final int SOCKET_TIMEOUT = 1000;

    private static final IO.Options opts = new IO.Options();
    private static Socket socketIo;

    private SessionSocket() {}

    private static void initiateSocketIo(){
        opts.timeout = SOCKET_TIMEOUT;
        try {
            socketIo = IO.socket(BuildConfig.SOCKET_BASE_URL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void addQueryParam(String key, String value) {
        opts.query = opts.query == null ? key + "=" + value : opts.query.concat("&" + key + "=" + value);
    }

    public static Socket getInstance() {
        if (socketIo == null) { initiateSocketIo(); }
        return socketIo;
    }
}

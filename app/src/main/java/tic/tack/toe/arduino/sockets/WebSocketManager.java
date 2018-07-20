package tic.tack.toe.arduino.sockets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import timber.log.Timber;

public class WebSocketManager extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String TAG = "WebSocketManager";

    private final OkHttpClient mClient = new OkHttpClient();
    private MessageListener mMessageListener;
    private WebSocket mWebSocket;

    public void start() {
        Request request = new Request.Builder()
                .url("ws://daily-live.wcstd.net:9697")
                .build();
        this.mClient.newWebSocket(request, this);
    }

    public void setMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.mWebSocket = webSocket;
        Timber.tag(TAG).e("onOpen");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Timber.tag(TAG).e("onMessage");
        if (this.mMessageListener != null) {
            this.mMessageListener.onMessage(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        //empty
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Timber.tag(TAG).e("onClosing");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Timber.tag(TAG).e("onFailure %s", t.getMessage());
    }

    public void sendMessage(String message) {
        Timber.tag(TAG).e("sendMessage %s", message);

        if (this.mWebSocket != null) {
            this.mWebSocket.send(message);
        }
    }

    public void close() {
//        try {
//            JSONObject closeObject = new JSONObject();
//            closeObject.put(WebSocketConstants.EXIT_GAME,true);
//            sendMessage(closeObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (this.mWebSocket != null) {
            this.mWebSocket.close(NORMAL_CLOSURE_STATUS, "Client close request");
            Timber.tag(TAG).e("close");
        }
    }
}

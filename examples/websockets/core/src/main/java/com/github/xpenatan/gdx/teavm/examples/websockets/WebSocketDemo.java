package com.github.xpenatan.gdx.teavm.examples.websockets;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSockets;

public class WebSocketDemo extends ApplicationAdapter implements InputProcessor {

    public static final String DEFAULT_URL = "wss://ws.postman-echo.com/raw";

    private static final int MAX_LOG_LINES = 10;
    private static final int MAX_INPUT_LENGTH = 120;

    private final Array<String> logLines = new Array<>();
    private final String endpoint;
    private final StringBuilder inputBuffer = new StringBuilder("Hello server");
    private SpriteBatch batch;
    private BitmapFont font;
    private WebSocket socket;
    private String status = "Idle";
    private String lastMessage = "No messages yet";
    private String helperText = "Type text, press Enter to send, F5 reconnect, Esc clear";
    private int width = 800;
    private int height = 480;

    public WebSocketDemo() {
        this(DEFAULT_URL);
    }

    public WebSocketDemo(String endpoint) {
        this.endpoint = endpoint == null || endpoint.isEmpty() ? DEFAULT_URL : endpoint;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(this);
        connect();
    }

    private void connect() {
        closeSocket();
        status = "Connecting";
        lastMessage = "Waiting for server response";
        log("Connecting to " + endpoint);

        socket = WebSockets.newSocket(endpoint);
        socket.addListener(new WebSocketAdapter() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                status = "Open";
                log("Connected");
                sendMessage("Hello from gdx-teavm @ " + TimeUtils.millis());
                return NOT_HANDLED;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                status = "Closed (" + closeCode + ")";
                log("Closed: " + normalizeReason(reason));
                return NOT_HANDLED;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                lastMessage = packet;
                log("Received: " + packet);
                return NOT_HANDLED;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                status = "Error";
                log("Error: " + (error == null ? "unknown" : error.getMessage()));
                return NOT_HANDLED;
            }
        });
        socket.connect();
    }

    private void sendMessage(String message) {
        String trimmed = message == null ? "" : message.trim();
        if(trimmed.isEmpty()) {
            log("Nothing to send");
            return;
        }
        if(socket == null) {
            log("Socket not created yet");
            return;
        }
        if(!socket.isOpen()) {
            log("Socket is not open yet");
            return;
        }
        socket.send(trimmed);
        log("Sent: " + trimmed);
    }

    private void sendTypedMessage() {
        sendMessage(inputBuffer.toString());
    }

    private void appendTypedChar(char character) {
        if(character < 32 || character == 127) {
            return;
        }
        if(inputBuffer.length() >= MAX_INPUT_LENGTH) {
            log("Input limit reached");
            return;
        }
        inputBuffer.append(character);
    }

    private void deleteTypedChar() {
        if(inputBuffer.length() > 0) {
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);
        }
    }

    private String normalizeReason(String reason) {
        return reason == null || reason.isEmpty() ? "no reason" : reason;
    }

    private void log(String message) {
        if(message == null || message.isEmpty()) {
            return;
        }
        if(logLines.size >= MAX_LOG_LINES) {
            logLines.removeIndex(0);
        }
        logLines.add(message);
        Gdx.app.log("WebSocketDemo", message);
    }

    @Override
    public void resize(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.1f, 0.14f, 1f);

        batch.begin();
        float x = 24f;
        float y = height - 24f;
        float lineHeight = 26f;

        font.setColor(Color.WHITE);
        font.draw(batch, "gdx-teavm websocket demo", x, y);
        y -= lineHeight;
        font.draw(batch, "Status: " + status, x, y);
        y -= lineHeight;
        font.draw(batch, "Endpoint: " + endpoint, x, y);
        y -= lineHeight;
        font.draw(batch, helperText, x, y);
        y -= lineHeight;
        font.draw(batch, "Last message: " + lastMessage, x, y);
        y -= lineHeight;
        font.setColor(Color.valueOf("9CE5FF"));
        font.draw(batch, "Input > " + inputBuffer, x, y);
        y -= lineHeight * 1.5f;
        font.setColor(Color.WHITE);
        font.draw(batch, "Recent events:", x, y);
        y -= lineHeight;

        for(int i = logLines.size - 1; i >= 0; i--) {
            font.draw(batch, logLines.get(i), x, y);
            y -= lineHeight;
        }
        batch.end();
    }

    private void closeSocket() {
        if(socket == null) {
            return;
        }
        try {
            if(!socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {
            log("Ignoring close error: " + ignored.getMessage());
        }
        socket = null;
    }

    @Override
    public void dispose() {
        closeSocket();
        Gdx.input.setInputProcessor(null);
        font.dispose();
        batch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE) {
            inputBuffer.setLength(0);
            return true;
        }
        if(keycode == Input.Keys.F5) {
            connect();
            return true;
        }
        if(keycode == Input.Keys.BACKSPACE) {
            deleteTypedChar();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(character == '\r' || character == '\n') {
            sendTypedMessage();
            return true;
        }
        if(character == '\b') {
            deleteTypedChar();
            return true;
        }
        appendTypedChar(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

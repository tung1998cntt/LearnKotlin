package com.example.learnkotlin.core.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer

abstract class FullFeatureWebSocketAdvanced(
    private val url: String,
    private val getToken: () -> String,            // JWT token provider
    private val heartbeatInterval: Long = 15000L, // ms
    private val maxReconnectAttempts: Int = 5,
    private val maxSendRate: Long = 50L           // ms giữa 2 message (throttling)
) {
    private val TAG = "FFWebSocketAdvanced"
    private val client = OkHttpClient.Builder()
        .pingInterval(heartbeatInterval, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var reconnectAttempts = 0
    private var heartbeatTimer: Timer? = null
    private val messageQueue = ConcurrentLinkedQueue<Message>()
    private var lastSentTime = 0L
    private val mainHandler = Handler(Looper.getMainLooper())
    private val sendRunnable = object : Runnable {
        override fun run() {
            flushQueue()
            mainHandler.postDelayed(this, maxSendRate)
        }
    }

    // --- Data class message ---
    private data class Message(
        val topic: String,
        val payload: Any, // JSONObject or ByteString
        val isBinary: Boolean = false
    )

    // --- Abstract callbacks ---
    abstract fun onMessageReceived(topic: String, message: JSONObject)
    abstract fun onBinaryReceived(topic: String, bytes: ByteString)
    abstract fun onOpen()
    abstract fun onClosed()
    abstract fun onFailure(t: Throwable)
    abstract fun onTokenExpired()

    // --- Connect / Reconnect ---
    fun connect() {
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Connected")
                reconnectAttempts = 0
                startHeartbeat()
                startSendLoop()
                mainHandler.post { onOpen() }
            }

            override fun onMessage(ws: WebSocket, text: String) {
                handleIncomingText(text)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                handleIncomingBinary(bytes)
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "Closing: $code / $reason")
                ws.close(1000, null)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "Closed: $code / $reason")
                stopHeartbeat()
                stopSendLoop()
                mainHandler.post { onClosed() }
                attemptReconnect()
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Failure: ${t.message}")
                stopHeartbeat()
                stopSendLoop()
                mainHandler.post { onFailure(t) }
                // Check if token expired (server can send specific code)
                if (response?.code == 401) {
                    mainHandler.post { onTokenExpired() }
                } else {
                    attemptReconnect()
                }
            }
        })
    }

    // --- Send JSON message ---
    fun send(topic: String, json: JSONObject) {
        val message = Message(topic, interceptOutgoing(json), isBinary = false)
        messageQueue.add(message)
    }

    // --- Send binary message ---
    fun sendBinary(topic: String, bytes: ByteString) {
        val message = Message(topic, bytes, isBinary = true)
        messageQueue.add(message)
    }

    // --- Flush queue with throttling & backpressure ---
    private fun flushQueue() {
        val now = System.currentTimeMillis()
        if (messageQueue.isEmpty() || webSocket == null) return
        if (now - lastSentTime < maxSendRate) return

        val msg = messageQueue.poll() ?: return
        try {
            if (msg.isBinary && msg.payload is ByteString) {
                webSocket?.send(msg.payload)
            } else if (!msg.isBinary && msg.payload is JSONObject) {
                webSocket?.send(JSONObject().apply {
                    put("topic", msg.topic)
                    put("payload", msg.payload)
                }.toString())
            }
            lastSentTime = now
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message: ${e.message}")
            // Push back to queue
            messageQueue.add(msg)
        }
    }

    // --- Incoming text ---
    private fun handleIncomingText(message: String) {
        try {
            val json = JSONObject(message)
            val topic = json.optString("topic", "default")
            val payload = interceptIncoming(json.optJSONObject("payload") ?: JSONObject())
            mainHandler.post { onMessageReceived(topic, payload) }
        } catch (e: Exception) {
            Log.e(TAG, "Invalid JSON received: $message")
        }
    }

    // --- Incoming binary ---
    private fun handleIncomingBinary(bytes: ByteString) {
        mainHandler.post { onBinaryReceived("binary", bytes) }
    }

    // --- Interceptor ---
    open fun interceptOutgoing(json: JSONObject): JSONObject = json
    open fun interceptIncoming(json: JSONObject): JSONObject = json

    // --- Heartbeat ---
    private fun startHeartbeat() {
        heartbeatTimer?.cancel()
        heartbeatTimer = fixedRateTimer("heartbeat", true, heartbeatInterval, heartbeatInterval) {
            try {
                webSocket?.send(JSONObject().put("type", "ping").toString())
            } catch (e: Exception) {
                Log.e(TAG, "Heartbeat failed: ${e.message}")
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatTimer?.cancel()
        heartbeatTimer = null
    }

    // --- Send loop ---
    private fun startSendLoop() { mainHandler.post(sendRunnable) }
    private fun stopSendLoop() { mainHandler.removeCallbacks(sendRunnable) }

    // --- Reconnect ---
    private fun attemptReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.e(TAG, "Max reconnect attempts reached")
            return
        }
        reconnectAttempts++
        Log.d(TAG, "Reconnecting... attempt $reconnectAttempts")
        Timer().schedule(object : TimerTask() {
            override fun run() { connect() }
        }, (reconnectAttempts * 2000L))
    }

    // --- Close ---
    fun close() {
        stopHeartbeat()
        stopSendLoop()
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }
}

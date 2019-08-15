package example

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.*
import org.eclipse.jetty.websocket.api.extensions.Frame
import org.eclipse.jetty.websocket.common.frames.PongFrame
import java.nio.ByteBuffer
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@WebSocket
class ClientSocket(private val messageConsumer: (String) -> Unit) {
    private var session: Session? = null
    private val executorService = Executors.newScheduledThreadPool(1)
    private var lastPong: Instant? = null
    private var pingFuture: ScheduledFuture<*>? = null
    private val queue = LinkedBlockingDeque<() -> Unit>()
    private var messageHandlerThread: Thread? = null

    @OnWebSocketClose
    fun onClose(statusCode: Int, reason: String) {
        pingFuture!!.cancel(true)
        this.session = null
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        this.session = session

        pingFuture = executorService.scheduleAtFixedRate({
            session.remote.sendPing(ByteBuffer.wrap("ping".toByteArray()))
        }, 10, 10, TimeUnit.SECONDS)

        val newMessageHandlerThread = Thread {
            try {
                while (true) {
                    queue.take().invoke()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        newMessageHandlerThread.name = "Socket message handler"
        newMessageHandlerThread.start()

        messageHandlerThread = newMessageHandlerThread;
    }

    @OnWebSocketFrame
    fun onFrame(pong: Frame) {
        if (pong is PongFrame) {
            lastPong = Instant.now()
        }
    }

    @OnWebSocketMessage
    fun onMessage(msg: String) {
        queue.add { messageConsumer.invoke(msg) }
    }

    @OnWebSocketError
    fun onError(cause: Throwable) {
        throw RuntimeException(cause)
    }

    fun close() {
        pingFuture!!.cancel(true)
        session!!.close()
        session = null
        messageHandlerThread!!.interrupt()
    }
}
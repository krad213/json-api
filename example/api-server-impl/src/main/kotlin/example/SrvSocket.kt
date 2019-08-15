package example

import org.eclipse.jetty.util.ConcurrentHashSet
import org.eclipse.jetty.util.log.Log
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import su.kore.json.api.server.ServerSocket

@WebSocket
class SrvSocket : ServerSocket {
    private val LOG = Log.getLogger(javaClass)
    private var session: Session? = null;

    private val closeListeners = ConcurrentHashSet<() -> Unit>()
    override fun addCloseListener(listener: () -> Unit) {
        closeListeners.add(listener);
    }

    override fun pushMessage(message: String) {
        val session = this.session
        session?.remote?.sendString(message)
    }

    @OnWebSocketConnect
    fun connected(session: Session) {
        this.session = session
    }

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String) {
        closeListeners.forEach { it.invoke() }
    }

    @OnWebSocketError
    fun onError(error:Throwable) {
        LOG.warn(error)
    }
}
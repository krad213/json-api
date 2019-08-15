package example

import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.util.StringContentProvider
import org.eclipse.jetty.util.log.Log
import org.eclipse.jetty.websocket.client.WebSocketClient
import su.kore.json.api.client.ApiClientProvider
import su.kore.json.api.client.RequestProcessor
import java.net.URI

fun main() {
    val LOG = Log.getLogger("Main")

    val requestProcessor = RequestProcessor()
    val client = WebSocketClient()
    val socket = ClientSocket(requestProcessor::onSocketMessage)
    val httpClient = HttpClient()
    httpClient.start()
    client.start()
    client.connect(socket, URI("ws://localhost:4567/socket"))

    val apiClientProvider = ApiClientProvider(requestProcessor)
    val api = apiClientProvider.createApiProxy(ApiExample::class.java) { urlPath: String, content: String? ->

        if (content != null) {
            httpClient.POST("http://localhost:4567/$urlPath").content(StringContentProvider(content)).send().contentAsString
        } else {
            httpClient.POST("http://localhost:4567/$urlPath").content(StringContentProvider("")).send().contentAsString
        }
    }

    LOG.info(api.str())
    val complexObjects = api.getComplexObjects(10)
    LOG.info("List of complex objects: $complexObjects")
    api.setComplexObject(complexObjects[3])
    api.addComplexObjectListener { LOG.info("Notified: $it") }
    api.startNotify()
}
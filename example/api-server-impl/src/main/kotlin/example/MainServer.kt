package example

import spark.Spark.*
import su.kore.json.api.server.ResponseProcessor
import su.kore.json.api.server.ServerConfigurer

fun main() {
    val serverSocket = SrvSocket()
    val serverConfigurer = ServerConfigurer(ResponseProcessor(serverSocket))
    val apiExample: ApiExample = ApiExampleImpl()

    webSocket("/socket", serverSocket)

    serverConfigurer.configureApiInterface(apiExample) { endPoint ->
        get(endPoint.path) { req, _ ->
            endPoint.call(req.body())
        }

        post(endPoint.path) { req, _ ->
            endPoint.call(req.body())
        }
    }
}
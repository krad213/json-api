package su.kore.json.api.server

class ServerConfigurer(private val responseProcessor: ResponseProcessor) {
    fun configureApiInterface(apiInterface: Any, configurer: (endPoint: EndPoint<Any>) -> Unit) {
        val apiInterfaceClass = findApiInterface(apiInterface::class.java)
        apiInterfaceClass.declaredMethods.forEach {
            val endPoint = EndPoint(apiInterface, it, responseProcessor)
            configurer.invoke(endPoint)
        }
    }
}
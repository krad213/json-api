package su.kore.json.api.server

import su.kore.json.api.common.dto.ListenerTriggerWrapper
import su.kore.json.api.common.dto.ListenerWrapper
import su.kore.json.api.common.util.Mapper
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class ResponseProcessor(private val serverSocket: ServerSocket) {
    init {
        serverSocket.addCloseListener(this::clearListeners)
    }

    private val listenerMap = ConcurrentHashMap<ListenerKey, Any>()


    fun <T : Any> processMethod(endPoint: EndPoint<T>, params: List<Any>): Array<Any> {
        val method = endPoint.method
        val apiInterface = endPoint.apiInterface
        return if (method.name.startsWith("add") && method.getName().endsWith("Listener")) {
            registerListener(apiInterface, params)
        } else if (method.name.startsWith("remove") && method.getName().endsWith("Listener")) {
            unregisterListener(apiInterface, params)
        } else {
            params.toTypedArray()
        }
    }

    private fun registerListener(apiInterface: Any, params: List<Any>): Array<Any> {
        if (params.size != 1) {
            throw RuntimeException("add listener method should have one arg")
        }
        val listenerWrapper = params[0] as ListenerWrapper
        val proxyListener = createProxyListener(listenerWrapper)
        listenerMap[ListenerKey(apiInterface, listenerWrapper.id)] = proxyListener
        return arrayOf(proxyListener)
    }

    private fun unregisterListener(apiInterface: Any, params: List<Any>): Array<Any> {
        if (params.size != 1) {
            throw RuntimeException("add listener method should have one arg")
        }
        val listenerWrapper = params[0] as ListenerWrapper
        val removedListener = listenerMap.remove(ListenerKey(apiInterface, listenerWrapper.id))
        return if (removedListener != null) {
            arrayOf(removedListener)
        } else {
            arrayOf(createDummyProxyListener(listenerWrapper))
        }
    }

    private fun createDummyProxyListener(param: ListenerWrapper): Any {
        val cls = Class.forName(param.className)
        return Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(cls)) { proxy, method, args ->
        }
    }

    private fun createProxyListener(param: ListenerWrapper): Any {
        val cls = Class.forName(param.className)
        return Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(cls)) { proxy, method, args ->
            if ("toString" == method.name && method.parameterCount == 0) {
                return@newProxyInstance cls.name + "-Proxy"
            } else if ("equals" == method.name && method.parameterCount == 1) {
                return@newProxyInstance param.equals(proxy)
            } else if ("hashCode" == method.name && method.parameterCount == 0) {
                return@newProxyInstance param.hashCode()
            } else if (method.declaringClass != cls) {
                return@newProxyInstance method.invoke(proxy, *args)
            } else {
                if (args != null) {
                    serverSocket.pushMessage(Mapper.objToStr(ListenerTriggerWrapper(param.id, method.name, args.asList())))
                } else {
                    serverSocket.pushMessage(Mapper.objToStr(ListenerTriggerWrapper(param.id, method.name, null)))
                }
                return@newProxyInstance null
            }
        }
    }

    private fun clearListeners() {
        listenerMap.forEach { (key, value) ->
            val methodName = "remove" + key.id.split("/")[0]
            key.apiInterface::class.java.getDeclaredMethod(methodName, value.javaClass.interfaces[0]).invoke(key.apiInterface, value)
        }
        listenerMap.clear()
    }

    data class ListenerKey(val apiInterface: Any, val id: String)
}

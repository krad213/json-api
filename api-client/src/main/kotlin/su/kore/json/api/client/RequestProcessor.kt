package su.kore.json.api.client


import su.kore.json.api.common.dto.ListenerTriggerWrapper
import su.kore.json.api.common.dto.ListenerWrapper
import su.kore.json.api.common.util.Mapper
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class RequestProcessor {
    private val idListenerMap = ConcurrentHashMap<String, Any>()
    private val listenerIdMap = ConcurrentHashMap<Any, String>()

    fun processMethod(method: Method, args: Array<Any>?): String? {
        if (args == null) {
            return null
        }
        return if (method.name.startsWith("add") && method.name.endsWith("Listener")) {
            Mapper.objToStr(listOf(registerListener(method, args)))
        } else if (method.name.startsWith("remove") && method.name.endsWith("Listener")) {
            Mapper.objToStr(listOf(unregisterListener(args)))
        } else {
            Mapper.objToStr(args.asList())
        }
    }

    private fun unregisterListener(args: Array<Any>): ListenerWrapper {
        if (args.size != 1) {
            throw RuntimeException("remove listener method should have one arg")
        }
        val arg = args[0]
        val id = listenerIdMap[arg]
        idListenerMap.remove(id)
        listenerIdMap.remove(arg)
        if (id != null) {
            return ListenerWrapper(id, arg.javaClass.interfaces[0].name)
        } else {
            throw ListenerNotFoundException()
        }
    }

    private fun registerListener(method: Method, args: Array<Any>): ListenerWrapper {
        if (args.size != 1) {
            throw RuntimeException("add listener method should have one arg")
        }
        val arg = args[0]
        val id = createListenerId(method)
        idListenerMap[id] = arg
        listenerIdMap[arg] = id
        return ListenerWrapper(id, arg.javaClass.interfaces[0].name)
    }

    private fun createListenerId(method: Method): String {
        return method.name.substring("add".length) + "/" + UUID.randomUUID().toString()
    }

    fun onSocketMessage(message: String) {
        val listenerTriggerWrapper = Mapper.strToObj(message, ListenerTriggerWrapper::class.java)
        val listener = idListenerMap[listenerTriggerWrapper.id] ?: return
        val method = findMethod(listener, listenerTriggerWrapper)
        if (listenerTriggerWrapper.params != null) {
            method.invoke(listener, *listenerTriggerWrapper.params!!.toTypedArray())
        } else {
            method.invoke(listener)
        }
    }

    private fun findMethod(listener: Any, listenerTriggerWrapper: ListenerTriggerWrapper): Method {
        return listener.javaClass.interfaces[0].declaredMethods
                .filter { it.name == listenerTriggerWrapper.methodName }
                .first { matchingParams(it.parameterTypes, listenerTriggerWrapper.params) }
    }

    private fun matchingParams(parameterTypes: Array<Class<*>>, params: List<Any>?): Boolean {
        if (params == null && parameterTypes.size == 0) {
            return true
        }

        if (parameterTypes.size != params!!.size) {
            return false
        }

        for (i in parameterTypes.indices) {
            if (!parameterTypes[i].isAssignableFrom(params[i].javaClass)) {
                return false
            }
        }
        return true
    }
}

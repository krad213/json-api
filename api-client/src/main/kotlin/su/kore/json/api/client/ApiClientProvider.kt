package su.kore.json.api.client

import su.kore.json.api.common.dto.ExceptionWrapper
import su.kore.json.api.common.util.Mapper
import java.lang.reflect.Proxy

class ApiClientProvider(private val requestProcessor: RequestProcessor) {
    fun <T : Any> createApiProxy(interfaceClass: Class<T>, sender: (urlPath: String, content: String?) -> String?): T {
        return Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf<Class<*>>(interfaceClass)) { _, method, args ->
            val result = sender.invoke(interfaceClass.simpleName + "/" + method.name, requestProcessor.processMethod(method, args))
            if (result == null || result.isEmpty()) {
                return@newProxyInstance null
            } else {
                val resultObject: Any = Mapper.strToObj(result)
                if (resultObject is ExceptionWrapper) {
                    val exceptionWrapper = resultObject
                    val apiException = exceptionWrapper.apiException
                    if (apiException != null) {
                        throw apiException
                    } else {
                        throw RuntimeException(exceptionWrapper.message)
                    }
                }
                return@newProxyInstance resultObject
            }
        } as T
    }
}
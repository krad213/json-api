package su.kore.json.api.server

import su.kore.json.api.common.dto.ExceptionWrapper
import su.kore.json.api.common.util.Mapper
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class EndPoint<T : Any>(val apiInterface: T, val method: Method, val responseProcessor: ResponseProcessor) {
    val path: String get() = method.declaringClass.simpleName+ "/" + method.name

    fun call(request: String): String {
        val params = getGetParams(request)
        try {
            val result = if (params != null) {
                method.invoke(apiInterface, *responseProcessor.processMethod(this, params))
            } else {
                method.invoke(apiInterface)
            }
            return Mapper.objToStr(result)
        } catch (ex: InvocationTargetException) {
            return Mapper.objToStr(ExceptionWrapper(ex))
        }
    }


    private fun getGetParams(paramString: String): List<Any>? {
        var params: List<Any>? = null
        if (!paramString.isEmpty()) {
            params = Mapper.strToParams(paramString)
        }
        return params
    }

}

package su.kore.json.api.common.dto

import java.lang.reflect.InvocationTargetException

class ExceptionWrapper {
    var message: String? = null
    var apiException: ApiException? = null

    constructor() {}

    constructor(ex: InvocationTargetException) {
        val targetException = ex.targetException
        if (targetException is ApiException) {
            apiException = targetException
        } else {
            this.message = targetException.message
        }
    }
}

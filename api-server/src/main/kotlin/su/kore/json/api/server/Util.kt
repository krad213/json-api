package su.kore.json.api.server

import su.kore.json.api.common.annotations.JsonApi

fun findApiInterface(clazz: Class<*>): Class<*> {
    return clazz.interfaces.first { it -> it.getAnnotation(JsonApi::class.java) != null };
}
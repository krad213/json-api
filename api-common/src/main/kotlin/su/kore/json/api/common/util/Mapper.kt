package su.kore.json.api.common.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

class Mapper {
    companion object {
        private val MAPPER = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        }

        fun strToParams(str: String): List<Any>? {
            var params: List<Any>? = null
            if (str.isNotEmpty()) {
                params = MAPPER.readValue(str, object : TypeReference<List<Any>>() {})
            }
            return params
        }

        fun objToStr(result: Any?): String {
            return if (result != null) {
                MAPPER.writeValueAsString(result)
            } else {
                ""
            }
        }

        fun <T : Any> strToObj(str: String): T {
            return MAPPER.readValue(str, Any::class.java) as T
        }

        fun <T : Any> strToObj(str: String, clazz: Class<T>): T {
            return MAPPER.readValue(str, clazz) as T
        }
    }
}
package example

import su.kore.json.api.common.annotations.JsonApi;
import java.time.temporal.TemporalAmount

@JsonApi
interface ApiExample {
    fun str(): String
    fun getComplexObjects(amount:Int):List<ComplexObj>
    fun setComplexObject(obj: ComplexObj)
    fun addComplexObjectListener(listener:(ComplexObj)->Unit)
    fun removeComplexObjectListener(listener:(ComplexObj)->Unit)
    fun startNotify()
}
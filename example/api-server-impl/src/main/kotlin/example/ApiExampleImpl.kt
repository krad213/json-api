package example

import org.eclipse.jetty.util.ConcurrentHashSet
import org.eclipse.jetty.util.log.Log

class ApiExampleImpl : ApiExample {
    private val LOG = Log.getLogger(javaClass)
    private val complexObjectsGenerator = ComplexObjectsGenerator()

    private val complexObjListeners = ConcurrentHashSet<(ComplexObj) -> Unit>()

    override fun str(): String {
        return "Simple string"
    }

    override fun getComplexObjects(amount: Int): List<ComplexObj> = IntRange(0, amount - 1).map { complexObjectsGenerator.generate() }.toList()

    override fun setComplexObject(obj: ComplexObj) {
        LOG.info("received:$obj")
    }

    override fun addComplexObjectListener(listener: (ComplexObj) -> Unit) {
        complexObjListeners.add(listener)
    }

    override fun removeComplexObjectListener(listener: (ComplexObj) -> Unit) {
        complexObjListeners.remove(listener)
    }

    fun notifyComplexObject(obj: ComplexObj) {
        complexObjListeners.forEach { it.invoke(obj) }
    }

    override fun startNotify() {
        Thread {
            while (true) {
                notifyComplexObject(complexObjectsGenerator.generate())
                Thread.sleep(1000)
            }
        }.start()
    }
}
package su.kore.json.api.server

interface ServerSocket {
    fun addCloseListener(listener: () -> Unit)
    fun pushMessage(message: String)
}
package mikhail.shell.education.security.server

fun main(args: Array<String>) {
    var type = Protocol.DEFFIE_HELLMAN
    for (i in 0..<args.size) {
        when (args[i]) {
            "--df" -> type = Protocol.DEFFIE_HELLMAN
            "--dfi" -> type = Protocol.DEFFIE_HELLMAN_IMPROVED
            "--mqv" -> type = Protocol.MQV
        }
    }
    val server: BaseServer = when(type) {
        Protocol.DEFFIE_HELLMAN -> DFServer()
        Protocol.DEFFIE_HELLMAN_IMPROVED -> DFIServer()
        else -> MQVServer()
    }
    server.start()
}
enum class Protocol {
    DEFFIE_HELLMAN, DEFFIE_HELLMAN_IMPROVED, MQV
}
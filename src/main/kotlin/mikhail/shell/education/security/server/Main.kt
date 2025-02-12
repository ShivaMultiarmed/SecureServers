package mikhail.shell.education.security.server

fun main(args: Array<String>) {
    var type = Protocol.DEFFIE_HELLMAN
    for (i in 0..<args.size) {
        when (args[i]) {
            "--dh" -> type = Protocol.DEFFIE_HELLMAN
            "--dfi" -> type = Protocol.DEFFIE_HELLMAN_IMPROVED
            "--mqv" -> type = Protocol.MQV
        }
    }
    val server: Server = DFIServer()
    server.start()
}
enum class Protocol {
    DEFFIE_HELLMAN, DEFFIE_HELLMAN_IMPROVED, MQV
}
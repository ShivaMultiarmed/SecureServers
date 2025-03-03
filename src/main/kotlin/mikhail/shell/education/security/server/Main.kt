package mikhail.shell.education.security.server

fun main(args: Array<String>) {
    var type = Protocol.DEFFIE_HELLMAN
    var subType = MathType.ALGEBRAIC
    for (i in 0..<args.size) {
        when (args[i]) {
            "--dh" -> type = Protocol.DEFFIE_HELLMAN
            "--dhi" -> type = Protocol.DEFFIE_HELLMAN_IMPROVED
            "--mqv" -> type = Protocol.MQV
            "--alg" -> subType = MathType.ALGEBRAIC
            "--el" -> subType = MathType.ELLIPTIC
        }
    }
    val server: Server = when(type) {
        Protocol.DEFFIE_HELLMAN -> DFFieldServer()
        Protocol.DEFFIE_HELLMAN_IMPROVED -> when(subType) {
            MathType.ALGEBRAIC -> DFIFieldServer()
            MathType.ELLIPTIC -> DhiEllipticServer()
        }
        Protocol.MQV -> when(subType) {
            MathType.ALGEBRAIC -> MQVFieldServer()
            MathType.ELLIPTIC -> MqvEllipticServer()
        }
    }
    server.start()
}
enum class Protocol {
    DEFFIE_HELLMAN, DEFFIE_HELLMAN_IMPROVED, MQV
}
enum class MathType {
    ALGEBRAIC, ELLIPTIC
}
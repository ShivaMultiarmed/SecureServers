package mikhail.shell.education.security.server.estream

import mikhail.shell.education.security.server.Server

fun main(args: Array<String>) {
    val server: Server = EStreamServer()
    server.start()
}
package mikhail.shell.education.security.server.estream

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import mikhail.shell.education.security.server.Server
import org.slf4j.event.Level

class EStreamServer: Server {
    private companion object {
        const val BUFFER_SIZE = 1024
    }
    private val sessions = mutableSetOf<DefaultWebSocketSession>()
    override fun start() {
        embeddedServer(
            factory = Netty,
            port = 9999
        ) {
            install(WebSockets) {
                masking = false
            }
            routing {
                webSocket("/transfer") {
                    if (!sessions.contains(this) && sessions.size < 2) {
                        sessions.add(this)
                    } else {
                        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room is full."))
                        return@webSocket
                    }
                    while (sessions.size < 2) {
                        delay(1000)
                    }
                    while (sessions.size == 2) {
                        val anotherSession = sessions.first { it != this }
                        val fileSizeFrame = incoming.receive()
                        anotherSession.outgoing.send(fileSizeFrame)
                        val fileSize = (fileSizeFrame as Frame.Text).readText().toInt()
                        val fileNameFrame = incoming.receive()
                        anotherSession.send(fileNameFrame)
                        var bytesLeft = fileSize
                        while (bytesLeft > 0) {
                            val ivFrame = incoming.receive()
                            val byteFrame = incoming.receive()
                            anotherSession.outgoing.send(ivFrame)
                            anotherSession.outgoing.send(byteFrame)
                            bytesLeft -= BUFFER_SIZE
                        }
                    }
                }
            }
        }.start(wait = true)
    }
}
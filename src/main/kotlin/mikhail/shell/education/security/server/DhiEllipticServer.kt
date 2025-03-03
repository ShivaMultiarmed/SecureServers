package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

class DhiEllipticServer: BaseEllipticServer() {
    override fun start() {
        embeddedServer(Netty, port = 9876) {
            install(WebSockets.Plugin) {
                pingPeriod = Duration.ofSeconds(10)
            }
            routing {
                webSocket("/handshake") {
                    val clientName = (incoming.receive() as Frame.Text).readText()
                    sendNumber(p)
                    sendNumber(q)
                    sendNumber(a)
                    sendNumber(b)
                    sendNumber(xG)
                    sendNumber(yG)
                    if (clientName == "CATHY") {
                        xC = receivePublicKey()
                        yC = receivePublicKey()
                        xCdef.complete(xC!!)
                        yCdef.complete(yC!!)
                        xF = xFdef.await()
                        yF = yFdef.await()
                        sendNumber(xF!!)
                        sendNumber(yF!!)
                    } else if (clientName == "FRED") {
                        xF = receivePublicKey()
                        yF = receivePublicKey()
                        xFdef.complete(xF!!)
                        yFdef.complete(yF!!)
                        xC = xCdef.await()
                        yC = yCdef.await()
                        sendNumber(xC!!)
                        sendNumber(yC!!)
                    }
                }
            }
        }.start(wait = true)
    }
}
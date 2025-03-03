package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

class DiffieHellmanEllipticServer: BaseEllipticServer() {
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
                        C = receivePublicKey()
                        cathyDeferred.complete(C!!)
                        F = fredDeferred.await()
                        sendNumber(F!!)
                    } else if (clientName == "FRED") {
                        F = receivePublicKey()
                        fredDeferred.complete(F!!)
                        C = cathyDeferred.await()
                        sendNumber(C!!)
                    }
                }
            }
        }
    }
}
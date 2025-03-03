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
                        xC = receivePublicKey()
                        xCathyDeferred.complete(xC!!)
                        yC = receivePublicKey()
                        yCathyDeferred.complete(yC!!)
                        xF = xFredDeferred.await()
                        sendNumber(xF!!)
                        yF = yFredDeferred.await()
                        sendNumber(yF!!)
                    } else if (clientName == "FRED") {
                        xF = receivePublicKey()
                        xFredDeferred.complete(xF!!)
                        yC = receivePublicKey()
                        yCathyDeferred.complete(yF!!)
                        xC = xCathyDeferred.await()
                        sendNumber(xC!!)
                        yC = yCathyDeferred.await()
                        sendNumber(yC!!)
                    }
                }
            }
        }
    }
}
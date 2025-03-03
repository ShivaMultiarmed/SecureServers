package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.math.BigInteger
import java.time.Duration

class DFIFieldServer: BaseFieldServer() {
    private val q: BigInteger = generatePrime(256)
    init {
        p = generateSafePrime(q)
        g = findGenerator(p, q)
    }
    override fun start() {
        embeddedServer(Netty, port = 9876) {
            install(WebSockets.Plugin) {
                pingPeriod = Duration.ofSeconds(10)
            }
            routing {
                webSocket("/handshake") {
                    val clientName = (incoming.receive() as Frame.Text).readText()
                    sendNumber(q)
                    sendNumber(p)
                    sendNumber(g)
                    if (clientName == "ALICE") {
                        X = receivePublicKey()
                        aliceKeyDeferred.complete(X!!)
                        Y = bobKeyDeferred.await()
                        sendNumber(Y!!)
                    } else if (clientName == "BOB") {
                        Y = receivePublicKey()
                        bobKeyDeferred.complete(Y!!)
                        X = aliceKeyDeferred.await()
                        sendNumber(X!!)
                    }
                }
            }

        }.start(wait = true)
    }
}
package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import java.math.BigInteger
import java.time.Duration

class MQVServer: BaseServer() {
    private val aliceSessionKeyDeferred = CompletableDeferred<BigInteger>()
    private val bobSessionKeyDeferred = CompletableDeferred<BigInteger>()
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
                    if (clientName == "ALICE") {
                        sendNumber(q)
                        sendNumber(p)
                        sendNumber(g)

                        X = receivePublicKey()
                        aliceKeyDeferred.complete(X!!)
                        Y = bobKeyDeferred.await()
                        sendNumber(Y!!)

                        val A = receivePublicKey()
                        aliceSessionKeyDeferred.complete(A)
                        val B = bobSessionKeyDeferred.await()
                        sendNumber(B)
                    } else if (clientName == "BOB") {
                        sendNumber(q)
                        sendNumber(p)
                        sendNumber(g)

                        Y = receivePublicKey()
                        bobKeyDeferred.complete(Y!!)
                        X = aliceKeyDeferred.await()
                        sendNumber(X!!)

                        val B = receivePublicKey()
                        bobSessionKeyDeferred.complete(B)
                        val A = aliceSessionKeyDeferred.await()
                        sendNumber(A)
                    }
                }
            }

        }.start(wait = true)
    }
}
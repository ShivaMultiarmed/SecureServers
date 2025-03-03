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

class MqvEllipticServer: BaseEllipticServer() {
    var xU: BigInteger? = null
    var yU: BigInteger? = null
    var xV: BigInteger? = null
    var yV: BigInteger? = null
    val xUdef = CompletableDeferred<BigInteger>()
    val yUdef = CompletableDeferred<BigInteger>()
    val xVdef = CompletableDeferred<BigInteger>()
    val yVdef = CompletableDeferred<BigInteger>()
    override fun start() {
        embeddedServer(Netty, port = 9876){
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
                        xU = receivePublicKey()
                        yU = receivePublicKey()
                        xUdef.complete(xU!!)
                        yUdef.complete(yU!!)
                        xV = xVdef.await()
                        yV = yVdef.await()
                        sendNumber(xV!!)
                        sendNumber(yV!!)
                    } else if (clientName == "FRED") {
                        xF = receivePublicKey()
                        yF = receivePublicKey()
                        xFdef.complete(xF!!)
                        yFdef.complete(yF!!)
                        xC = xCdef.await()
                        yC = yCdef.await()
                        sendNumber(xC!!)
                        sendNumber(yC!!)
                        xV = receivePublicKey()
                        yV = receivePublicKey()
                        xVdef.complete(xV!!)
                        yVdef.complete(yV!!)
                        xU = xUdef.await()
                        yU = yUdef.await()
                        sendNumber(xU!!)
                        sendNumber(yU!!)
                    }
                }
            }
        }.start(wait = true)
    }
}
package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import java.math.BigInteger
import java.time.Duration

open class MqvEllipticServer: BaseEllipticServer() {
    var xU: BigInteger? = null
    var yU: BigInteger? = null
    var xV: BigInteger? = null
    var yV: BigInteger? = null
    val xUdef = CompletableDeferred<BigInteger>()
    val yUdef = CompletableDeferred<BigInteger>()
    val xVdef = CompletableDeferred<BigInteger>()
    val yVdef = CompletableDeferred<BigInteger>()
    protected val userIDset = mutableSetOf<String>()
    override fun start() {
        embeddedServer(Netty, port = 9876, host = "0.0.0.0"){
            install(WebSockets.Plugin) {
                pingPeriod = Duration.ofSeconds(10)
            }
            routing {
                setupEndpoints(this)
            }
        }.start(wait = true)
    }

    protected open fun setupEndpoints(route: Route) {
        route.webSocket("/handshake") {
            if (userIDset.size == 2) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room is full."))
                return@webSocket
            }
            val clientID = (incoming.receive() as Frame.Text).readText()
            userIDset.add(clientID)
            while(userIDset.size < 2) {
                delay(1000)
            }
            sendNumber(p)
            sendNumber(q)
            sendNumber(a)
            sendNumber(b)
            sendNumber(xG)
            sendNumber(yG)
            if (clientID == userIDset.first()) {
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
            } else if (clientID == userIDset.last()) {
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
}
package mikhail.shell.education.security.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import java.math.BigInteger
import java.nio.ByteBuffer
import java.time.Duration

abstract class Server {
    private val q: BigInteger = generatePrime(256)
    protected lateinit var p: BigInteger
    protected lateinit var g: BigInteger
    protected var X: BigInteger? = null
    protected var Y: BigInteger? = null

    protected val aliceKeyDeferred = CompletableDeferred<BigInteger>()
    protected val bobKeyDeferred = CompletableDeferred<BigInteger>()

    abstract fun start()

    protected suspend fun WebSocketSession.receivePublicKey(): BigInteger {
        return BigInteger((incoming.receive() as Frame.Binary).readBytes())
    }

    protected suspend fun WebSocketSession.sendNumber(number: BigInteger) {
        send(
            Frame.Binary(
                true,
                ByteBuffer.wrap(number.toByteArray())
            )
        )
    }
}



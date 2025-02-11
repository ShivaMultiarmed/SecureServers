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
import java.util.concurrent.CountDownLatch

class Server {
    private val q: BigInteger = generatePrime(256)
    private val p: BigInteger = generateSafePrime(q)
    private val g: BigInteger = findGenerator(p, q)
    private var X: BigInteger? = null
    private var Y: BigInteger? = null

    private var aliceSession: WebSocketSession? = null
    private var bobSession: WebSocketSession? = null
    private var isAliceConnected: Boolean = false
    private var isBobConnected: Boolean = false
    private val aliceKeyDeferred = CompletableDeferred<BigInteger>()
    private val bobKeyDeferred = CompletableDeferred<BigInteger>()
    private val aliceLatch = CountDownLatch(1)
    private val bobLatch = CountDownLatch(1)
    fun start() {
        embeddedServer(Netty, port = 9876) {
            install(WebSockets.Plugin) {
                pingPeriod = Duration.ofSeconds(10)
            }
            routing {
                webSocket("/handshake") {
                    val clientName = (incoming.receive() as Frame.Text).readText()
                    if (clientName == "ALICE") {
                        aliceSession = this

                        sendNumber(q)
                        sendNumber(p)
                        sendNumber(g)

                        // Получаем публичный ключ от ALICE
                        X = receivePublicKey()
                        aliceKeyDeferred.complete(X!!)
                        // Ждем публичного ключа от BOB
                        Y = bobKeyDeferred.await()
                        // Отправляем ALICE публичный ключ BOB
                        sendNumber(Y!!)
                    } else if (clientName == "BOB") {
                        bobSession = this

                        sendNumber(q)
                        sendNumber(p)
                        sendNumber(g)

                        // Получаем публичный ключ от BOB
                        Y = receivePublicKey()
                        bobKeyDeferred.complete(Y!!)
                        // Ждем публичного ключа от ALICE
                        X = aliceKeyDeferred.await()
                        // Отправляем BOB публичный ключ ALICE
                        sendNumber(X!!)
                    }
                }
            }

        }.start(wait = true)
    }
}

private suspend fun WebSocketSession.receivePublicKey(): BigInteger {
    return BigInteger((incoming.receive() as Frame.Binary).readBytes())
}

private suspend fun WebSocketSession.sendNumber(number: BigInteger) {
    send(
        Frame.Binary(
            true,
            ByteBuffer.wrap(number.toByteArray())
        )
    )
}

package mikhail.shell.education.security.server

import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import java.math.BigInteger
import java.nio.ByteBuffer

abstract class BaseEllipticServer: Server {
    protected val p = BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")
    protected val a = BigInteger("-3")
    protected val b = BigInteger("0x5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16)
    protected val q = BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369")
    protected val xG = BigInteger("0x6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16)
    protected val yG = BigInteger("0x4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16)
    var xC: BigInteger? = null
    var yC: BigInteger? = null
    var xF: BigInteger? = null
    var yF: BigInteger? = null
    val xCathyDeferred = CompletableDeferred<BigInteger>()
    val yCathyDeferred = CompletableDeferred<BigInteger>()
    val xFredDeferred = CompletableDeferred<BigInteger>()
    val yFredDeferred = CompletableDeferred<BigInteger>()

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

package mikhail.shell.education.security.server

import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import java.math.BigInteger
import java.nio.ByteBuffer

abstract class BaseEllipticServer: Server {
    private val p = BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")
    private val a = BigInteger("-3")
    private val b = BigInteger("0x5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16)
    private val q = BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369")
    private val xG = BigInteger("0x6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16)
    private val yG = BigInteger("0x4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16)
    val cathyDeferred = CompletableDeferred<BigInteger>()
    val fredDeferred = CompletableDeferred<BigInteger>()

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

fun Pair<BigInteger, BigInteger>.evaluateCompositionByTwo(modNumber: BigInteger, a: BigInteger): Pair<BigInteger, BigInteger> {
    return this.evaluateComposition(this, modNumber, a)
}

fun Pair<BigInteger, BigInteger>.evaluateComposition(
    other: Pair<BigInteger, BigInteger>,
    modNumber: BigInteger,
    a: BigInteger? = null
):Pair<BigInteger, BigInteger> {
    val (x1, y1) = this
    if (this == other) {
        val numerator = (BigInteger("3") * x1.modPow(BigInteger.TWO, modNumber) + a!!).mod(modNumber)
        val denominator = (BigInteger.TWO * y1).mod(modNumber)
        val denominatorInversed = denominator.modInverse(modNumber)
        val k = (numerator * denominatorInversed).mod(modNumber)
        val x3 = (k.modPow(BigInteger.TWO, modNumber) - BigInteger.TWO * x1).mod(modNumber)
        val y3 = (k * (x1 - x3) - y1).mod(modNumber)
        return x3 to y3
    } else {
        val (x2, y2) = other
        val numerator = (y2 - y1).mod(modNumber)
        val denominator = (x2 - x1).mod(modNumber)
        val denominatorInversed = denominator.modInverse(modNumber)
        val k = (numerator * denominatorInversed).mod(modNumber)
        val x3 = (k.pow(2) - x1 - x2).mod(modNumber)
        val y3 = (k * (x1 - x3) - y1).mod(modNumber)
        return x3 to y3
    }
}

fun Pair<BigInteger, BigInteger>.evaluateComposition(n: BigInteger, modNumber: BigInteger, a: BigInteger): Pair<BigInteger, BigInteger> {
    var result: Pair<BigInteger, BigInteger>? = null
    var addend: Pair<BigInteger, BigInteger>? = this

    var k = n
    while (k > BigInteger.ZERO) {
        if (k.testBit(0)) {
            result = if (result == null) addend
            else result.evaluateComposition(addend!!, modNumber)
        }
        addend = addend?.evaluateCompositionByTwo(modNumber, a)
        k = k.shiftRight(1)
    }
    return result ?: (BigInteger.ZERO to BigInteger.ZERO)
}

fun BigInteger.getBit(n: BigInteger): BigInteger {
    return this shr n.toInt() and BigInteger.ONE
}
package mikhail.shell.education.security.server

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class EllipticFunctionsTests {
    @Test
    fun testGetNthBit() {
        val value = BigInteger("10344")
        val expectedBits = arrayOf(1, 0, 1, 0, 0, 0, 0,1 ,1, 0, 1,0, 0,0).map { it.toBigInteger() }.toTypedArray().reversedArray()
        for (i in expectedBits.size - 1 downTo 0) {
            val actualBit = value.getBit(i.toBigInteger())
            Assertions.assertEquals(expectedBits[i], actualBit)
        }
    }

    @Test
    fun testComposition() {
        val a = BigInteger("5") to BigInteger.ONE
        val b = BigInteger("4") to BigInteger("6")
        val expected = BigInteger.TWO to BigInteger("5")
        val actual = a.evaluateComposition(b, BigInteger("7"))
        Assertions.assertEquals(expected, actual)
    }
    @Test
    fun testMultipleComposition() {
        val n = BigInteger("3")
        val point = BigInteger("5") to BigInteger.ONE
        val expected = BigInteger.TWO to BigInteger("5")
        val actual = point.evaluateComposition(n, BigInteger("7"), BigInteger("2"))
        Assertions.assertEquals(expected, actual)
    }
    @Test
    fun testDiffieHellmanEllipticKeyGeneration() {
        val p = BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")
         val a = BigInteger("-3")
         val b = BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16)
         val q = BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369")
         val xG = BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16)
         val yG = BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16)
        val G = xG to yG
        val c = generateSecretKey(q)
        val f = generateSecretKey(q)
        println("c = $c")
        println("f = $f")
        val C = G.evaluateComposition(c, p, a)
        val F = G.evaluateComposition(f, p, a)
        println("C = $C")
        println("F = $F")
        val Sc = C.evaluateComposition(f, p, a)
        val Sf = F.evaluateComposition(c, p, a)
        println("Sc = $Sc")
        println("Sf = $Sf")
        Assertions.assertEquals(Sc, Sf)
    }
}
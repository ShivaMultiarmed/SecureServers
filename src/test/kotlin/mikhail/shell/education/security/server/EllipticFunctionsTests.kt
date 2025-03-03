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
}
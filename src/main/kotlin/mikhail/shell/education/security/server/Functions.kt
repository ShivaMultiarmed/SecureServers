package mikhail.shell.education.security.server

import java.math.BigInteger
import java.security.SecureRandom

val random = SecureRandom()

fun generateSecretKey(q: BigInteger): BigInteger {
    var secret: BigInteger
    do {
        secret = BigInteger(q.bitLength(), random)
    } while (secret < BigInteger.ONE || secret >= q)
    return secret
}

// Генерирует простое число длины bits
fun generatePrime(bits: Int): BigInteger {
    return BigInteger(bits, 100, random)
}

// Генерирует 1024-bit простое число p = kq + 1
fun generateSafePrime(q: BigInteger): BigInteger {
    var p: BigInteger
    do {
        val k = BigInteger(
            768,
            random
        ).setBit(767) // Гарантирует единицу в самом старшем бите, а следовательно число становится достаточно большим.
        p = k.multiply(q).add(BigInteger.ONE)
    } while (!p.isProbablePrime(100))
    return p
}

// Ищет генератор для подгруппы порядка q
fun findGenerator(p: BigInteger, q: BigInteger): BigInteger {
    // t = (p-1)/q
    val t = (p - BigInteger.ONE) / q
    val maxAttempts = 1_000_000
    for (i in 0 until maxAttempts) {
        // Выбираем случайное число r в диапазоне [2, p-2].
        val r = (BigInteger(p.bitLength(), random) % (p - BigInteger.valueOf(3))) + BigInteger.TWO
        // Вычисляем g = r^t mod p. Тогда g принадлежит подгруппе порядка q, поскольку g^q = r^(t*q) = r^(p-1) ≡ 1 mod p.
        val g = r.modPow(t, p)
        // Если g не равно 1, то оно является нетривиальным элементом подгруппы (а в циклической подгруппе порядка q любой нетривиальный элемент является её генератором).
        if (g != BigInteger.ONE) {
            return g
        }
    }
    throw RuntimeException("Не удалось найти генератор циклической подгруппы порядка q за $maxAttempts попыток")
}
fun findGenerator(p: BigInteger): BigInteger {
    val one = BigInteger.ONE
    val two = BigInteger.TWO
    // Вычисляем q = (p - 1) / 2
    val q = p - one / two

    var g = two
    // Перебираем кандидатов g от 2 до p-1
    while (g < p) {
        // Проверяем оба условия:
        // 1. g^q mod p != 1 (исключаем порядок, делящий q)
        // 2. g^2 mod p != 1 (исключаем порядок 2)
        if (g.modPow(q, p) != one && g.modPow(two, p) != one) {
            return g
        }
        g += one
    }
    throw RuntimeException("Не удалось найти примитивный корень для p = $p")
}
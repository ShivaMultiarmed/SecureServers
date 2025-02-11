package mikhail.shell.security.education.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.PrintWriter
import java.math.BigInteger
import java.net.ServerSocket
import java.net.Socket

class Server {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val q: BigInteger = generatePrime(256)
    private val p: BigInteger = generateSafePrime(q)
    private val g: BigInteger = findGenerator(p, q)
    lateinit var X: BigInteger
    lateinit var Y: BigInteger
    private val PORT1 = 12345
    private val PORT2 = 12346
    private val serverSocket1 = ServerSocket(PORT1)
    private val serverSocket2 = ServerSocket(PORT2)
    private lateinit var clientSocket1: Socket
    private lateinit var clientSocket2: Socket
    private lateinit var reader1: BufferedReader
    private lateinit var reader2: BufferedReader
    private lateinit var writer1: PrintWriter
    private lateinit var writer2: PrintWriter
    init {
        scope.launch {
            clientSocket1 = serverSocket1.accept()
            reader1 = BufferedReader(clientSocket1.getInputStream().reader())
            val request = reader1.readLine()
            if (request == "CONNECT") {
                shareData(12345)
            }
        }
        scope.launch {
            clientSocket2 = serverSocket2.accept()
            reader2 = BufferedReader(clientSocket2.getInputStream().reader())
            reader2.readLine()
            val request = reader2.readLine()
            if (request == "CONNECT") {
                shareData(12346)
            }
        }
    }
    fun shareData(port: Int) {
        if (port == 12345) {
            writer1 = PrintWriter(clientSocket1.getOutputStream(), true)
            writer1.println(q)
            writer1.println(p)
            writer1.println(g)
            exchangeData()
        } else if (port == 12346) {
            writer2 = PrintWriter(clientSocket2.getOutputStream(), true)
            writer2.println(q)
            writer2.println(p)
            writer2.println(g)
            exchangeData()
        }
    }
    fun exchangeData() {
        scope.launch {
            val request = reader1.readLine()
            if (request == "HANDSHAKE") {
                X = reader1.readLine().toBigInteger()
                writer2.println(X)
            }
        }
        scope.launch {
            val request = reader2.readLine()
            if (request == "HANDSHAKE") {
                Y = reader2.readLine().toBigInteger()
                writer1.println(Y)
            }
        }
    }
}
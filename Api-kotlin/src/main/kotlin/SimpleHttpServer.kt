import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random
import org.json.JSONObject

data class User(
    val id: Int,
    val dtNascimento: String,
    val email: String,
    val nome: String,
    val telefone: String,
    val password: String
)

data class ContaEnergia(
    val id: Int,
    val value: String,
    val month: String,
    val notes: String
)

val users = mutableListOf<User>()
val contas = mutableListOf<ContaEnergia>()

fun main() {
    val serverSocket = ServerSocket(8080)
    println("Servidor rodando na porta 8080")

    while (true) {
        val clientSocket = serverSocket.accept()
        handleClient(clientSocket)
    }
}

fun handleClient(clientSocket: Socket) {
    try {
        val inputStream = clientSocket.getInputStream()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val outputStream = clientSocket.getOutputStream()
        val writer = PrintWriter(outputStream, true)

        val requestLine = reader.readLine()
        println("Recebido: $requestLine")

        val (method, path) = parseRequest(requestLine)

        var line: String?
        var contentLength = 0
        while (reader.readLine().also { line = it } != null) {
            if (line!!.isEmpty()) break
            if (line!!.startsWith("Content-Length:")) {
                contentLength = line!!.substringAfter(":").trim().toInt()
            }
        }

        val body = StringBuilder()
        if (contentLength > 0) {
            val buffer = CharArray(contentLength)
            reader.read(buffer, 0, contentLength)
            body.append(buffer)
        }

        when (method) {
            "GET" -> {
                when {
                    path == "/user" -> {
                        writer.println("HTTP/1.1 200 OK")
                        writer.println("Content-Type: application/json")
                        writer.println()
                        writer.println(users.joinToString(prefix = "[", postfix = "]") {
                            """
                            {
                                "id": ${it.id},
                                "dtNascimento": "${it.dtNascimento}",
                                "email": "${it.email}",
                                "nome": "${it.nome}",
                                "telefone": "${it.telefone}",
                                "password": "${it.password}"
                            }
                            """.trimIndent()
                        })
                    }
                    path == "/conta" -> {
                        writer.println("HTTP/1.1 200 OK")
                        writer.println("Content-Type: application/json")
                        writer.println()
                        writer.println(contas.joinToString(prefix = "[", postfix = "]") {
                            """
                            {
                                "id": ${it.id},
                                "value": "${it.value}",
                                "month": "${it.month}",
                                "notes": "${it.notes}"
                            }
                            """.trimIndent()
                        })
                    }
                    else -> {
                        writer.println("HTTP/1.1 404 Not Found")
                    }
                }
            }
            "POST" -> {
                if (path == "/user") {
                    val newUserData = parseUserJson(body.toString())
                    val newUser = User(
                        id = Random.nextInt(),
                        dtNascimento = newUserData.dtNascimento,
                        email = newUserData.email,
                        nome = newUserData.nome,
                        telefone = newUserData.telefone,
                        password = newUserData.password
                    )
                    users.add(newUser)

                    writer.println("HTTP/1.1 201 Created")
                    writer.println("Content-Type: application/json")
                    writer.println()
                    writer.println(
                        """
                        {
                            "id": ${newUser.id},
                            "dtNascimento": "${newUser.dtNascimento}",
                            "email": "${newUser.email}",
                            "nome": "${newUser.nome}",
                            "telefone": "${newUser.telefone}",
                            "password": "${newUser.password}"
                        }
                        """.trimIndent()
                    )
                } else if (path == "/conta") {
                    val newContaData = parseContaEnergiaJson(body.toString())
                    val newConta = ContaEnergia(
                        id = Random.nextInt(),
                        value = newContaData.value,
                        month = newContaData.month,
                        notes = newContaData.notes
                    )
                    contas.add(newConta)

                    writer.println("HTTP/1.1 201 Created")
                    writer.println("Content-Type: application/json")
                    writer.println()
                    writer.println(
                        """
                        {
                            "id": ${newConta.id},
                            "value": "${newConta.value}",
                            "month": "${newConta.month}",
                            "notes": "${newConta.notes}"
                        }
                        """.trimIndent()
                    )
                }
            }
            "PUT" -> {
                if (path.startsWith("/user/")) {
                    val id = path.split("/").last().toInt()
                    val updatedUserData = parseUserJson(body.toString())

                    val userIndex = users.indexOfFirst { it.id == id }
                    if (userIndex != -1) {
                        users[userIndex] = users[userIndex].copy(
                            dtNascimento = updatedUserData.dtNascimento,
                            email = updatedUserData.email,
                            nome = updatedUserData.nome,
                            telefone = updatedUserData.telefone,
                            password = updatedUserData.password
                        )

                        writer.println("HTTP/1.1 200 OK")
                        writer.println("Content-Type: application/json")
                        writer.println()
                        writer.println(
                            """
                            {
                                "id": $id,
                                "dtNascimento": "${updatedUserData.dtNascimento}",
                                "email": "${updatedUserData.email}",
                                "nome": "${updatedUserData.nome}",
                                "telefone": "${updatedUserData.telefone}",
                                "password": "${updatedUserData.password}"
                            }
                            """.trimIndent()
                        )
                    } else {
                        writer.println("HTTP/1.1 404 Not Found")
                    }
                } else if (path.startsWith("/conta/")) {
                    val id = path.split("/").last().toInt()
                    val updatedContaData = parseContaEnergiaJson(body.toString())

                    val contaIndex = contas.indexOfFirst { it.id == id }
                    if (contaIndex != -1) {
                        contas[contaIndex] = contas[contaIndex].copy(
                            value = updatedContaData.value,
                            month = updatedContaData.month,
                            notes = updatedContaData.notes
                        )

                        writer.println("HTTP/1.1 200 OK")
                        writer.println("Content-Type: application/json")
                        writer.println()
                        writer.println(
                            """
                            {
                                "id": $id,
                                "value": "${updatedContaData.value}",
                                "month": "${updatedContaData.month}",
                                "notes": "${updatedContaData.notes}"
                            }
                            """.trimIndent()
                        )
                    } else {
                        writer.println("HTTP/1.1 404 Not Found")
                    }
                }
            }
            "DELETE" -> {
                if (path.startsWith("/user/")) {
                    val id = path.split("/").last().toInt()
                    val userIndex = users.indexOfFirst { it.id == id }
                    if (userIndex != -1) {
                        users.removeAt(userIndex)

                        writer.println("HTTP/1.1 204 No Content")
                        writer.println("Content-Length: 0")
                        writer.println()

                        println("Usuário com ID $id removido com sucesso.")
                    } else {
                        writer.println("HTTP/1.1 404 Not Found")
                        writer.println("Content-Length: 0")
                        writer.println()

                        println("Usuário com ID $id não encontrado.")
                    }
                } else if (path.startsWith("/conta/")) {
                    val id = path.split("/").last().toInt()
                    val contaIndex = contas.indexOfFirst { it.id == id }
                    if (contaIndex != -1) {
                        contas.removeAt(contaIndex)

                        writer.println("HTTP/1.1 204 No Content")
                        writer.println("Content-Length: 0")
                        writer.println()

                        println("Conta com ID $id removida com sucesso.")
                    } else {
                        writer.println("HTTP/1.1 404 Not Found")
                        writer.println("Content-Length: 0")
                        writer.println()

                        println("Conta com ID $id não encontrada.")
                    }
                }
            }
            else -> {
                writer.println("HTTP/1.1 405 Method Not Allowed")
            }
        }

        clientSocket.close()
    } catch (e: Exception) {
    }
}

fun parseUserJson(body: String): User {
    val jsonObject = JSONObject(body)
    return User(
        id = 0,
        dtNascimento = jsonObject.getString("dtNascimento"),
        email = jsonObject.getString("email"),
        nome = jsonObject.getString("nome"),
        telefone = jsonObject.getString("telefone"),
        password = jsonObject.getString("password")
    )
}

fun parseContaEnergiaJson(body: String): ContaEnergia {
    val jsonObject = JSONObject(body)
    return ContaEnergia(
        id = 0, // O ID será gerado dinamicamente para novos objetos
        value = jsonObject.getString("value"),
        month = jsonObject.getString("month"),
        notes = jsonObject.getString("notes")
    )
}

fun parseRequest(requestLine: String): Pair<String, String> {
    val parts = requestLine.split(" ")
    return Pair(parts[0], parts[1])
}

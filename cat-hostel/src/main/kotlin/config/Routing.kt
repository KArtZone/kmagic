package pro.artkart.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import pro.artkart.db.CatsTable

val log = KotlinLogging.logger { }

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }

        get("/status") {

            HttpClient(CIO)
                .get("https://cat-hostel-provider.free.beeceptor.com/test")
                .let { response ->
                    log.info { "Got response with status: ${response.status}" }
                    call.respond(
                        mapOf("status" to "Ok", "response" to response.bodyAsText())
                    )
                }
        }

        post("cats") {
            call.receiveParameters().let { parameters ->
                transaction {
                    CatsTable.insertAndGetId { cat ->
                        cat[CatsTable.name] = requireNotNull(parameters["name"])
                        cat[CatsTable.age] = parameters["age"]?.toInt() ?: 0
                    }
                }
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}

package pro.artkart.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import pro.artkart.service.CatService
import pro.artkart.service.DefaultCatService

val log = KotlinLogging.logger { }

fun Application.configureRouting() {
    routing {
        cats(DefaultCatService())
    }
}

fun Routing.cats(service: CatService) = route("/cats") {
    get {
        val cats = service.findAll()
            .also { log.info { "Fetched ${it.size}" } }
        call.respond(cats)
    }
    get("/{id}") {
        val cat = service.find(requireNotNull(call.parameters["id"]).toInt())
            .also { log.info { "Fetched cat: $it" } }

        call.respond(cat ?: HttpStatusCode.NotFound)
    }
    post {
        val parameters = call.receiveParameters()
        val created = service.create(
            requireNotNull(parameters["name"]),
            parameters["age"]?.toInt() ?: 0
        ).also { log.info { "Cat with id: $it has been saved" } }
        call.respond(HttpStatusCode.Created, created.value)
    }

    delete("/{id}") {
        val id = requireNotNull(call.parameters["id"]).toInt()
        log.info { "Trying to delete cat with id: $id" }
        call.respond(
            when {
                service.delete(id) -> HttpStatusCode.OK
                else -> HttpStatusCode.NotFound
            }
        )
    }

    put("/{id}") {
        with(call.receiveParameters()) {
            when {
                service.update(
                    requireNotNull(call.parameters["id"]).toInt(),
                    requireNotNull(this["name"]),
                    this["age"]?.toInt() ?: 0
                ) -> HttpStatusCode.OK

                else -> HttpStatusCode.NotFound
            }
        }.let { code ->
            call.respond(code)
        }
    }
}

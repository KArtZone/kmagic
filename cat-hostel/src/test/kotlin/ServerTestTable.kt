package pro.artkart

import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import pro.artkart.config.DB
import pro.artkart.config.configureNegotiation
import pro.artkart.config.configureRouting
import pro.artkart.db.CatsTable
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTestTable {


    @BeforeAll
    fun setUp() {
        DB.connect()
        transaction {
            SchemaUtils.create(CatsTable)
        }
    }

    @AfterAll
    fun cleanUp() {
        DB.connect()
        transaction {
            SchemaUtils.drop(CatsTable)
        }
    }

    @Test
    fun `route should return Hello, World!`() = test {
        client.get("/").let {
            it.status shouldBe HttpStatusCode.OK
            it.bodyAsText() shouldBe "Hello, World!"
        }
    }

    @Test
    fun `POST creates new cat`() = test {
        val response = client.post("/cats") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "name" to "Meatloaf",
                    "age" to 4.toString()
                ).formUrlEncode()
            )
        }.let {
            assertEquals(HttpStatusCode.Created, it.status)
        }
    }
}

fun test(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
    application {
        configureRouting()
        configureNegotiation()
    }
    block()
}

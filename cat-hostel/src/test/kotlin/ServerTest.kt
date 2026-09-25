package pro.artkart

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import pro.artkart.config.CatsTable
import pro.artkart.config.DB
import pro.artkart.config.myModule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    @AfterAll
    fun cleanup() {
        DB.connect()
        transaction {
            SchemaUtils.drop(CatsTable)
        }
    }

    @BeforeAll
    fun setup() {
        DB.connect()
        transaction {
            SchemaUtils.create(CatsTable)
        }
    }

    @Nested
    inner class CatTest {

        private lateinit var id: EntityID<Int>

        @BeforeEach
        fun setup() {
            DB.connect()
            id = transaction {
                CatsTable.insertAndGetId {
                    it[CatsTable.name] = "Fluffy"
                    it[CatsTable.age] = 2
                }
            }
        }

        @AfterEach
        fun teardown() {
            DB.connect()
            transaction {
                CatsTable.deleteAll()
            }
        }

        @Test
        fun `Get without ID fetches all cats`() = testApplication {
            application {
                myModule()
            }
            assertEquals(
                """[{"id":$id,"name":"Fluffy","age":2}]""",
                client.get(BASE_URL).bodyAsText()
            )
        }

        @Test
        fun `Get with ID fetches a single cat`() = testApplication {
            application {
                myModule()
            }
            assertEquals(
                """{"id":$id,"name":"Fluffy","age":2}""",
                client.get("$BASE_URL/$id").bodyAsText()
            )
        }

        @Test
        fun `POST creates a new cat`() {
            testApplication {
                application {
                    myModule()
                }
                val response = client.post(BASE_URL) {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(
                        listOf(
                            "name" to "Meatloaf",
                            "age" to 4.toString()
                        ).formUrlEncode()
                    )
                }
                assertEquals(HttpStatusCode.Created, response.status)
            }
        }

        @Test
        fun `DELETE deletes the cat with the id`() = testApplication {
            application {
                myModule()
            }
            assertEquals(
                HttpStatusCode.OK,
                client.delete("$BASE_URL/$id").status
            )

            assertEquals(
                HttpStatusCode.NotFound,
                client.delete("$BASE_URL/$id").status
            )
        }

        @Test
        fun `UPDATE updates the cat with the id`() = testApplication {
            application {
                myModule()
            }
            val response = client.put("$BASE_URL/$id") {
                header(
                    HttpHeaders.ContentType,
                    ContentType.Application.FormUrlEncoded
                )
                setBody(
                    listOf(
                        "name" to "Meatloaf",
                        "age" to 4.toString()
                    ).formUrlEncode()
                )
            }
            assertEquals(
                HttpStatusCode.OK,
                response.status
            )
        }
    }

    companion object {
        const val BASE_URL = "/cats"
    }
}

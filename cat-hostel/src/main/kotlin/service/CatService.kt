package pro.artkart.service

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import pro.artkart.config.CatDto
import pro.artkart.config.CatsTable

interface CatService {
    fun findAll(): List<CatDto>
    fun find(id: Int): CatDto?
    fun create(name: String, age: Int): EntityID<Int>
    fun delete(id: Int): Boolean
    fun update(id: Int, name: String, age: Int): Boolean
}

class DefaultCatService : CatService {

    override fun findAll(): List<CatDto> = transaction {
        CatsTable.selectAll().map {
            CatDto(
                it[CatsTable.id].value,
                it[CatsTable.name],
                it[CatsTable.age]
            )
        }
    }

    override fun find(id: Int): CatDto? = transaction {
        CatsTable.select(
            CatsTable.id,
            CatsTable.name,
            CatsTable.age
        ).where {
            CatsTable.id eq id
        }.firstOrNull()
            ?.let {
                CatDto(
                    it[CatsTable.id].value,
                    it[CatsTable.name],
                    it[CatsTable.age]
                )
            }
    }

    override fun create(
        name: String,
        age: Int
    ): EntityID<Int> = transaction {
        CatsTable.insertAndGetId {
            it[CatsTable.name] = name
            it[CatsTable.age] = age
        }
    }

    override fun delete(id: Int): Boolean = transaction {
        CatsTable.deleteWhere { CatsTable.id eq id } > 0
    }

    override fun update(id: Int, name: String, age: Int): Boolean = transaction {
        CatsTable.update({ CatsTable.id eq id }) {
            it[CatsTable.name] = name
            it[CatsTable.age] = age
        } > 0
    }
}
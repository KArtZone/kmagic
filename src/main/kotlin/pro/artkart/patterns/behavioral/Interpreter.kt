package pro.artkart.patterns.behavioral


class SelectStatement(
    vararg columns: String
) {
    val content = StringBuilder("SELECT ${columns.joinToString(", ")} ")

    fun from(table: String, block: FromStatement.() -> Unit): FromStatement =
        FromStatement(table)
            .apply(block)
            .also { content.append(it) }

    override fun toString(): String = content.toString()
}

class FromStatement(table: String) {
    val content = StringBuilder("FROM $table ")
    override fun toString(): String = content.toString()

    fun where(query: String) {
        content.append("WHERE $query")
    }
}

fun select(vararg columns: String, block: SelectStatement.() -> Unit): SelectStatement =
    SelectStatement(*columns).apply(block)

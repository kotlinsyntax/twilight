package gg.flyte.twilight.data.sql

class QueryBuilder {
    private val selectColumns = mutableListOf<String>()
    private var tableName: String? = null
    private val insertColumns = mutableListOf<String>()
    private val insertValues = mutableListOf<Any?>()
    private var updateTableName: String? = null
    private val updateSet = mutableMapOf<String, Any?>()
    private var deleteTableName: String? = null
    private val whereClauses = mutableListOf<String>()
    private val joinClauses = mutableListOf<String>()

    fun select(vararg columns: String): QueryBuilder {
        selectColumns.addAll(columns)
        return this
    }

    fun from(table: String): QueryBuilder {
        tableName = table
        return this
    }

    fun insertInto(table: String, vararg columns: String): QueryBuilder {
        insertColumns.addAll(columns)
        tableName = table
        return this
    }

    fun values(vararg values: Any?): QueryBuilder {
        insertValues.addAll(values)
        return this
    }

    fun update(table: String): QueryBuilder {
        updateTableName = table
        return this
    }

    fun set(column: String, value: Any?): QueryBuilder {
        updateSet[column] = value
        return this
    }

    fun deleteFrom(table: String): QueryBuilder {
        deleteTableName = table
        return this
    }

    fun where(condition: String): QueryBuilder {
        whereClauses.add(condition)
        return this
    }

    fun join(joinClause: String): QueryBuilder {
        joinClauses.add(joinClause)
        return this
    }

    fun buildSelectQuery(): String {
        requireNotNull(tableName) { "Table name must be specified for SELECT query." }
        require(selectColumns.isNotEmpty()) { "At least one column must be selected." }
        val columns = selectColumns.joinToString(", ")
        var query = "SELECT $columns FROM $tableName"
        if (joinClauses.isNotEmpty()) {
            query += " ${joinClauses.joinToString(" ")}"
        }
        if (whereClauses.isNotEmpty()) {
            query += " WHERE ${whereClauses.joinToString(" AND ")}"
        }
        return query
    }

    fun buildInsertQuery(): String {
        requireNotNull(tableName) { "Table name must be specified for INSERT query." }
        require(insertColumns.isNotEmpty()) { "At least one column must be specified for INSERT query." }
        require(insertValues.isNotEmpty()) { "Values must be provided for INSERT query." }
        val columns = insertColumns.joinToString(", ")
        val placeholders = insertColumns.joinToString(", ") { "?" }
        return "INSERT INTO $tableName ($columns) VALUES ($placeholders)"
    }

    fun buildUpdateQuery(): String {
        requireNotNull(updateTableName) { "Table name must be specified for UPDATE query." }
        require(updateSet.isNotEmpty()) { "At least one column must be updated for UPDATE query." }
        val setClause = updateSet.entries.joinToString(", ") { "${it.key} = ?" }
        var query = "UPDATE $updateTableName SET $setClause"
        if (whereClauses.isNotEmpty()) {
            query += " WHERE ${whereClauses.joinToString(" AND ")}"
        }
        return query
    }

    fun buildDeleteQuery(): String {
        requireNotNull(deleteTableName) { "Table name must be specified for DELETE query." }
        var query = "DELETE FROM $deleteTableName"
        if (whereClauses.isNotEmpty()) {
            query += " WHERE ${whereClauses.joinToString(" AND ")}"
        }
        return query
    }
}
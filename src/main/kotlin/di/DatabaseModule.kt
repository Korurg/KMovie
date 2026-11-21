package com.github.korurg.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.babyfish.jimmer.sql.dialect.SQLiteDialect
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.newKSqlClient
import org.babyfish.jimmer.sql.runtime.ConnectionManager
import org.koin.dsl.module
import org.koin.dsl.onClose
import java.nio.file.Files
import java.nio.file.Path

fun databaseModule(
    databasePath: Path = Path.of(System.getProperty("user.home"), ".kmovie", "kmovie.db"),
    changelog: String = "db/changelog/db.changelog-master.yaml",
) = module {
    single(createdAtStart = true) {
        databasePath.toAbsolutePath().parent?.let(Files::createDirectories)
        val dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:${databasePath.toAbsolutePath()}"
            maximumPoolSize = 1
            connectionInitSql = "PRAGMA foreign_keys = ON"
            addDataSourceProperty("busy_timeout", "5000")
        })
        try {
            dataSource.connection.use { connection ->
                val database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(connection))
                Liquibase(changelog, ClassLoaderResourceAccessor(), database).use { liquibase ->
                    liquibase.update(Contexts(), LabelExpression())
                }
            }
            dataSource
        } catch (error: Throwable) {
            dataSource.close()
            throw error
        }
    } onClose { it?.close() }

    single<KSqlClient> {
        val dataSource = get<HikariDataSource>()
        newKSqlClient {
            setDialect(SQLiteDialect())
            setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource))
        }
    }
}

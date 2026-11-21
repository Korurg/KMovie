package com.github.korurg

import com.github.korurg.di.databaseModule
import com.zaxxer.hikari.HikariDataSource
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.io.TempDir
import org.koin.dsl.koinApplication
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Entity
@Table(name = "test_movie")
interface TestMovie {
    @Id
    val id: Long
    val title: String
}

class DatabaseIntegrationTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `default changelog initializes SQLite`() {
        val app = koinApplication { modules(databaseModule(tempDir.resolve("default.db"))) }
        try {
            app.koin.get<KSqlClient>()
        } finally {
            app.close()
        }
    }

    @Test
    fun `Koin connects Jimmer to persistent SQLite and closes the pool`() {
        val path = tempDir.resolve("movies.db")
        val app = koinApplication { modules(databaseModule(path, "db/changelog/test-changelog.yaml")) }
        val pool = app.koin.get<HikariDataSource>()
        try {
            pool.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("insert into test_movie values (1, 'Arrival')")
                    statement.executeQuery("pragma foreign_keys").use { result ->
                        assertTrue(result.next())
                        assertEquals(1, result.getInt(1))
                    }
                }
            }
            val movie = app.koin.get<KSqlClient>().findById(TestMovie::class, 1L)
            assertNotNull(movie)
            assertEquals("Arrival", movie.title)
        } finally {
            app.close()
        }
        assertTrue(pool.isClosed)
        val reopened = koinApplication { modules(databaseModule(path, "db/changelog/test-changelog.yaml")) }
        try {
            val movie = reopened.koin.get<KSqlClient>().findById(TestMovie::class, 1L)
            assertEquals("Arrival", assertNotNull(movie).title)
            reopened.koin.get<HikariDataSource>().connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("select count(*) from DATABASECHANGELOG").use { result ->
                        assertTrue(result.next())
                        assertEquals(1, result.getInt(1))
                    }
                }
            }
        } finally {
            reopened.close()
        }
    }
}

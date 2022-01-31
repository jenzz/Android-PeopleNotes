package com.jenzz.peoplenotes.common.data

import com.jenzz.peoplenotes.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.random.Random

class DatabaseTest {

    private val driver = JdbcSqliteDriver(
        JdbcSqliteDriver.IN_MEMORY,
        Properties().apply { put("foreign_keys", "true") }
    ).also { Database.Schema.create(it) }

    private val testDatabase = Database(driver)

    private val personQueries = testDatabase.personQueries
    private val noteQueries = testDatabase.noteQueries

    @Test
    fun autoIncrementsPersonIds() {
        (1..3).forEach { i ->
            personQueries.insert(
                firstName = "first name $i",
                lastName = "last name $i",
                color = Random.nextInt(),
                lastModified = "last modified",
            )
        }

        val people = personQueries
            .selectAll(null)
            .executeAsList()

        assertEquals(1, people[0].id)
        assertEquals(2, people[1].id)
        assertEquals(3, people[2].id)
    }

    @Test
    fun autoIncrementsNoteIds() {
        personQueries.insert(
            firstName = "first name",
            lastName = "last name",
            color = Random.nextInt(),
            lastModified = "last modified",
        )
        (1..3).forEach { i ->
            noteQueries.insert(
                text = "note $i",
                personId = 1,
                lastModified = "last modified",
            )
        }

        val notes = noteQueries
            .selectAll(1)
            .executeAsList()

        assertEquals(1, notes[0].id)
        assertEquals(2, notes[1].id)
        assertEquals(3, notes[2].id)
    }

    @Test
    fun doesNotAllowDeletingAPersonWithNotes() {
        personQueries.insert(
            firstName = "first name",
            lastName = "last name",
            color = Random.nextInt(),
            lastModified = "last modified",
        )
        noteQueries.insert(
            text = "note",
            personId = 1,
            lastModified = "last modified",
        )

        val exception = assertThrows<Exception> {
            personQueries.delete(1)
        }
        assertTrue(
            exception.message!!.contains("SQLITE_CONSTRAINT_FOREIGNKEY")
        )
    }
}

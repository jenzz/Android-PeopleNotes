package com.jenzz.peoplenotes.common.data.di

import com.jenzz.peoplenotes.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class DatabaseTest {

    private val driver = JdbcSqliteDriver(
        JdbcSqliteDriver.IN_MEMORY,
        Properties().apply { put("foreign_keys", "true") }
    ).also { Database.Schema.create(it) }

    private val database = Database(driver)
    private val personQueries = database.personQueries
    private val noteQueries = database.noteQueries

    @Test
    fun autoIncrementsPersonIds() {
        (1..3).forEach { i ->
            personQueries.insert("first name $i", "last name $i")
        }

        val people = personQueries.selectAll().executeAsList()

        assertEquals(1, people[0].id)
        assertEquals(2, people[1].id)
        assertEquals(3, people[2].id)
    }

    @Test
    fun autoIncrementsNoteIds() {
        personQueries.insert("first name", "last name")
        (1..3).forEach { i ->
            noteQueries.insert("note $i", 1)
        }

        val notes = noteQueries.selectAll().executeAsList()

        assertEquals(1, notes[0].id)
        assertEquals(2, notes[1].id)
        assertEquals(3, notes[2].id)
    }

    @Test
    fun doesNotAllowDeletingAPersonWithNotes() {
        personQueries.insert("first name", "last name")
        noteQueries.insert("note", 1)

        val exception = assertThrows<Exception> {
            personQueries.delete(1)
        }
        assertTrue(exception.message!!.contains("SQLITE_CONSTRAINT_FOREIGNKEY"))
    }
}

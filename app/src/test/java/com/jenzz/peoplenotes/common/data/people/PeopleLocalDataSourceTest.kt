package com.jenzz.peoplenotes.common.data.people

import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.Database
import com.jenzz.peoplenotes.common.data.TestCoroutineDispatchers
import com.jenzz.peoplenotes.common.data.time.SystemClock
import com.jenzz.peoplenotes.ext.random
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.*

class PeopleLocalDataSourceTest {

    private val driver = JdbcSqliteDriver(
        JdbcSqliteDriver.IN_MEMORY,
        Properties().apply { put("foreign_keys", "true") }
    ).also { Database.Schema.create(it) }

    private val testScope = TestScope()
    private val sut = PeopleLocalDataSource(
        personQueries = Database(driver).personQueries,
        dispatchers = TestCoroutineDispatchers(testScope),
        clock = SystemClock(),
    )

    @Test
    fun `sorts people by first name`() = testScope.runTest {
        val person1 = NewPerson(
            firstName = FirstName("John".toNonEmptyString()),
            lastName = LastName("Doe".toNonEmptyString()),
            color = Color.random(),
        )
        val person2 = NewPerson(
            firstName = FirstName("Bob".toNonEmptyString()),
            lastName = LastName("Bob".toNonEmptyString()),
            color = Color.random(),
        )
        val person3 = NewPerson(
            firstName = FirstName("Richard".toNonEmptyString()),
            lastName = LastName("Roe".toNonEmptyString()),
            color = Color.random(),
        )
        sut.add(person1)
        sut.add(person2)
        sut.add(person3)

//        sut.observeAllPeople(PeopleSortBy.FirstName, "")
//            .test {
//                val firstNames = awaitItem().persons.map { person -> person.firstName.toString() }
//                assertThat(firstNames).isEqualTo(listOf("Bob", "John", "Richard"))
//            }
    }
}

package com.jenzz.peoplenotes.common.data.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jenzz.peoplenotes.Database
import com.jenzz.peoplenotes.common.data.NoteQueries
import com.jenzz.peoplenotes.common.data.PersonQueries
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Database(
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = "peoples_notes.db",
                callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                    override fun onConfigure(db: SupportSQLiteDatabase) {
                        db.setForeignKeyConstraintsEnabled(true)
                    }
                }
            )
        )

    @Provides
    fun providePersonQueries(database: Database): PersonQueries =
        database.personQueries

    @Provides
    fun provideNoteQueries(database: Database): NoteQueries =
        database.noteQueries
}

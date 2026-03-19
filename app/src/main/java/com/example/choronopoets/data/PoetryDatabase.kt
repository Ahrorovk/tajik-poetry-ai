package com.example.choronopoets.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dao.CenturyDao
import com.example.choronopoets.dao.FavoritesDao
import com.example.choronopoets.dao.GeneratedFavoritesDao
import com.example.choronopoets.dao.PoemDao
import com.example.choronopoets.dao.PoetDao
import com.example.choronopoets.dataClass.Century
import com.example.choronopoets.dataClass.ChatMessageEntity
import com.example.choronopoets.dataClass.FavoritePoem
import com.example.choronopoets.dataClass.GeneratedFavorite
import com.example.choronopoets.dataClass.Poems
import com.example.choronopoets.dataClass.Poet

@Database(
    entities = [Century::class, Poet::class, Poems::class, FavoritePoem::class, GeneratedFavorite::class, ChatMessageEntity::class],
    version = 5,
    exportSchema = false
)
abstract class PoetryDatabase : RoomDatabase() {
    abstract fun centuryDao(): CenturyDao
    abstract fun poetDao(): PoetDao
    abstract fun poemDao(): PoemDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun generatedFavoritesDao(): GeneratedFavoritesDao
    abstract fun chatHistoryDao(): ChatHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PoetryDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favorites (
                        poemId INTEGER NOT NULL PRIMARY KEY,
                        poetId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        savedAt INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS generated_favorites (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        poetName TEXT NOT NULL,
                        style TEXT NOT NULL DEFAULT '',
                        savedAt INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chat_history (
                        id TEXT NOT NULL PRIMARY KEY,
                        sessionKey TEXT NOT NULL,
                        role TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_chat_history_sessionKey ON chat_history(sessionKey)"
                )
            }
        }

        fun getInstance(context: Context): PoetryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PoetryDatabase::class.java,
                    "poetry_database"
                )
                    .createFromAsset("poetry_database.db")
                    .enableMultiInstanceInvalidation()
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            TajikPoetsPrepopulate.insertIfNeeded(db)
                        }
                    })
                    .build().apply {
                        INSTANCE = this
                    }
            }
        }
    }
}

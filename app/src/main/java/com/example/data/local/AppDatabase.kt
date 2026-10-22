package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AttemptEntity
import com.example.data.model.DailyQuotaEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        TestEntity::class,
        QuestionEntity::class,
        AttemptEntity::class,
        DailyQuotaEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testDao(): TestDao
    abstract fun attemptDao(): AttemptDao
    abstract fun quotaDao(): QuotaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "examkraft_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

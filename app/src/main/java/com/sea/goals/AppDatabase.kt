package com.sea.goals

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [Daily::class, Weekly::class, Challenge::class, Progress::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun dailyGoals(): DailyGoalsDao
    abstract fun weeklyGoals(): WeeklyGoalsDao
    abstract fun challengeGoals(): ChallengeGoalsDao
    abstract fun progress(): ProgressDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: Room
                .databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appdb"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}

//.fallbackToDestructiveMigration()
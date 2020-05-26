package com.sea.goals

import androidx.room.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//Types are indexed by Number -> Daily:1, Weekly:2, Unit: 3, NonComittial: 4

@Entity(tableName = "progress", primaryKeys = arrayOf("goal_id", "type", "date"))
class Progress(
    @ColumnInfo(name = "goal_id") val goal_id: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "progress") val progress: Double,
    @ColumnInfo(name = "date") val date: Int = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
)

abstract class Goal(
    @PrimaryKey(autoGenerate = true) var id:  Int = 0,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "goal") var goal: Double,
    @ColumnInfo(name = "unit") var unit: String,
    @ColumnInfo(name = "specific_goal") var specificGoal: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "today") var today: Int,
    @ColumnInfo(name = "monday") var monday: Double = 0.toDouble(),
    @ColumnInfo(name = "tuesday") var tuesday: Double = 0.toDouble(),
    @ColumnInfo(name = "wednesday") var wednesday: Double = 0.toDouble(),
    @ColumnInfo(name = "thursday") var thursday: Double = 0.toDouble(),
    @ColumnInfo(name = "friday") var friday: Double = 0.toDouble(),
    @ColumnInfo(name = "saturday") var saturday: Double = 0.toDouble(),
    @ColumnInfo(name = "sunday") var sunday: Double = 0.toDouble()
) {
    var priority = 0
    abstract fun setPriority()
}

@Entity(tableName = "daily_goals")
class Daily(name: String,
            goal: Double = 0.0,
            unit: String = "",
            specificGoal: Int = 0) : Goal(name = name, today = 0, type = 1, goal = goal, specificGoal = specificGoal, unit = unit) {
    var perseverance = 0
    init {
        this.priority = 110
        setPriority()
    }
    override fun setPriority() {
        var day = LocalDateTime.now()
        var daysProgress = 0.0
        var progressFactor: Double
        var todaysDay = day.dayOfWeek.name.toLowerCase()

        val week = arrayOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val progressArr = arrayOf(0,0,0,0,0,0,0)
        //Create Week Array: 1 -> if avtivity was done 0 -> if it wasnt done
        var i = 0
        for(cDay in week){
            if(cDay == todaysDay) {
                break
            }
            when(cDay) {
                "monday" -> daysProgress = monday
                "tuesday" -> daysProgress = tuesday
                "wednesday" -> daysProgress = wednesday
                "thursday" -> daysProgress = thursday
                "friday" -> daysProgress = friday
                "saturday" -> daysProgress = saturday
                "sunday" -> daysProgress = sunday
            }
            if(daysProgress != 0.0) {
                progressArr[i] = 1
            }
            i++
        }
        //Algorithm for perseverance
        var t = 0
        var prev = true
        while(t < i) {
            if(progressArr[t] == 0) {
                progressFactor = if(prev) {
                    0.0
                } else {
                    -0.3
                }
                prev = false
            } else {
                progressFactor = if(prev) {
                    1.3
                } else {
                    1.0
                }
                prev = true
            }
            perseverance += (progressFactor * 100).toInt()
            t++
        }
        if(i == 0) {
            perseverance = 50;
        } else {
            perseverance /= i
            if(perseverance > 100) {
                perseverance = 100
            }
            if(perseverance < 0) {
                perseverance = 0
            }
        }
    }


}

@Entity(tableName = "weekly_goals")
class Weekly(name: String,
             goal: Double = 0.0,
             specificGoal: Int = 0,
             unit: String = "",
             @ColumnInfo(name = "days_per_week") val daysPerWeek: Int) : Goal(name = name, today = 0, type = 2, goal = goal, specificGoal = specificGoal, unit = unit) {
    var progress = 0
    init {
        setPriority()
    }
    override fun setPriority() {
        val day = LocalDateTime.now()
        var daysProgress = 0.0
        val todaysDay = day.dayOfWeek.name.toLowerCase()
        val week = arrayOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        var daysLeft = 7;
        for(cDay in week){
            if(cDay == todaysDay) {
                break;
            }
            daysLeft -= 1
            when(cDay) {
                "monday" -> daysProgress = monday
                "tuesday" -> daysProgress = tuesday
                "wednesday" -> daysProgress = wednesday
                "thursday" -> daysProgress = thursday
                "friday" -> daysProgress = friday
                "saturday" -> daysProgress = saturday
                "sunday" -> daysProgress = sunday
            }
            if(!daysProgress.equals(0.0)) {
                progress++
            }
        }
        priority = (((daysPerWeek - progress).toDouble() / daysLeft) * 100.0).toInt()

        if(priority < 0) {
            priority = 0
        }
        if(priority > 100) {
            priority = 120
        }
    }

}

@Dao
interface DailyGoalsDao {
    @Insert
    suspend fun addDailyGoal (dailyGoal : Daily)
    @Query("Select * from daily_goals WHERE today = 0")
    suspend fun getAllRecs(): List<Daily>

    @Query("Select * from daily_goals")
    suspend fun getAll(): List<Daily>

    @Query("Select * from daily_goals WHERE today = 1")
    suspend fun getAllToday(): List<Daily>

    @Query("DELETE from daily_goals")
    suspend fun removeAll()

    @Query("Update daily_goals SET today = :today WHERE id = :id")
    suspend fun setToday(id: Int, today: Int)

    @Query("Update daily_goals SET today = 0")
    suspend fun resetToday()

    @Query("Update daily_goals SET monday = :progress WHERE id = (:id)")
    suspend fun updateMonday(id: Int, progress: Double)

    @Query("Update daily_goals SET tuesday = :progress WHERE id = (:id)")
    suspend fun updateTuesday(id: Int, progress: Double)

    @Query("Update daily_goals SET wednesday = :progress WHERE id = (:id)")
    suspend fun updateWednesday(id: Int, progress: Double)

    @Query("Update daily_goals SET thursday = :progress WHERE id = (:id)")
    suspend fun updateThursday(id: Int, progress: Double)

    @Query("Update daily_goals SET friday = :progress WHERE id = (:id)")
    suspend fun updateFriday(id: Int, progress: Double)

    @Query("Update daily_goals SET saturday = :progress WHERE id = (:id)")
    suspend fun updateSaturday(id: Int, progress: Double)

    @Query("Update daily_goals SET sunday = :progress WHERE id = (:id)")
    suspend fun updateSunday(id: Int, progress: Double)
}

@Dao
interface WeeklyGoalsDao {
    @Insert
    suspend fun addWeeklyGoal (weeklyGoal: Weekly)
    @Query("Select * from weekly_goals WHERE today = 0")
    suspend fun getAllRecs(): List<Weekly>

    @Query("Select * from weekly_goals")
    suspend fun getAll(): List<Weekly>

    @Query("Select * from weekly_goals WHERE today = 1")
    suspend fun getAllToday(): List<Weekly>

    @Query("DELETE from weekly_goals")
    suspend fun removeAll()

    @Query("Update weekly_goals SET today = :today WHERE id = :id")
    suspend fun setToday(id: Int, today: Int)

    @Query("Update weekly_goals SET today = 0")
    suspend fun resetToday()

    @Query("Update weekly_goals SET monday = :progress WHERE id = (:id)")
    suspend fun updateMonday(id: Int, progress: Double)

    @Query("Update weekly_goals SET tuesday = :progress WHERE id = (:id)")
    suspend fun updateTuesday(id: Int, progress: Double)

    @Query("Update weekly_goals SET wednesday = :progress WHERE id = (:id)")
    suspend fun updateWednesday(id: Int, progress: Double)

    @Query("Update weekly_goals SET thursday = :progress WHERE id = (:id)")
    suspend fun updateThursday(id: Int, progress: Double)

    @Query("Update weekly_goals SET friday = :progress WHERE id = (:id)")
    suspend fun updateFriday(id: Int, progress: Double)

    @Query("Update weekly_goals SET saturday = :progress WHERE id = (:id)")
    suspend fun updateSaturday(id: Int, progress: Double)

    @Query("Update weekly_goals SET sunday = :progress WHERE id = (:id)")
    suspend fun updateSunday(id: Int, progress: Double)
}

@Dao
interface ProgressDao {
    @Query("Select * from progress")
    suspend fun getAll(): List<Progress>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProgress (newProgress: Progress)

    @Query("DELETE from progress")
    suspend fun removeAll()
}

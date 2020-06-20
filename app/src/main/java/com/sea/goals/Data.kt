package com.sea.goals

import android.util.Log
import androidx.room.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//TODO getProgress und getPriority schöner schreiben
/**
 * Es gibt 4 verschiedene Tables in der Room Datenbank:
 * Daily ->
 *      Für Tägliche Ziele, ist der Wert von specificGoal 1 dann ist das Feld goal als spezifisches
 *      Ziel relevant, falls der Wert 0 ist, gibt es kein spezifisches Goal, goal ist irrelevant
 * Weekly ->
 *      Das Feld daysPerWeek entscheidet wie oft in der Woche die Aktivität durchgeführt werden soll
 *      Auch hier gilt: ist der Wert von specificGoal 1 dann ist das Feld goal als spezifisches
 *      Ziel relevant, falls der Wert 0 ist, gibt es kein spezifisches Goal, goal ist irrelevant
 * Challenge ->
 *      Hier bestimmt das Feld goal, wie viel in der Woche gemacht werden soll, das Feld specificGoal
 *      ist auf 0 geestzt und hier irrelevant
 * Progress ->
 *      Für jede Aktivität die absolviert wird, wird hier ein Eintrag mit der Aktivitäts Id, dem Typ
 *      und dem Fortschritt erstellt.
 *      Aktivitäten ohne spezifisches Ziel bekommen als Fortschritt imer den Wert 1.0, bei allen
 *      anderen wird der Fortschritt eingetragen
 *
 * Alle Aktivitäten haben für den Fortschritt an jedem Wochentag ein Feld, wo simultan mit dem Progress Table
 * Fortschritte eingetragen werden.
 * Mithilfe dieser Felder kann bei jeder Abfrage und damit erzeugung eines Objekts, der Fortschritt und
 * die Priorität der Aktivität berechnet werden.
 *
 * *Besonderheit, bei Daily ist, dass die Priorität immer auf 110 ist, hier zählt Perseverance also die
 * Konsequenz mit der man an der Aktivität dran bleibt.
 *
 * Das Feld today gibt den Stauts an:
 * 1 -> Aktivität wurde für heute angenommen, wird in Mein Tag angezeiget
 * 0 -> Aktivität wurde nicht aktiviert, wird in Recommondations angezeigt
 * -1 -> Aktivität wurde heute bereits erledigt
 */

@Entity(tableName = "progress", primaryKeys = arrayOf("goal_id", "type", "date"))
class Progress(
    @ColumnInfo(name = "goal_id") val goal_id: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "progress") val progress: Double,
    @ColumnInfo(name = "date") val date: Int = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
)

/**
 * Basisklasse für alles Goals, bestitzt Felder für jeden Wochentag um eine schnelle Abfrage zum Fortschritt
 * der letzten Woche zu gewährleisten
 * Der Typ gibt an um welche Form von Goal es sich handelt -> Daily:1, Weekly:2, Challenge: 3
 * Specific Goal gibt an ob ein Session bzw Tages Ziel gesetzt worden ist (nur für Daily und Weekly), bei Challenge
 * steht hier dass zu erreichende Ziel
 */
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
    abstract fun getPriority(): Int
}

/**
 * Repräsentiert Aktivitäten die jeden Tag ausgeführt werden wollen. Anstelle eine Fortschrittes (Macht nicht so viel
 * Sinn wenn man nur die Tage runter zählt) gibt es die Konsequenz: Wird anhand der vorherigen Tage berechnet
 * wie gut man dran bleibt
 * Man kann entscheiden ob man sich ein spezifisches Ziel pro Tag setzen möchte oder nicht
 */
@Entity(tableName = "daily_goals")
class Daily(name: String,
            goal: Double = 0.0,
            unit: String = "",
            specificGoal: Int = 0) : Goal(name = name, today = 0, type = 1, goal = goal, specificGoal = specificGoal, unit = unit) {
    /**
     * Die Priorität ist by default auf 110 gesetzt und steht damit jeden Tag an erster Stelle
     * (außer bei überfälligen Aktivitäten)
     */
    override fun getPriority(): Int {
        return 110
    }
    /**
     * Hier wird die Konsequenz zurückgegeben (Perseverance) also wie gut man dran bleibt
     * Wird jedes mal berechnet wenn man ein Objekt aus der Datenbank holt
     */
    fun getPeserverance(): Int {
        var perseverance = 0
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
        var testPerseverance = perseverance
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
        return perseverance
    }

}

/**
 * Für eine Aktivität die man ein gewisse Anzahl an Tagen in der Woche machen möchte
 * Man kann sich ein Ziel pro Woche festlegen muss es aber nicht
 */
@Entity(tableName = "weekly_goals")
class Weekly(name: String,
             goal: Double = 0.0,
             specificGoal: Int = 0,
             unit: String = "",
             @ColumnInfo(name = "days_per_week") val daysPerWeek: Int) : Goal(name = name, today = 0, type = 2, goal = goal, specificGoal = specificGoal, unit = unit) {
      /**
     * Hier wird der Fortschritt der Woche zurückgegeben
     */
    fun getProgress(): Double {
          var progress = 0.0
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
                  "monday" -> daysProgress = super.monday
                  "tuesday" -> daysProgress = super.tuesday
                  "wednesday" -> daysProgress = super.wednesday
                  "thursday" -> daysProgress = super.thursday
                  "friday" -> daysProgress = super.friday
                  "saturday" -> daysProgress = super.saturday
                  "sunday" -> daysProgress = super.sunday
              }
              if(!daysProgress.equals(0.0)) {
                  progress++
              }
          }
          return progress
    }

    /**
     * Hie wird die aus dem Fortshritt resultierende Priorität zurückgegeben,
     * Je weniger Tage für das Ziel übrig sind, desto höher wird die Priorität. Geht es sich gerade
     * noch aus, dann ist die Priorität bie 100%, geht es sich nicht mehr aus, dann ist die Priorität bei
     * 120%, dem höchsten Wert den es gibt.
     */
    override fun getPriority(): Int {
        var priority: Int
        var progress = 0.0
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
                "monday" -> daysProgress = super.monday
                "tuesday" -> daysProgress = super.tuesday
                "wednesday" -> daysProgress = super.wednesday
                "thursday" -> daysProgress = super.thursday
                "friday" -> daysProgress = super.friday
                "saturday" -> daysProgress = super.saturday
                "sunday" -> daysProgress = super.sunday
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
        return priority
    }
}

/**
 * Für Aktivitäten wo das Ziel nicht in Tagen sonder in einer anderen Einheit relevant ist
 */
@Entity(tableName= "challenge_goals")
class Challenge(name: String,
                goal: Double = 0.0,
                specificGoal: Int = 0,
                unit: String = "") : Goal(name = name, today = 0, type = 3, goal = goal, specificGoal = specificGoal, unit = unit) {
    fun getProgress(): Double {
        var progress = 0.0
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
        return progress
    }

    override fun getPriority(): Int {
        var priority: Int
        var progress = 0
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
        Log.i("testProgress", progress.toString())
        val relaxFactor = (daysLeft.toDouble() / 14.toDouble())
        val leftTodo = goal - progress.toDouble()
        priority = ((leftTodo/goal)*100).toInt()
        priority -= (relaxFactor * 100).toInt()
        if(priority < 0) {
            priority = 0
        }
        return  priority
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
interface ChallengeGoalsDao {
    @Insert
    suspend fun addChallengeGoal (challengeGoal: Challenge)

    @Query("Select * from challenge_goals WHERE today = 0")
    suspend fun getAllRecs(): List<Challenge>

    @Query("Select * from challenge_goals")
    suspend fun getAll(): List<Challenge>

    @Query("Select * from challenge_goals WHERE today = 1")
    suspend fun getAllToday(): List<Challenge>

    @Query("DELETE from challenge_goals")
    suspend fun removeAll()

    @Query("Update challenge_goals SET today = :today WHERE id = :id")
    suspend fun setToday(id: Int, today: Int)

    @Query("Update challenge_goals SET today = 0")
    suspend fun resetToday()

    @Query("Update challenge_goals SET monday = :progress WHERE id = (:id)")
    suspend fun updateMonday(id: Int, progress: Double)

    @Query("Update challenge_goals SET tuesday = :progress WHERE id = (:id)")
    suspend fun updateTuesday(id: Int, progress: Double)

    @Query("Update challenge_goals SET wednesday = :progress WHERE id = (:id)")
    suspend fun updateWednesday(id: Int, progress: Double)

    @Query("Update challenge_goals SET thursday = :progress WHERE id = (:id)")
    suspend fun updateThursday(id: Int, progress: Double)

    @Query("Update challenge_goals SET friday = :progress WHERE id = (:id)")
    suspend fun updateFriday(id: Int, progress: Double)

    @Query("Update challenge_goals SET saturday = :progress WHERE id = (:id)")
    suspend fun updateSaturday(id: Int, progress: Double)

    @Query("Update challenge_goals SET sunday = :progress WHERE id = (:id)")
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

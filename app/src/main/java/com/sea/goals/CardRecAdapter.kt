package com.sea.goals

import android.animation.ObjectAnimator
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.roundToInt

class CardRecAdapter(private val list: List<Goal>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TYPE_DAILY: Int= 1
    private var TYPE_WEEKLY: Int= 2
    private var TYPE_CHALLENGE: Int= 3
    private lateinit var parentView: ViewGroup


    /**
     * Function um auf Card-Button click zu reagieren, Mein Activity implementiert das Interface
     * und kann so die Methode aufrufen
     */
    interface CardDialogListner {
        fun onAcceptedActivity(type: Int, id: Int)
    }

    /**
     * Wählt je nach Typ der Aktivität das richtige Layout aus
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentView = parent
        return when (viewType) {
            TYPE_DAILY -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.card_rec_daily, parent, false)
                DailyViewHolder(v)
            }
            TYPE_WEEKLY -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.card_rec_weekly, parent, false)
                WeeklyViewHolder(v)
            }
            TYPE_CHALLENGE -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.card_rec_challenge, parent, false)
                ChallengeViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.card_rec_daily, parent, false)
                DailyViewHolder(v)
            }
        }
    }

    /**
     * Füllt die im ViewHolder enthaltenen Views (also die Views der einzelnen Cards) mit Inhalt
     * Für die verschieden Typen wird verschiedener Inhalt befüllt
     * Die views die man hier bearbeiten kann werden im ViewHolder festgelegt
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_DAILY -> {
                //TODO Text durch Graphic ersetzten
                val currentCard: Daily= list[position] as Daily
                val dailyHolder = holder as DailyViewHolder


                // MAX UND PROGRESS
                dailyHolder.progressBar.max = 100//currentCard.goal.roundToInt()
                val currentProgress = currentCard.getPeserverance()


                ObjectAnimator.ofInt(dailyHolder.progressBar, "progress", currentProgress)
                    .setDuration(2000)
                    .start()


                /*
                val consequenceText  = "Konsequenz: " + currentCard.getPeserverance().toString() +
                        "\n" + "Priorität: " + currentCard.getPriority().toString()

                 */

                val specificGoalText = if(currentCard.specificGoal == 1) {
                    "${currentCard.goal}${currentCard.unit} /Tag"
                } else {
                    "kein Tagesziel"
                }

                dailyHolder.titleView.text = currentCard.name
                dailyHolder.goalView.text = specificGoalText
                //dailyHolder.consequenceView.text = consequenceText
                dailyHolder.buttonView.setOnClickListener {
                    //Dialog aufmachen und Click-Listner hinzufügen
                    MaterialAlertDialogBuilder(parentView.context)
                        .setTitle(currentCard.name)
                        .setNegativeButton("Abbrechen") { dialog, which ->
                        }
                        .setPositiveButton("Machen") {dialog, which ->
                            (parentView.context as CardDialogListner).onAcceptedActivity(currentCard.type, currentCard.id)
                        }
                        .show()
                }
            }
            TYPE_WEEKLY -> {
                //TODO Text durch Graphic ersetzten
                val currentCard: Weekly = list[position] as Weekly
                val weeklyHolder = holder as WeeklyViewHolder


                    // circle animation
                    weeklyHolder.circleView.apply {
                        progressMax = currentCard.goal.toFloat()
                        setProgressWithAnimation(currentCard.getProgress().toFloat(), 1000)
                        progressBarWidth = 5f
                        backgroundProgressBarWidth = 7f
                        progressBarColor = Color.GREEN
                    }

                /*
                val progressText = "Progress: " + currentCard.getProgress().toString() +
                        "\n" + "Priorität: " + currentCard.getPriority().toString()

                 */
                val specificGoalText = if(currentCard.specificGoal == 1) {
                    "${currentCard.goal.toString()}${currentCard.unit} /Session"
                } else {
                    "kein Session Ziel"
                }

                weeklyHolder.titleView.text = currentCard.name
                weeklyHolder.goalView.text = specificGoalText
                //weeklyHolder.progressView.text = progressText
                weeklyHolder.buttonView.setOnClickListener{
                    //Dialog aufmachen und Click-Listner hinzufügen
                    MaterialAlertDialogBuilder(parentView.context)
                        .setTitle(currentCard.name)
                        .setNegativeButton("Abbrechen") { dialog, which ->
                        }
                        .setPositiveButton("Machen") {dialog, which ->
                            (parentView.context as CardDialogListner).onAcceptedActivity(currentCard.type, currentCard.id)
                        }
                        .show()
                }
            }
            TYPE_CHALLENGE -> {
                //TODO Text durch Graphic ersetzten
                val currentCard: Challenge = list[position] as Challenge
                val challengeHolder = holder as ChallengeViewHolder


                // circle animation
                challengeHolder.circleView.apply {
                    progressMax = currentCard.goal.toFloat()
                    setProgressWithAnimation(currentCard.getProgress().toFloat(), 1000)
                    progressBarWidth = 5f
                    backgroundProgressBarWidth = 7f
                    progressBarColor = Color.GREEN
                }

                /*
                val progressText = "Progress: " + currentCard.getProgress().toString() +
                        "\n" + "Priorität: " + currentCard.getPriority().toString()

                 */
                val specificGoalText = "${currentCard.goal.toString()}${currentCard.unit} /Woche"

                challengeHolder.titleView.text = currentCard.name
                challengeHolder.goalView.text = specificGoalText
                //challengeHolder.progressView.text = progressText
                challengeHolder.buttonView.setOnClickListener{
                    //Dialog aufmachen und Click-Listner hinzufügen
                    MaterialAlertDialogBuilder(parentView.context)
                        .setTitle(currentCard.name)
                        .setNegativeButton("Abbrechen") { dialog, which ->
                        }
                        .setPositiveButton("Machen") {dialog, which ->
                            (parentView.context as CardDialogListner).onAcceptedActivity(currentCard.type, currentCard.id)
                        }
                        .show()
                }
            }
        }
    }

    /**
     * Gibt den Typ für das Element an der position: position zurück
     */
    override fun getItemViewType(position: Int): Int {
        return when(list[position]) {
            is Daily -> TYPE_DAILY
            is Weekly -> TYPE_WEEKLY
            is Challenge -> TYPE_CHALLENGE
            else -> 0
        }
    }

    override fun getItemCount() = list.size

    /**
     * Speichert gewünschte Views aus dem Layout in Variablen, die dann über DailyViewHolder.Variablename
     * benutzt werden können
     */
    class DailyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var buttonView: Button = v.findViewById(R.id.card_rec_daily_do)
        var titleView: TextView = v.findViewById(R.id.card_rec_daily_title)
        var consequenceView: TextView = v.findViewById(R.id.card_rec_daily_consequence)
        var goalView: TextView = v.findViewById(R.id.card_rec_daily_goal)

        var progressBar: ProgressBar = v.findViewById(R.id.card_rec_daily_progressBar)

    }

    /**
     * Speichert gewünschte Views aus dem Layout in Variablen, die dann über WeeklyViewHolder.Variablename
     * benutzt werden können
     */
    class WeeklyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var buttonView: Button = v.findViewById(R.id.card_rec_weekly_do)
        var titleView: TextView = v.findViewById(R.id.card_rec_weekly_title)
        var progressView: TextView = v.findViewById(R.id.card_rec_weekly_progress)
        var goalView: TextView = v.findViewById(R.id.card_rec_weekly_goal)

        var circleView: CircularProgressBar = v.findViewById(R.id.card_rec_weekly_circle)

    }

    /**
     * Speichert gewünschte Views aus dem Layout in Variablen, die dann über ChallengeViewHolder.Variablename
     * benutzt werden können
     */
    class ChallengeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var buttonView: Button = v.findViewById(R.id.card_rec_challenge_do)
        var titleView: TextView = v.findViewById(R.id.card_rec_challenge_title)
        var progressView: TextView = v.findViewById(R.id.card_rec_challenge_progress)
        var goalView: TextView = v.findViewById(R.id.card_rec_challenge_goal)

        var circleView: CircularProgressBar = v.findViewById(R.id.card_rec_challenge_circle)


    }

}
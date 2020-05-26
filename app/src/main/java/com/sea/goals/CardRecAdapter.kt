package com.sea.goals

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CardRecAdapter(private val list: List<Goal>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TYPE_DAILY: Int= 1
    private var TYPE_WEEKLY: Int= 2
    private lateinit var parentView: ViewGroup

    /**
     * Um in der Main Klasse
     */
    interface CardDialogListner {
        fun onAcceptedActivity(type: Int, id: Int)
    }

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
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.card_rec_daily, parent, false)
                DailyViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //TODO schöner schreiben, nur provisorisch
        when (getItemViewType(position)) {
            TYPE_DAILY -> {
                val currentCard: Daily= list[position] as Daily
                val dailyHolder = holder as DailyViewHolder
                dailyHolder.titleView.text = currentCard.name
                if(currentCard.specificGoal == 1) {
                    dailyHolder.goalView.text = currentCard.goal.toString() + currentCard.unit + "/Tag"
                } else {
                    dailyHolder.goalView.text = "kein Tagesziel"
                }
                dailyHolder.consequenceView.text = "Konsequenz: " + currentCard.perseverance.toString() +
                        "\n" + "Priorität: " + currentCard.priority.toString()
                dailyHolder.buttonView.setOnClickListener {
                    Log.i("test", "card with id:${currentCard.id} and type:${currentCard.type}")
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
                val currentCard: Weekly = list[position] as Weekly
                val weeklyHolder = holder as WeeklyViewHolder
                weeklyHolder.titleView.text = currentCard.name
                if(currentCard.specificGoal == 1) {
                    weeklyHolder.goalView.text = currentCard.goal.toString() + currentCard.unit + "/Session"
                } else {
                    weeklyHolder.goalView.text = "kein Session Ziel"
                }
                weeklyHolder.progressView.text = "Progress: " + currentCard.progress.toString() +
                        "\n" + "Priorität: " + currentCard.priority.toString()
                weeklyHolder.buttonView.setOnClickListener{
                    Log.i("test", "card with id:${currentCard.id} and type:${currentCard.type}")
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

    override fun getItemViewType(position: Int): Int {
        return when(list[position]) {
            is Daily -> TYPE_DAILY
            is Weekly -> TYPE_WEEKLY
            else -> 33
        }
    }

    override fun getItemCount() = list.size

    class DailyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
        var buttonView: Button = v.findViewById(R.id.card_rec_daily_do)
        var titleView: TextView = v.findViewById(R.id.card_rec_daily_title)
        var consequenceView: TextView = v.findViewById(R.id.card_rec_daily_consequence)
        var goalView: TextView = v.findViewById(R.id.card_rec_daily_goal)
    }

    class WeeklyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
        var buttonView: Button = v.findViewById(R.id.card_rec_weekly_do)
        var titleView: TextView = v.findViewById(R.id.card_rec_weekly_title)
        var progressView: TextView = v.findViewById(R.id.card_rec_weekly_progress)
        var goalView: TextView = v.findViewById(R.id.card_rec_weekly_goal)
    }

}
package com.sea.goals

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CardTodayAdapter(private val list: List<Goal>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var TYPE_DAILY: Int= 1
    private var TYPE_WEEKLY: Int= 2
    private lateinit var parentView: ViewGroup

    /**
     *
     */
    interface CardTodayDialogListner {
        fun onCompleteActivity(currentCard: Goal)
        fun onRemoveActivity(currentCard: Goal)
        fun onCompleteActivitySpecific(currentCard: Goal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentView = parent
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_today, parent, false)
        return TodayViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todayHolder = holder as TodayViewHolder
        when (getItemViewType(position)) {
            TYPE_DAILY -> {
                val currentCard: Daily= list[position] as Daily
                todayHolder.title.text = currentCard.name
                todayHolder.buttonDone.setOnClickListener {
                    if(currentCard.specificGoal == 1) {
                        (parentView.context as CardTodayDialogListner).onCompleteActivitySpecific(currentCard)
                    } else {
                        (parentView.context as CardTodayDialogListner).onCompleteActivity(currentCard)
                    }
                 }
                todayHolder.buttonRemove.setOnClickListener{
                    MaterialAlertDialogBuilder(parentView.context)
                        .setTitle(currentCard.name)
                        .setNegativeButton("Abbrechen") { dialog, which ->
                        }
                        .setPositiveButton("Entfernen") {dialog, which ->
                            (parentView.context as CardTodayDialogListner).onRemoveActivity(currentCard)
                        }
                        .show()
                }
            }
            TYPE_WEEKLY -> {
                val currentCard: Weekly = list[position] as Weekly
                todayHolder.title.text = currentCard.name
                todayHolder.buttonDone.setOnClickListener{
                    if(currentCard.specificGoal == 1) {
                        Log.i("test", "non-specific")
                        (parentView.context as CardTodayDialogListner).onCompleteActivitySpecific(currentCard)
                    } else {
                        Log.i("test", "non-specific")
                        (parentView.context as CardTodayDialogListner).onCompleteActivity(currentCard)
                    }

                }
                todayHolder.buttonRemove.setOnClickListener{
                    MaterialAlertDialogBuilder(parentView.context)
                        .setTitle(currentCard.name)
                        .setNegativeButton("Abbrechen") { dialog, which ->
                        }
                        .setPositiveButton("Entfernen") {dialog, which ->
                            (parentView.context as CardTodayDialogListner).onRemoveActivity(currentCard)
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

    class TodayViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
        var buttonDone: Button = v.findViewById(R.id.card_today_done)
        var buttonRemove: Button = v.findViewById(R.id.card_today_remove)
        var title: TextView = v.findViewById(R.id.card_today_title)
    }

}
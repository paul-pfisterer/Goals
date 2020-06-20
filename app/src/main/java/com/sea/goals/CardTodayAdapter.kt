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
    private var TYPE_CHALLENGE: Int= 3
    private lateinit var parentView: ViewGroup

    /**
     * Interface das vom Listner (also TodayActivity) implementiert werden muss, so werden
     * click Events weitergegeben
     */
    interface CardTodayDialogListner {
        fun onCompleteActivity(currentCard: Goal)
        fun onRemoveActivity(currentCard: Goal)
        fun onCompleteActivitySpecific(currentCard: Goal)
    }

    /**
     * Layout für die Cards wird festgelegt
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentView = parent
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_today, parent, false)
        return TodayViewHolder(v)
    }

    /**
     * Die Cards werden mit Content befüllt, abhängig davon welchen Typ sie haben
     * Der OnClick Listner für den buttonDone ruft hier für
     * Aktivität mit einem spezifischen Goal, bzw Challenge Aktivitäten
     * eine andere Methode auf, da hier bei beendigung etwas eingegeben werden muss
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todayHolder = holder as TodayViewHolder
        when (getItemViewType(position)) {
            TYPE_DAILY -> {
                val currentCard: Daily= list[position] as Daily
                todayHolder.title.text = currentCard.name
                todayHolder.buttonDone.setOnClickListener {
                    if(currentCard.specificGoal == 1) {
                        //specifische Aktivitäten rufen Methode für die Eingabe von Werten auf
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
                        //specifische Aktivitäten rufen Methode für die Eingabe von Werten auf
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
            TYPE_CHALLENGE-> {
                val currentCard: Challenge = list[position] as Challenge
                todayHolder.title.text = currentCard.name
                todayHolder.buttonDone.setOnClickListener{
                    //die Methode für die Eingabe von Werten wird aufgrufen, bei Challenges muss immer
                    //etwas eigegeben werden
                    (parentView.context as CardTodayDialogListner).onCompleteActivitySpecific(currentCard)
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

    /**
     * Gibt die Id einer bestimmten Card zurück
     */
    override fun getItemViewType(position: Int): Int {
        return when(list[position]) {
            is Daily -> TYPE_DAILY
            is Weekly -> TYPE_WEEKLY
            is Challenge -> TYPE_CHALLENGE
            else -> 33
        }
    }

    override fun getItemCount() = list.size

    /**
     * Speichern von Views in Variablen die dann für die modifizierung von diesen
     * Views weiter verwendet werden kann
     */
    class TodayViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
        var buttonDone: Button = v.findViewById(R.id.card_today_done)
        var buttonRemove: Button = v.findViewById(R.id.card_today_remove)
        var title: TextView = v.findViewById(R.id.card_today_title)
    }

}
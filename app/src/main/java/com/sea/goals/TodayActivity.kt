package com.sea.goals

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_today.*
import kotlinx.android.synthetic.main.activity_today.drawer_layout
import kotlinx.android.synthetic.main.activity_today.nav_view
import kotlinx.android.synthetic.main.activity_today.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TodayActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    CardTodayAdapter.CardTodayDialogListner,
    DialogFragmentListener{
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase
    private lateinit var dsFragment: DoneSpecificDialogFragment
    private lateinit var dFragment: DoneDialogFragment
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var cardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var cardLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)
        //Time Manger um festzustellen, ob die App heute zum ersten Mal geöffnet wird
        timeManager()
        setContentView(R.layout.activity_today)

        //Set ActionBar, Enable toggle Navigation Drawer, Add Navigation Listener
        setSupportActionBar(toolbar as Toolbar?)
        (toolbar as Toolbar?)?.title = "Today"
        drawer = drawer_layout
        nav_view.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar as Toolbar?, 0, 0
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //Setup Recycler
        cardRecyclerView = recyclerViewToday
        cardLayoutManager = LinearLayoutManager(this)

        //Setup Cards
        CoroutineScope(IO).launch {
            setCards()
        }

        //ClickListner für versteckte Buttons
        goToRec.setOnClickListener {
            intent = Intent(this, RecommendationActivity::class.java)
            startActivity(intent)
        }
        buttonToRec.setOnClickListener {
            intent = Intent(this, RecommendationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(IO).launch {
            setCards()
        }
    }

    /**
     * ClickListner fürs Menu
     */
    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("test", "here")
        val intent: Intent
        when(item.itemId) {
            R.id.recs -> {
                intent = Intent(this, RecommendationActivity::class.java)
                startActivity(intent)
            }
            R.id.createGoal -> {
                intent = Intent(this, CreateGoalActivity::class.java)
                startActivity(intent)
            }
            R.id.stats -> {
                intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
            }
        }
        return true;
    }

    /**
     * Holt sich Einträge aus der Datenbank und listet sie auf
     * Falls keine Einträge vorhanden sind wird ein Hifstext eingeblendet
     */
    private suspend fun setCards() {
        val dailyGoals: List<Goal> = db.dailyGoals().getAllToday()
        val weeklyGoals: List<Goal> = db.weeklyGoals().getAllToday()
        val challengeGoals: List<Goal> = db.challengeGoals().getAllToday()
        val list: List<Goal> = weeklyGoals.plus(dailyGoals).plus(challengeGoals)

        CoroutineScope(Main).launch {
            if(list.isEmpty()) {
                goToRec.visibility = View.GONE
                emptyListBox.visibility = View.VISIBLE
            } else {
                goToRec.visibility = View.VISIBLE
                emptyListBox.visibility = View.GONE
            }
            //Set RecyclerView
            cardAdapter = CardTodayAdapter(list)
            cardRecyclerView.layoutManager = cardLayoutManager
            cardRecyclerView.adapter = cardAdapter
        }
    }

    /**
     * Von RecyclerViwe aufgerufen wenn eine Aktivtät Weekly oder Daily ohne ein spezifisches Goal
     * abgeschlossen wird
     * Öffnet ein neues Dialog Fragment: DoneDialogFragment
     */
    override fun onCompleteActivity(currentCard: Goal) {
        dFragment = DoneDialogFragment(currentCard)
        val fragmentTransaction = supportFragmentManager
        dFragment.show(fragmentTransaction, "")
    }

    /**
     * Von RecyclerViwe aufgerufen wenn eine Aktivtät die vom Typ Challenge ist oder
     * die ein spezifisches Goal hat,abgeschlossen wird
     * Öffnet ein neues Dialog Fragment: DoneSpecificDialogFragment
     */
    override fun onCompleteActivitySpecific(currentCard: Goal) {
        dsFragment = DoneSpecificDialogFragment(currentCard)
        val fragmentTransaction = supportFragmentManager
        dsFragment.show(fragmentTransaction, "")
    }

    /**
     * Von RecyclerView aufgerufen wenn eine Aktivtät nicht gemacht also abgebrochen wird
     */
    override fun onRemoveActivity(currentCard: Goal) {
        CoroutineScope(IO).launch {
            when(currentCard.type) {
                1 -> {
                    db.dailyGoals().setToday(currentCard.id, 0)
                }
                2 -> {
                    db.weeklyGoals().setToday(currentCard.id, 0)
                }
                3 -> {
                    db.challengeGoals().setToday(currentCard.id, 0)
                }
            }
            setCards()
        }
    }

    /**
     * Vom DialogFragment aufgerufen, eine Aktivtät die vom Typ Challenge ist oder
     * die ein spezifisches Goal hat, im Dialog Fenster bestätigt wurde
     * Der eingegebene Wert wird als progress übergen, dann wird addProgress aufgerufen
     * und die Liste erneuert
    */
    override fun onDoneConfirmSpecific(goal: Goal, progress: Double) {
        supportFragmentManager.beginTransaction().remove(dsFragment).commit();
        CoroutineScope(IO).launch {
            addProgress(id = goal.id, type = goal.type, todaysProgress = progress)
            setCards()
        }
    }

    /**
     * Vom DialogFragment aufgerufen wenn eine Aktivtät Weekly oder Daily ohne ein spezifisches Goal
     * im Dialog Fenster bestätigt wurde
     * Es wird addProgress aufgerufen mit 1.0 als Progress
     * und die Liste erneuert
     */
    override fun onDoneConfirm(goal: Goal) {
        supportFragmentManager.beginTransaction().remove(dFragment).commit()
        CoroutineScope(IO).launch {
            addProgress(id = goal.id, type = goal.type, todaysProgress = 1.0)
            setCards()
        }
    }

    /**
     * Abhängig vom Typ der Aktivität werden die jewiligen Methoden aufgerufen
     * Im Progress Table wird ein neuer Eintrag erstellt
     * Beim zutreffenden Wochentag wird der Progress eingetragen
     */
    private suspend fun addProgress(id: Int, type: Int, todaysProgress: Double){
        var day = LocalDateTime.now()
        var date = day.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        var dayField = day.dayOfWeek.name.toLowerCase()
        var progress = Progress(goal_id = id, type = type, progress = todaysProgress, date = date)

        db.progress().addProgress(progress)
        when(type) {
            1 -> {
                when(dayField) {
                    "monday" -> db.dailyGoals().updateMonday(id = id, progress = todaysProgress)
                    "tuesday" -> db.dailyGoals().updateTuesday(id = id, progress = todaysProgress)
                    "wednesday" -> db.dailyGoals().updateWednesday(id = id, progress = todaysProgress)
                    "thursday" -> db.dailyGoals().updateThursday(id = id, progress = todaysProgress)
                    "friday" -> db.dailyGoals().updateFriday(id = id, progress = todaysProgress)
                    "saturday" -> db.dailyGoals().updateSaturday(id = id, progress = todaysProgress)
                    "sunday" -> db.dailyGoals().updateSunday(id = id, progress = todaysProgress)
                }
                db.dailyGoals().setToday(id = id, today = -1)
            }
            2 -> {
                when(dayField) {
                    "monday" -> db.weeklyGoals().updateMonday(id = id, progress = todaysProgress)
                    "tuesday" -> db.weeklyGoals().updateTuesday(id = id, progress = todaysProgress)
                    "wednesday" -> db.weeklyGoals().updateWednesday(id = id, progress = todaysProgress)
                    "thursday" -> db.weeklyGoals().updateThursday(id = id, progress = todaysProgress)
                    "friday" -> db.weeklyGoals().updateFriday(id = id, progress = todaysProgress)
                    "saturday" -> db.weeklyGoals().updateSaturday(id = id, progress = todaysProgress)
                    "sunday" -> db.weeklyGoals().updateSunday(id = id, progress = todaysProgress)
                }
                db.weeklyGoals().setToday(id = id, today = -1)
            }
            3 -> {
                when(dayField) {
                    "monday" -> db.challengeGoals().updateMonday(id = id, progress = todaysProgress)
                    "tuesday" -> db.challengeGoals().updateTuesday(id = id, progress = todaysProgress)
                    "wednesday" -> db.challengeGoals().updateWednesday(id = id, progress = todaysProgress)
                    "thursday" -> db.challengeGoals().updateThursday(id = id, progress = todaysProgress)
                    "friday" -> db.challengeGoals().updateFriday(id = id, progress = todaysProgress)
                    "saturday" -> db.challengeGoals().updateSaturday(id = id, progress = todaysProgress)
                    "sunday" -> db.challengeGoals().updateSunday(id = id, progress = todaysProgress)
                }
                db.challengeGoals().setToday(id = id, today = -1)
            }
        }

    }

    /**
     * Mithilfe der sharedPreferences werden jeden Tag die Aktivitäten zurück gesetzt, und
     * eimal am Tag eine Guten Moregen Meldung ausgegeben
     */
    private fun timeManager() {
        var calendar = Calendar.getInstance()
        var today = calendar.get(Calendar.DAY_OF_MONTH)
        var mode = 0
        val pref_name = "DayCheck"
        val sharedPref: SharedPreferences = getSharedPreferences(pref_name, mode)
        if(sharedPref.getInt("dayOfMonth", 0) != today) {
            val editor = sharedPref.edit()
            editor.putInt("dayOfMonth", today)
            editor.apply()
            CoroutineScope(IO).launch {
                db.dailyGoals().resetToday()
                db.weeklyGoals().resetToday()
                db.challengeGoals().resetToday()
            }
            intent = Intent(this, GoodMorning::class.java)
            startActivity(intent)
        }
    }

}

package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_today.*
import kotlinx.android.synthetic.main.done_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        //Setup Database
        db = AppDatabase.getDatabase(this)
        CoroutineScope(IO).launch {
            setCards()
        }
    }

    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("test", "here")
        val intent: Intent
        when(item.itemId) {
            R.id.recs -> {
                intent = Intent(this, MainActivity::class.java)
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

    private suspend fun setCards() {
        val dailyGoals: List<Goal> = db.dailyGoals().getAllToday()
        val weeklyGoals: List<Goal> = db.weeklyGoals().getAllToday()
        val list: List<Goal> = weeklyGoals.plus(dailyGoals)

        CoroutineScope(Main).launch {
            //Set RecyclerView
            cardAdapter = CardTodayAdapter(list)
            cardRecyclerView.layoutManager = cardLayoutManager
            cardRecyclerView.adapter = cardAdapter
        }
    }

    override fun onCompleteActivity(currentCard: Goal) {
        dFragment = DoneDialogFragment(currentCard)
        val fragmentTransaction = supportFragmentManager
        dFragment.show(fragmentTransaction, "")
    }

    override fun onCompleteActivitySpecific(currentCard: Goal) {
        dsFragment = DoneSpecificDialogFragment(currentCard)
        val fragmentTransaction = supportFragmentManager
        dsFragment.show(fragmentTransaction, "")
    }

    override fun onRemoveActivity(currentCard: Goal) {
        CoroutineScope(IO).launch {
            when(currentCard.type) {
                1 -> {
                    db.dailyGoals().setToday(currentCard.id, 0)
                }
                2 -> {
                    db.weeklyGoals().setToday(currentCard.id, 0)
                }
            }
            setCards()
        }
    }

    override fun onDoneConfirmSpecific(goal: Goal, progress: Double) {
        supportFragmentManager.beginTransaction().remove(dsFragment).commit();
        CoroutineScope(IO).launch {
            addProgress(id = goal.id, type = goal.type, todaysProgress = progress)
            setCards()
        }
    }

    override fun onDoneConfirm(goal: Goal) {
        supportFragmentManager.beginTransaction().remove(dFragment).commit()
        CoroutineScope(IO).launch {
            addProgress(id = goal.id, type = goal.type, todaysProgress = 1.0)
            setCards()
        }
    }

    private suspend fun addProgress(id: Int, type: Int, todaysProgress: Double){
        var day = LocalDateTime.now()
        var date = day.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        var dayField = day.dayOfWeek.name.toLowerCase()
        var progress = Progress(goal_id = id, type = type, progress = todaysProgress, date = date)

        db.progress().addProgress(progress)
        when(type) {
            1 -> {
                Log.i("test", "day")
                when(dayField) {
                    "monday" -> db.dailyGoals().updateMonday(id = id, progress = todaysProgress)
                    "tuesday" -> db.dailyGoals().updateTuesday(id = id, progress = todaysProgress)
                    "wednesday" -> db.dailyGoals().updateWednesday(id = id, progress = todaysProgress)
                    "thursday" -> db.dailyGoals().updateThursday(id = id, progress = todaysProgress)
                    "friday" -> db.dailyGoals().updateFriday(id = id, progress = todaysProgress)
                    "saturday" -> db.dailyGoals().updateSaturday(id = id, progress = todaysProgress)
                    "sunday" -> db.dailyGoals().updateSunday(id = id, progress = todaysProgress)
                }
                db.dailyGoals().setToday(id = id, today = 0)
            }
            2 -> {
                Log.i("test", "weekly")
                when(dayField) {
                    "monday" -> db.weeklyGoals().updateMonday(id = id, progress = todaysProgress)
                    "tuesday" -> db.weeklyGoals().updateTuesday(id = id, progress = todaysProgress)
                    "wednesday" -> db.weeklyGoals().updateWednesday(id = id, progress = todaysProgress)
                    "thursday" -> db.weeklyGoals().updateThursday(id = id, progress = todaysProgress)
                    "friday" -> db.weeklyGoals().updateFriday(id = id, progress = todaysProgress)
                    "saturday" -> db.weeklyGoals().updateSaturday(id = id, progress = todaysProgress)
                    "sunday" -> db.weeklyGoals().updateSunday(id = id, progress = todaysProgress)
                }
                db.weeklyGoals().setToday(id = id, today = 0)
            }
        }

    }

}

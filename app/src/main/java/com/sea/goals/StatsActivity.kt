package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class StatsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

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

        //Setup Database
        db = AppDatabase.getDatabase(this)
        CoroutineScope(IO).launch {
            var data = ""
            val dailyGoals: List<Daily> = db.dailyGoals().getAll()
            dailyGoals.forEach {
                data += "${it.name}/${it.id} --> Mo: ${it.monday}, Di: ${it.tuesday}, Mi: ${it.wednesday}, DO: ${it.thursday}," +
                        "Fr: ${it.friday}, Sa: ${it.saturday}, So: ${it.sunday} " +
                        "today: ${it.today} \n" +
                        "priority: ${it.priority} " +
                        "perseverance: ${it.perseverance}" +
                        "\n\n"
            }
            val weeklyGoals: List<Weekly> = db.weeklyGoals().getAll()
            weeklyGoals.forEach {
                data += "${it.name}/${it.id} --> Mo: ${it.monday}, Di: ${it.tuesday}, Mi: ${it.wednesday}, DO: ${it.thursday}," +
                        "Fr: ${it.friday}, Sa: ${it.saturday}, So: ${it.sunday} " +
                        "today: ${it.today} \n" +
                        "priority: ${it.priority}" +
                        "\n\n"
            }
            val progress: List<Progress> = db.progress().getAll()
            progress.forEach {
                data += "id${it.goal_id}type${it.type} --> Pr: ${it.progress}, on ${it.date}" +
                        "\n"
            }
            CoroutineScope(Main).launch {
                dataText.text = data
            }
        }

        resetTodayButton.setOnClickListener {
            CoroutineScope(IO).launch {
                db.dailyGoals().resetToday()
                db.weeklyGoals().resetToday()
            }
        }

        resetProgress.setOnClickListener {
            CoroutineScope(IO).launch {
                db.progress().removeAll()
            }
        }
    }

    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
            R.id.today -> {
                intent = Intent(this, TodayActivity::class.java)
                startActivity(intent)
            }
        }
        return true;
    }
}

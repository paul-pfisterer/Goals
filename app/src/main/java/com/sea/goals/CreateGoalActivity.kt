package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CreateGoalActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    DailyGoalFragment.FragmentDailyGoalListener,
    WeeklyGoalFragment.FragmentWeeklyGoalListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_goal)

        //Set ActionBar, Enable toggle Navigation Drawer, Add Navigation Listener
        setSupportActionBar(toolbar as Toolbar?)
        (toolbar as Toolbar?)?.title = "Aktivität hinzufügen"
        drawer = drawer_layout
        nav_view.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar as Toolbar?, 0, 0
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()


        //SetFragment
        val dailyGoalFragment = DailyGoalFragment()
        supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, dailyGoalFragment).commit()

        //Connect to Database and save the instance to private variable
        db = AppDatabase.getDatabase(this);
    }

    /**
     * Überschreibt die Funktion der Backtaste, falls der Navigation Drawer offen ist wird dieser geschlossen
     */
    @Override
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Listener für das Menu im Navigation Drawer
     */
    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent
        when(item.itemId) {
            R.id.recs -> {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.today -> {
                intent = Intent(this, TodayActivity::class.java)
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
     * Added ein neues Tägliches Ziel zur Database und finshed im Anschluss die Aktivität
     */
    override fun onSubmitDailyGoalSend(value: String, unit: String) {
        //TODO Checken ob der Input valid ist
        val chosenName = createName.text.toString()
        val goal: Double
        val specificGoal: Int
        if(value != "") {
            specificGoal= 1
            goal = value.toInt().toDouble()
        } else {
            specificGoal = 0
            goal = 0.0
        }
        val goalOb = Daily(name = chosenName, specificGoal = specificGoal, goal = goal, unit = unit)
        CoroutineScope(IO).launch {
            db.dailyGoals().addDailyGoal(goalOb)
            //TODO wo soll beendet werden?
            finish()
        }
    }

    /**
     * Added ein neues wöchentliches Ziel zu Database und fimished im Anschluss die Aktivity
     */
    override fun onSubmitWeeklyGoalSend(value: String, unit: String, daysPerWeek: Int) {
        //TODO Checken ob der Input valid ist
        val chosenName = createName.text.toString()
        val specificGoal: Int
        val goal: Double
        if(value != "") {
            specificGoal= 1
            goal = value.toInt().toDouble()
        } else {
            specificGoal = 0;
            goal = 0.0
        }
        val goalOb = Weekly(name = chosenName, specificGoal = specificGoal, goal = goal, unit = unit, daysPerWeek = daysPerWeek)
        CoroutineScope(IO).launch {
            db.weeklyGoals().addWeeklyGoal(goalOb)
            Log.i("test" , "values")
            //TODO wo soll beendet werden?
            finish()
        }
    }


    /**
     * Wechselt das Fragement, Fragmente sind hier mit Indexen vertreten
     */
    override fun onChangeFrag(forward: Boolean, index: Int) {
        val fragmentNumber: Int = if(forward) {
            index+1
        } else {
            index-1
        }
        when(fragmentNumber) {
            0 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, WeeklyGoalFragment()).commit()
            1 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, DailyGoalFragment()).commit()
            2 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, WeeklyGoalFragment()).commit()
            3 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, DailyGoalFragment()).commit()
        }

    }
}

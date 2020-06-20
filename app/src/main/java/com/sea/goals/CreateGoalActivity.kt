package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    WeeklyGoalFragment.FragmentWeeklyGoalListener,
    ChallengeGoalFragment.FragmentChallengeGoalListener{
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_goal)

        //Connect to Database and save the instance to private variable
        db = AppDatabase.getDatabase(this);
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
                intent = Intent(this, RecommendationActivity::class.java)
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
        //TODO Input Validation
        //Name der Aktivität
        val chosenName = createName.text.toString()
        //Das spezifische Ziel, wenn specificGoal 0, dann irrelevant
        val goal: Double
        //Gibt Auskunft ob es ein spezifisches Ziel gibt: wenn 0 dann nein, wenn 1 dann ja
        val specificGoal: Int
        //Wurde kein Ziel eingegeben, dann wird specificGoal auf 0 gesetzt
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
            finish()
        }
    }

    /**
     * Added ein neues wöchentliches Ziel zu Database und finished im Anschluss die Aktivity
     */
    override fun onSubmitWeeklyGoalSend(value: String, unit: String, daysPerWeek: Int) {
        //TODO Input Validation
        //Name der Aktivität
        val chosenName = createName.text.toString()
        //Gibt Auskunft ob es ein spezifisches Ziel gibt: wenn 0 dann nein, wenn 1 dann ja
        val specificGoal: Int
        //Das spezifische Ziel, wenn specificGoal 0, dann irrelevant
        val goal: Double
        //Wurde kein Ziel eingegeben, dann wird specificGoal auf 0 gesetzt
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
            finish()
        }
    }

    /**
     * Added ein neues challenge Ziel zu Database und finished im Anschluss die Aktivity
     */
    override fun onSubmitChallengeGoalSend(value: String, unit: String) {
        //TODO Input Validation
        //Name der Aktivität
        val chosenName = createName.text.toString()
        //Ziel dass für die Woche vorgenommen wurde
        val goal = value.toDouble()
        //In diesem Fall irrelevant, es gibt nur ein Ziel das unter goal gespeichert wird
        val specificGoal = 0
        val goalOb = Challenge(name = chosenName, specificGoal = specificGoal, goal = goal, unit = unit)
        CoroutineScope(IO).launch {
            db.challengeGoals().addChallengeGoal(goalOb)
            finish()
        }
    }


    /**
     * Wechselt das Fragement, Fragmente sind hier mit Indexen vertreten
     * Der Index wird vom Fragment übergeben wenn es onChangeFrag aufruft
     */
    override fun onChangeFrag(forward: Boolean, index: Int) {
        val fragmentNumber: Int = if(forward) {
            index+1
        } else {
            index-1
        }
        when(fragmentNumber) {
            0 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, ChallengeGoalFragment()).commit()
            1 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, DailyGoalFragment()).commit()
            2 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, WeeklyGoalFragment()).commit()
            3 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, ChallengeGoalFragment()).commit()
            4 -> supportFragmentManager.beginTransaction().replace(R.id.option_placeholder, DailyGoalFragment()).commit()
        }

    }
}

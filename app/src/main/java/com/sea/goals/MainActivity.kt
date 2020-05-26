package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    CardRecAdapter.CardDialogListner{
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var cardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var cardLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set ActionBar, Enable toggle Navigation Drawer, Add Navigation Listener
        setSupportActionBar(toolbar as Toolbar?)
        (toolbar as Toolbar?)?.title = "Vorschläge"
        drawer = drawer_layout
        nav_view.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar as Toolbar?, 0, 0
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //Setup Recycler
        cardRecyclerView = recyclerV
        cardLayoutManager = LinearLayoutManager(this)

        //Setup Database
        db = AppDatabase.getDatabase(this)
        CoroutineScope(IO).launch {
            setCards()
        }
    }

    @Override
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @Override
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent
        when(item.itemId) {
            R.id.createGoal -> {
                intent = Intent(this, CreateGoalActivity::class.java)
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
     * Füllt die Seite mit Karten-Views
     */
    private suspend fun setCards() {
        val dailyGoals: List<Goal> = db.dailyGoals().getAllRecs()
        val weeklyGoals: List<Goal> = db.weeklyGoals().getAllRecs()
        val list: List<Goal> = weeklyGoals.plus(dailyGoals)

        CoroutineScope(Main).launch {
            //Set RecyclerView
            cardAdapter = CardRecAdapter(list)
            cardRecyclerView.layoutManager = cardLayoutManager
            cardRecyclerView.adapter = cardAdapter
        }

    }

    /**
     * Wird aufgerufen, nach Bestätigung, dass man eine Aktivität machen will
     */
    override fun onAcceptedActivity(type: Int, id: Int) {
        Log.i("test","right place: type: $type id: $id")
        CoroutineScope(IO).launch {
            when(type) {
                1 -> db.dailyGoals().setToday(id, 1)
                2 -> db.weeklyGoals().setToday(id, 1)
            }
            setCards()
        }
    }
}

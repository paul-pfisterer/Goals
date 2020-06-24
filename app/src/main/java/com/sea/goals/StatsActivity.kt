package com.sea.goals

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class StatsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var drawer: DrawerLayout
    private lateinit var db: AppDatabase
    private var dayNames = ArrayList<String>()
    private var dayDone = ArrayList<Double>()

    private var weekNames = ArrayList<String>()
    private var weekDone = ArrayList<Double>()

    private var challengeNames = ArrayList<String>()
    private var challengeDone = ArrayList<Double>()

    private lateinit var dailyGoalsTest: List<Daily>
    private lateinit var weeklyGoalsTest: List<Weekly>
    private lateinit var challengeGoalsTest: List<Challenge>

    private lateinit var dailyList: ArrayList<IntRange>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        db = AppDatabase.getDatabase(this)
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
        CoroutineScope(IO).launch {
            val dailyGoals: List<Daily> = db.dailyGoals().getAll()
            val weeklyGoals: List<Weekly> = db.weeklyGoals().getAll()
            val challengeGoals: List<Challenge> = db.challengeGoals().getAll()
            CoroutineScope(Main).launch {
                finishedLoading(dailyGoals, weeklyGoals, challengeGoals)

            }
        }
    }

    fun finishedLoading(dailyGoals: List<Daily>, weeklyGoals: List<Weekly>, challengeGoals: List<Challenge>) {
         dailyGoalsTest = dailyGoals
         weeklyGoalsTest = weeklyGoals
         challengeGoalsTest = challengeGoals

        dailyGoals.forEach {
            dayNames.add(it.name)
            dayDone.add(it.monday)
            dayDone.add(it.tuesday)
            dayDone.add(it.wednesday)
            dayDone.add(it.thursday)
            dayDone.add(it.friday)
            dayDone.add(it.saturday)
            dayDone.add(it.sunday)
        }

        weeklyGoals.forEach{
            weekNames.add(it.name)
            weekDone.add(it.monday)
            weekDone.add(it.tuesday)
            weekDone.add(it.wednesday)
            weekDone.add(it.thursday)
            weekDone.add(it.friday)
            weekDone.add(it.saturday)
            weekDone.add(it.sunday)
        }

        challengeGoals.forEach{
            challengeNames.add(it.name)
            challengeDone.add(it.monday)
            challengeDone.add(it.tuesday)
            challengeDone.add(it.wednesday)
            challengeDone.add(it.thursday)
            challengeDone.add(it.friday)
            challengeDone.add(it.saturday)
            challengeDone.add(it.sunday)
        }

        populateGraphData()
        graphWeekly()
        graphChallenge()

    }

    // statistic challenge goals
    fun graphChallenge(){
        var barChartView = findViewById<BarChart>(R.id.chartDataChallenge)
        val barWidth: Float
        val barSpace: Float
        val groupSpace: Float
        val groupCount = 7

        barWidth = 0.30f
        barSpace = 0.06f
        groupSpace = 0.60f

        var xAxisValues = ArrayList<String>()
        xAxisValues.add("Mon")
        xAxisValues.add("Die")
        xAxisValues.add("Mi")
        xAxisValues.add("Do")
        xAxisValues.add("Fr")
        xAxisValues.add("Sa")
        xAxisValues.add("So")


        var yValueGroup1 = ArrayList<BarEntry>()

       // var operation = yValueGroup1

        // draw the graph
        var barDataSet1: BarDataSet


        var start = 0
        var end = 6


        /*
        // for every different thing
        for(j in 0 until challengeNames.size) {


            if (!challengeDone.isEmpty()) {

                // everyday of the week
                for (i in start..end) {

                    var value = challengeDone.get(i)

                    when (i % 7) {
                        1 -> operation.add(BarEntry(2f, floatArrayOf(value.toFloat())))

                        2 -> operation.add(BarEntry(3f, floatArrayOf(value.toFloat())))

                        3 -> operation.add(BarEntry(4f, floatArrayOf(value.toFloat())))

                        4 -> operation.add(BarEntry(5f, floatArrayOf(value.toFloat())))

                        5 -> operation.add(BarEntry(6f, floatArrayOf(value.toFloat())))

                        6 -> {
                            operation.add(BarEntry(7f, floatArrayOf(value.toFloat())))

                            if(end < challengeDone.size){
                                //  start += 7
                                //  end += 7

                                // zum nächsten
                                // operation = ArrayList<BarEntry>()
                            }
                        }

                        0 -> operation.add(BarEntry(1f, floatArrayOf(value.toFloat())))

                    }

                }
            }


        }
        */

        yValueGroup1.add(BarEntry(1f, floatArrayOf(5.toFloat())))
        yValueGroup1.add(BarEntry(2f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(3f, floatArrayOf(15.toFloat())))
        yValueGroup1.add(BarEntry(4f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(5f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(6f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(7f, floatArrayOf(0.toFloat())))





        barDataSet1 = BarDataSet(yValueGroup1, "Test")

        barDataSet1.setColors(Color.CYAN)

        // barDataSet1.label = "2016"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(false)

        var barData = BarData(barDataSet1)
        barChartView.data = barData // set the data and list of lables into chart


        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()
        legenedEntries.add(LegendEntry("Laufen", Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))


        /*
        // legend for different color per goal
        if(!challengeNames.isEmpty()){

            for(i in 0 until challengeNames.size){

                when(i%7){

                    0 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.RED))

                    1 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.BLUE))

                    2 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.GREEN))

                    3 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))

                    4 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.MAGENTA))

                    5 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.WHITE))

                    6 -> legenedEntries.add(LegendEntry(challengeNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.YELLOW))
                }

            }
        }

         */

        legend.setCustom(legenedEntries)
        legend.setYOffset(5f)
        legend.setXOffset(5f)
        legend.setYEntrySpace(5f)
        legend.setTextSize(5f)
        legend.xEntrySpace = 20f


        // x Axis
        val xAxis = barChartView.getXAxis()
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 9f

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))

        xAxis.setLabelCount(7)
        xAxis.mAxisMaximum = 7f
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 4f
        xAxis.spaceMax = 4f
    }

    // statistic weekly goals
    fun graphWeekly(){
        var barChartView = findViewById<BarChart>(R.id.chartDataWeekly)
        val barWidth: Float
        val barSpace: Float
        val groupSpace: Float
        val groupCount = 7

        barWidth = 0.30f
        barSpace = 0.06f
        groupSpace = 0.60f

        var xAxisValues = ArrayList<String>()
        xAxisValues.add("Mon")
        xAxisValues.add("Die")
        xAxisValues.add("Mi")
        xAxisValues.add("Do")
        xAxisValues.add("Fr")
        xAxisValues.add("Sa")
        xAxisValues.add("So")


        var yValueGroup1 = ArrayList<BarEntry>()

        var operation = yValueGroup1

        // draw the graph
        var barDataSet1: BarDataSet


        var start = 0
        var end = 6

        /*
        // for every different thing
        for(j in 0 until weekNames.size) {



            if (!weekDone.isEmpty()) {

                // everyday of the week
                for (i in start..end) {

                    var value = weekDone.get(i)

                    when (i % 7) {
                        1 -> operation.add(BarEntry(2f, floatArrayOf(value.toFloat())))

                        2 -> operation.add(BarEntry(3f, floatArrayOf(value.toFloat())))

                        3 -> operation.add(BarEntry(4f, floatArrayOf(value.toFloat())))

                        4 -> operation.add(BarEntry(5f, floatArrayOf(value.toFloat())))

                        5 -> operation.add(BarEntry(6f, floatArrayOf(value.toFloat())))

                        6 -> {
                            operation.add(BarEntry(7f, floatArrayOf(value.toFloat())))

                            if(end < weekDone.size){
                                //  start += 7
                                //  end += 7

                                // zum nächsten
                                // operation = ArrayList<BarEntry>()
                            }
                        }

                        0 -> operation.add(BarEntry(1f, floatArrayOf(value.toFloat())))

                    }

                }
            }


        }

         */


        yValueGroup1.add(BarEntry(1f, floatArrayOf(30.toFloat())))
        yValueGroup1.add(BarEntry(2f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(3f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(4f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(5f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(6f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(7f, floatArrayOf(0.toFloat())))


        barDataSet1 = BarDataSet(operation, "Test")
        //barDataSet1 = BarDataSet(yValueGroup1, "Test")

        barDataSet1.setColors(Color.CYAN, Color.RED)

        // barDataSet1.label = "2016"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(false)

        var barData = BarData(barDataSet1)
        barChartView.data = barData // set the data and list of lables into chart


        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(LegendEntry("Trainieren", Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))


        /*
        // legend for different color per goal
        if(!weekNames.isEmpty()){

            for(i in 0 until weekNames.size){

                when(i%7){

                    0 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.RED))

                    1 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.BLUE))

                    2 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.GREEN))

                    3 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))

                    4 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.MAGENTA))

                    5 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.WHITE))

                    6 -> legenedEntries.add(LegendEntry(weekNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.YELLOW))
                }

            }
        }
        */

        legend.setCustom(legenedEntries)
        legend.setYOffset(5f)
        legend.setXOffset(5f)
        legend.setYEntrySpace(5f)
        legend.setTextSize(5f)
        legend.xEntrySpace = 20f


        // x Axis
        val xAxis = barChartView.getXAxis()
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 9f

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))

        xAxis.setLabelCount(7)
        xAxis.mAxisMaximum = 7f
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 4f
        xAxis.spaceMax = 4f
    }

    // statistic daily goals
    fun populateGraphData() {

        var barChartView = findViewById<BarChart>(R.id.chartData)

        val barWidth: Float
        val barSpace: Float
        val groupSpace: Float
        val groupCount = 7

        barWidth = 0.30f
        barSpace = 0.06f
        groupSpace = 0.60f


        var xAxisValues = ArrayList<String>()
        xAxisValues.add("Mon")
        xAxisValues.add("Die")
        xAxisValues.add("Mi")
        xAxisValues.add("Do")
        xAxisValues.add("Fr")
        xAxisValues.add("Sa")
        xAxisValues.add("So")


        var yValueGroup1 = ArrayList<BarEntry>()

        var yVal2 = ArrayList<BarEntry>()


        var start = 0
        var end = 6


        /*
        for(j in 0 until dayNames.size){

             dailyList = arrayListOf(0 until dayNames.size)

            dailyList.forEach {
                j -> ArrayList<BarEntry>()
                var barDataSet: BarDataSet
            }

        }

         */

/*
        // for every different thing
        for(j in 0 until dayNames.size) {


            if (!dayDone.isEmpty()) {

                for (i in start..end) {

                    var value = dayDone.get(i)


                    when (i % 7) {
                        1 -> yValueGroup1.add(BarEntry(2f, floatArrayOf(value.toFloat())))

                        2 -> yValueGroup1.add(BarEntry(3f, floatArrayOf(value.toFloat())))

                        3 -> yValueGroup1.add(BarEntry(4f, floatArrayOf(value.toFloat())))

                        4 -> yValueGroup1.add(BarEntry(5f, floatArrayOf(value.toFloat())))

                        5 -> yValueGroup1.add(BarEntry(6f, floatArrayOf(value.toFloat())))

                        6 -> {
                            yValueGroup1.add(BarEntry(7f, floatArrayOf(value.toFloat())))

                            if(end < dayDone.size){
                                //  start += 7
                                //  end += 7

                                // zum nächsten
                                // operation = ArrayList<BarEntry>()
                            }
                        }

                        0 -> yValueGroup1.add(BarEntry(1f, floatArrayOf(value.toFloat())))

                    }

                }

                // everyday of the week
                for (i in 7..13) {

                    var value = dayDone.get(i)

                    //  dailyList.get(j).add(BarEntry(2f, floatArrayOf(value.toFloat())))

                    when (i % 7) {
                        1 -> yVal2.add(BarEntry(2f, floatArrayOf(value.toFloat())))

                        2 -> yVal2.add(BarEntry(3f, floatArrayOf(value.toFloat())))

                        3 -> yVal2.add(BarEntry(4f, floatArrayOf(value.toFloat())))

                        4 -> yVal2.add(BarEntry(5f, floatArrayOf(value.toFloat())))

                        5 -> yVal2.add(BarEntry(6f, floatArrayOf(value.toFloat())))

                        6 -> {
                            yVal2.add(BarEntry(7f, floatArrayOf(value.toFloat())))

                            if(end < dayDone.size){
                                //  start += 7
                                //  end += 7

                                // zum nächsten
                                // operation = ArrayList<BarEntry>()
                            }
                        }

                        0 -> yVal2.add(BarEntry(1f, floatArrayOf(value.toFloat())))

                    }

                }



            }


        }
        */

        yValueGroup1.add(BarEntry(1f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(2f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(3f, floatArrayOf(15.toFloat())))
        yValueGroup1.add(BarEntry(4f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(5f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(6f, floatArrayOf(0.toFloat())))
        yValueGroup1.add(BarEntry(7f, floatArrayOf(0.toFloat())))

        // draw the graph
        var barDataSet1: BarDataSet
        var barDataSet2: BarDataSet

        barDataSet1 = BarDataSet(yValueGroup1, "Test")

        barDataSet2 = BarDataSet(yVal2, "")
       // barDataSet2.setColors(Color.YELLOW, Color.RED)
        barDataSet2.setDrawIcons(false)
        barDataSet2.setDrawValues(false)

        barDataSet1.setColors(Color.CYAN, Color.RED)

        // barDataSet1.label = "2016"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(false)

        var barData = BarData(barDataSet1, barDataSet2)

        barChartView.data = barData // set the data and list of lables into chart

        //barChartView.groupBars(0f, groupSpace, barSpace)

        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)
        legend.xEntrySpace = 20f

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(LegendEntry("Yoga", Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))


        /*
        // legend for different color per goal
        if(!dayNames.isEmpty()){

            for(i in 0 until dayNames.size){

                when(i%7){

                    0 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.RED))

                    1 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.BLUE))

                    2 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.GREEN))

                    3 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.CYAN))

                    4 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.MAGENTA))

                    5 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.WHITE))

                    6 -> legenedEntries.add(LegendEntry(dayNames.get(i), Legend.LegendForm.SQUARE, 15f, 15f, null, Color.YELLOW))
                }

            }
        }
        */

        legend.setCustom(legenedEntries)
        legend.setYOffset(5f)
        legend.setXOffset(5f)
        legend.setYEntrySpace(5f)
        legend.setTextSize(5f)


        // x Axis
        val xAxis = barChartView.getXAxis()
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 9f

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))

        xAxis.setLabelCount(7)
        xAxis.mAxisMaximum = 7f
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 4f
        xAxis.spaceMax = 4f

       // barChartView.data = barData
       // barChartView.setVisibleXRange(1f, 7f)

    }

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


}
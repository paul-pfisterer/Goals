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
            CoroutineScope(Main).launch {
                finishedLoading(dailyGoals)
            }
        }
    }

    fun finishedLoading(dailyGoals: List<Daily>) {
        var dailyGoalsTest = dailyGoals
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
        populateGraphData()
    }

    // statistic
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


        // draw the graph
        var barDataSet1: BarDataSet



        var value = 0.0

        if(!dayDone.isEmpty()) {
            for (i in 0..6) {


                value = dayDone.get(i)

                    when (i % 7) {

                        1 -> yValueGroup1.add(BarEntry(2f, floatArrayOf(value.toFloat())))

                        2 -> yValueGroup1.add(BarEntry(3f, floatArrayOf(value.toFloat())))

                        3 -> yValueGroup1.add(BarEntry(4f, floatArrayOf(value.toFloat())))

                        4 -> yValueGroup1.add(BarEntry(5f, floatArrayOf(value.toFloat())))

                        5 -> yValueGroup1.add(BarEntry(6f, floatArrayOf(value.toFloat())))

                        6 -> yValueGroup1.add(BarEntry(7f, floatArrayOf(value.toFloat())))

                        0 -> yValueGroup1.add(BarEntry(1f, floatArrayOf(value.toFloat())))

                    }

            }
        }

        /*
        yValueGroup1.add(BarEntry(1f, floatArrayOf(1.toFloat(), 3.toFloat())))

        yValueGroup1.add(BarEntry(2f, floatArrayOf(1.0.toFloat(), 0.toFloat())))

        yValueGroup1.add(BarEntry(3f, floatArrayOf(3.toFloat(), 7.toFloat())))

        yValueGroup1.add(BarEntry(4f, floatArrayOf(1.toFloat(), 2.toFloat())))

        yValueGroup1.add(BarEntry(5f, floatArrayOf(1.toFloat(), 8.toFloat())))

        yValueGroup1.add(BarEntry(6f, floatArrayOf(1.toFloat(), 1.toFloat())))

        yValueGroup1.add(BarEntry(7f, floatArrayOf(5.toFloat(), 0.toFloat())))

         */
        barDataSet1 = BarDataSet(yValueGroup1, "Test")
        barDataSet1.setColors(Color.BLUE, Color.RED)
       // barDataSet1.label = "2016"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(false)

        var barData = BarData(barDataSet1)
        barChartView.data = barData // set the data and list of lables into chart

        /*
        var barData = BarData(barDataSet1)
        barChartView.description.isEnabled = false
        barChartView.description.textSize = 0f
        barData.setValueFormatter(LargeValueFormatter())
        barChartView.setData(barData)
        barChartView.getBarData().setBarWidth(barWidth)
        barChartView.getXAxis().setAxisMinimum(0f)
        barChartView.getXAxis().setAxisMaximum(7f)
        barChartView.groupBars(0f, groupSpace, barSpace)

        // barChartView.groupBars(0f, groupSpace, barSpace)
        //   barChartView.setFitBars(true)
        barChartView.getData().setHighlightEnabled(false)
        barChartView.invalidate()
 */
        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        //dayNames.size


        /*
        var x = 0
        var name = ""

        for(x in 0..dayNames.size){
           name = dayNames.get(x)
            legenedEntries.add(LegendEntry(name, Legend.LegendForm.SQUARE, 8f, 8f,
               null, Color.RED))

        }

         */


        var x =dayNames.get(0)

        legenedEntries.add(LegendEntry(x, Legend.LegendForm.SQUARE, 8f, 8f, null, Color.RED))
        //legenedEntries.add(LegendEntry(y, Legend.LegendForm.SQUARE, 8f, 8f, null, Color.BLUE))


        legend.setCustom(legenedEntries)

        legend.setYOffset(2f)
        legend.setXOffset(2f)
        legend.setYEntrySpace(0f)
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


       // xAxis.setLabelCount(7)
       // xAxis.mAxisMaximum = 7f
       // xAxis.setCenterAxisLabels(true)
       // xAxis.setAvoidFirstLastClipping(true)
        //xAxis.spaceMin = 4f
       // xAxis.spaceMax = 4f
/*2
        barChartView.setVisibleXRangeMaximum(7f)
        barChartView.setVisibleXRangeMinimum(7f)
        barChartView.setDragEnabled(true)

        //Y-axis
        barChartView.getAxisRight().setEnabled(false)
        barChartView.setScaleEnabled(true)

        val leftAxis = barChartView.getAxisLeft()
        leftAxis.setValueFormatter(LargeValueFormatter())
        leftAxis.setDrawGridLines(false)
        leftAxis.setSpaceTop(1f)
        leftAxis.setAxisMinimum(0f)


        barChartView.data = barData
        barChartView.setVisibleXRange(1f, 7f)

         */




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

package com.sea.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_good_morning.*

/**
 * Wird einmal am Tag beim ersten Start der App angezeigt
 */
class GoodMorning : AppCompatActivity() {
    //TODO Message in abhängigkeit von Tageszeit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_good_morning)

        start_button.setOnClickListener {
            intent = Intent(this, RecommendationActivity::class.java)
            finish();
            startActivity(intent)
        }
    }
}
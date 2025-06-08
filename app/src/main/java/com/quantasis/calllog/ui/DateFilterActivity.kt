package com.quantasis.calllog.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quantasis.calllog.R
import com.quantasis.calllog.fragment.DateRangeDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale

class DateFilterActivity : AppCompatActivity() {

    private lateinit var dateRangeText: TextView
    private lateinit var startDateBtn: Button

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_filter)

        dateRangeText = findViewById(R.id.dateRangeText)
        startDateBtn = findViewById(R.id.startDateBtn)

        setupDateButtons();

    }



    private fun setupDateButtons() {
        startDateBtn.setOnClickListener {
            DateRangeDialogFragment { start, end ->
                if (start == null || end == null) {
                    Toast.makeText(applicationContext, "Filter Cleared", Toast.LENGTH_SHORT).show()
                    dateRangeText.text = "No Date ange Selected"
                } else {
                    Toast.makeText(applicationContext, "Selected: $start to $end", Toast.LENGTH_SHORT).show()
                    dateRangeText.text = "${dateFormat.format(start)} - ${dateFormat.format(end)}"
                }
            }.show(supportFragmentManager, "DateRangeDialog")
        }


    }

}
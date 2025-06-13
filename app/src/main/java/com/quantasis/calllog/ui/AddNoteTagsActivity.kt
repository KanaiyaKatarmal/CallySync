package com.quantasis.calllog.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.quantasis.calllog.R
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.database.CallLogDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNoteTagsActivity : AppCompatActivity() {

    private lateinit var callLogDao: CallLogDao
    private var callLogId: Int = 0

    private lateinit var editTextNote: EditText
    private lateinit var editTextTags: EditText
    private lateinit var chipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note_tags)

        callLogDao = AppDatabase.getInstance(this).callLogDao()

        callLogId = intent.getIntExtra("CALL_LOG_ID", 0)

        editTextNote = findViewById(R.id.editTextNote)
        editTextTags = findViewById(R.id.editTextTags)
        chipGroup = findViewById(R.id.chipGroup)

        editTextTags.addTextChangedListener{
            val tags = it.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
            updateChips(tags)
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveNoteAndTags()
        }

        loadExistingData()
    }

    private fun updateChips(tags: List<String>) {
        chipGroup.removeAllViews()
        tags.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                chipGroup.removeView(chip)
            }
            chipGroup.addView(chip)
        }
    }

    private fun saveNoteAndTags() {
        val note = editTextNote.text.toString()
        val tags = (0 until chipGroup.childCount).map {
            val chip = chipGroup.getChildAt(it) as Chip
            chip.text.toString()
        }.joinToString(",")

        lifecycleScope.launch(Dispatchers.IO) {
            callLogDao.updateNoteAndTags(callLogId, note, tags)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddNoteTagsActivity, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadExistingData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val log = callLogDao.getById(callLogId)
            withContext(Dispatchers.Main) {
                editTextNote.setText(log?.note ?: "")
                log?.tags?.split(",")?.let { updateChips(it) }
            }
        }
    }
}
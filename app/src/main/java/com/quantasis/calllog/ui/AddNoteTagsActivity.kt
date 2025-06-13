package com.quantasis.calllog.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
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
    private lateinit var editTextTagInput: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note_tags)

        // Initialize DAO
        callLogDao = AppDatabase.getInstance(this).callLogDao()

        callLogId = intent.getIntExtra("CALL_LOG_ID", 0)

        // Initialize views
        editTextNote = findViewById(R.id.editTextNote)
        editTextTagInput = findViewById(R.id.editTextTagInput)
        chipGroup = findViewById(R.id.chipGroup)
        buttonSave = findViewById(R.id.buttonSave)

        setupTagInput()

        buttonSave.setOnClickListener {
            saveNoteAndTags()
        }

        loadExistingData()
    }

    private fun setupTagInput() {
        // Handle comma, space, or newline entry while typing
        editTextTagInput.addTextChangedListener { editable ->
            editable?.let {
                if (it.endsWith(",") || it.endsWith(" ") || it.endsWith("\n")) {
                    val tag = it.trimEnd(',', ' ', '\n').toString().trim()
                    if (tag.isNotEmpty()) {
                        addChip(tag)
                    }
                    editTextTagInput.text.clear()
                }
            }
        }

        // Handle Done button on keyboard
        editTextTagInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val tag = editTextTagInput.text.toString().trim()
                if (tag.isNotEmpty()) {
                    addChip(tag)
                    editTextTagInput.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    private fun addChip(tag: String) {
        if (!isChipAlreadyExists(tag)) {
            val chip = Chip(this).apply {
                text = tag
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    chipGroup.removeView(this)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun isChipAlreadyExists(tag: String): Boolean {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.text.toString().equals(tag, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun saveNoteAndTags() {
        val note = editTextNote.text.toString()
        val tags = (0 until chipGroup.childCount).joinToString(",") { index ->
            val chip = chipGroup.getChildAt(index) as Chip
            chip.text.toString()
        }

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
                log?.tags?.split(",")?.forEach { tag ->
                    if (tag.isNotBlank()) {
                        addChip(tag)
                    }
                }
            }
        }
    }
}
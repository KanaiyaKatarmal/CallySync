package com.quantasis.calllog.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import java.util.*

class AddNoteTagsActivity : AppCompatActivity() {

    private lateinit var callLogDao: CallLogDao
    private var callLogId: Int = 0

    private lateinit var editTextNote: EditText
    private lateinit var editTextTagInput: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var buttonSave: Button
    private lateinit var buttonMic: ImageButton

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: android.content.Intent
    private var isListening = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startOrStopListening()
        else Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note_tags)

        callLogDao = AppDatabase.getInstance(this).callLogDao()
        callLogId = intent.getIntExtra("CALL_LOG_ID", 0)

        editTextNote = findViewById(R.id.editTextNote)
        editTextTagInput = findViewById(R.id.editTextTagInput)
        chipGroup = findViewById(R.id.chipGroup)
        buttonSave = findViewById(R.id.buttonSave)
        buttonMic = findViewById(R.id.buttonMic)

        setupSpeechRecognizer()
        setupTagInput()

        buttonSave.setOnClickListener { saveNoteAndTags() }
        buttonMic.setOnClickListener { requestAudioPermission() }
        loadExistingData()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { }
            override fun onBeginningOfSpeech() { }
            override fun onRmsChanged(rmsdB: Float) { }
            override fun onBufferReceived(buffer: ByteArray?) { }
            override fun onEndOfSpeech() { stopListening() }
            override fun onError(error: Int) {
                stopListening()
                Toast.makeText(this@AddNoteTagsActivity, "Speech Error: $error", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                spokenText?.let { editTextNote.append("$it ") }
            }
            override fun onPartialResults(partialResults: Bundle?) { }
            override fun onEvent(eventType: Int, params: Bundle?) { }
        })

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }


    }

    private fun flushPendingTag() {
        val pending = editTextTagInput.text.toString().trim()
        if (pending.isNotEmpty()) {
            addChip(pending)
            editTextTagInput.text?.clear()
        }
    }

    private fun requestAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                startOrStopListening()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startOrStopListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        isListening = true
        buttonMic.setImageResource(android.R.drawable.presence_audio_online)  // Change icon to indicate listening
        speechRecognizer.startListening(speechIntent)
    }

    private fun stopListening() {
        isListening = false
        buttonMic.setImageResource(android.R.drawable.ic_btn_speak_now)  // Back to normal icon
        speechRecognizer.stopListening()
    }

    private fun setupTagInput() {
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

        editTextTagInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val tag = editTextTagInput.text.toString().trim()
                if (tag.isNotEmpty()) {
                    addChip(tag)
                    editTextTagInput.text.clear()
                }
                true
            } else false
        }
    }

    private fun addChip(tag: String) {
        if (!isChipAlreadyExists(tag)) {
            val chip = Chip(this).apply {
                text = tag
                isCloseIconVisible = true
                setOnCloseIconClickListener { chipGroup.removeView(this) }
            }
            chipGroup.addView(chip)
        }
    }

    private fun isChipAlreadyExists(tag: String): Boolean {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.text.toString().equals(tag, ignoreCase = true)) return true
        }
        return false
    }

    private fun saveNoteAndTags() {

        flushPendingTag()

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
                log?.tags?.split(",")?.forEach { tag -> if (tag.isNotBlank()) addChip(tag) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
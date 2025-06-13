package com.quantasis.calllog.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.CallLogListAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.viewModel.CallLogViewModel
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.interfacecallback.OnCallLogItemClickListener
import com.quantasis.calllog.repository.CallLogPageType
import com.quantasis.calllog.repository.DownloadCallLogRepository
import com.quantasis.calllog.ui.AddNoteTagsActivity
import com.quantasis.calllog.ui.CallerDashboardActivity
import com.quantasis.calllog.util.CallConvertUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CallLogListFragment : Fragment(R.layout.fragment_call_log) {

    companion object {
        private const val ARG_CALL_TYPE = "arg_call_type"

        private const val ARG_CALL_NUMBER = "arg_call_number"

        private const val ARG_CALL_START_DATE = "arg_call_startdate"

        private const val ARG_CALL_END_DATE = "arg_call_enddate"

        fun newInstance(type: CallLogPageType,number: String?=null,startDate: Date? =null,endDate: Date?=null): CallLogListFragment {
            return CallLogListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CALL_TYPE, type)
                    putString(ARG_CALL_NUMBER, number)
                    putSerializable(ARG_CALL_START_DATE, startDate)
                    putSerializable(ARG_CALL_END_DATE, endDate)
                }
            }
        }
    }

    private lateinit var adapter: CallLogListAdapter
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var number: String? = null;
    private lateinit var progressDialog: AlertDialog



    private val viewModel: CallLogViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                val args = requireArguments()
                val callType = args.getSerializable(ARG_CALL_TYPE) as CallLogPageType
                number = args.getString(ARG_CALL_NUMBER)
                startDate = args.getSerializable(ARG_CALL_START_DATE) as? Date
                endDate = args.getSerializable(ARG_CALL_END_DATE) as? Date

                val dao = AppDatabase.getInstance(requireContext()).callLogDao()
                val repo = CallLogRepository(dao)
                return CallLogViewModel(repo, callType, number, startDate, endDate) as T
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBox = view.findViewById<EditText>(R.id.searchEditText)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
        val menuButton = view.findViewById<ImageButton>(R.id.menuButton)

        val args = requireArguments()
        number = args.getString(ARG_CALL_NUMBER)
        searchBox.setText(number)

        adapter = CallLogListAdapter(object : OnCallLogItemClickListener {
            override fun onItemClick(entry: CallLogEntity) {
                val intent = Intent(requireContext(), CallerDashboardActivity::class.java).apply {
                    putExtra("name", entry.name)
                    putExtra("number", entry.number)
                    putExtra("startDate", startDate?.time ?: -1L)
                    putExtra("endDate", endDate?.time ?: -1L)
                }
                startActivity(intent)
            }
        },object : OnCallLogItemClickListener {
            override fun onItemClick(entry: CallLogEntity) {
                val intent = Intent(requireContext(), AddNoteTagsActivity::class.java).apply {
                    putExtra("name", entry.name)
                    putExtra("number", entry.number)
                    putExtra("CALL_LOG_ID", entry.id)
                }
                startActivity(intent)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Search box input
        searchBox.addTextChangedListener {
            viewModel.setSearch(it.toString())
        }



        // Filter icon click
        filterButton.setOnClickListener {
            // TODO: Implement your filter action here

            DateRangeDialogFragment { start, end ->
                if (start == null || end == null) {
                    Toast.makeText(context, "Filter Cleared", Toast.LENGTH_SHORT).show()
                    //dateRangeText.text = "No Date ange Selected"
                } else {
                    //Toast.makeText(context, "Selected: $start to $end", Toast.LENGTH_SHORT).show()
                    //dateRangeText.text = "${dateFormat.format(start)} - ${dateFormat.format(end)}"
                }
                startDate=start;
                endDate=end
                viewModel.setDateRange(start, end)
            }.show(parentFragmentManager, "DateRangeDialog")
        }

        // 3-dot menu click showing popup
        menuButton.setOnClickListener {
            val popupMenu = android.widget.PopupMenu(requireContext(), menuButton)
            popupMenu.menuInflater.inflate(R.menu.call_log_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_save_pdf -> {
                        onSavePdfClicked()
                        true
                    }
                    R.id.action_save_csv -> {
                        onSaveCsvClicked()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        // Observe paged data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.callLogs.collectLatest {
                adapter.submitData(it)
            }
        }
    }



    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun onSavePdfClicked() {
        lifecycleScope.launch {
            showProgressDialog()
            try {
                val callLogs = fetchCallLogs()
                if (callLogs.isNotEmpty()) {
                    val file = savePdfToFile(callLogs)
                    dismissProgressDialog()
                    showReportGeneratedDialog(file)
                } else {
                    dismissProgressDialog()
                    Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                dismissProgressDialog()
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error while exporting PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSaveCsvClicked() {
        lifecycleScope.launch {
            showProgressDialog()
            try {
                val callLogs = fetchCallLogs()
                if (callLogs.isNotEmpty()) {
                    val file = saveCsvToFile(callLogs)
                    dismissProgressDialog()
                    showReportGeneratedDialog(file)
                } else {
                    dismissProgressDialog()
                    Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                dismissProgressDialog()
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error while exporting PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchCallLogs(): List<CallLogEntity> = withContext(Dispatchers.IO) {
        val dao = AppDatabase.getInstance(requireContext()).downloadCallLogDao()
        val repo = DownloadCallLogRepository(dao)
        repo.getCallLogsList(
            search = view?.findViewById<EditText>(R.id.searchEditText)?.text?.toString(),
            startDate = startDate,
            endDate = endDate,
            type = arguments?.getSerializable(ARG_CALL_TYPE) as CallLogPageType
        )
    }



    private suspend fun saveCsvToFile(callLogs: List<CallLogEntity>) : File = withContext(Dispatchers.IO) {
        val fileName = "call_log_${System.currentTimeMillis()}.csv"
        val file = File(getFilePath(), fileName)

        file.bufferedWriter().use { out ->
            out.write("Name,Number,Date,Duration,CallType\n")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            for (log in callLogs) {
                val dateStr = formatter.format(log.date)
                out.write("${log.name ?: "Unknown"},${log.number},${dateStr},${CallConvertUtil.formatDuration(log.duration)},${CallConvertUtil.callTypeToString(log.callType)}\n")
            }
        }
        file
    }

    private fun getFilePath(): File {
        val backUpFilePath: File= Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS + "/" + getString(R.string.app_name) + "/Report/"
        )
        if (!backUpFilePath.isDirectory) {
            backUpFilePath.mkdirs()
        }
        return backUpFilePath;
    }

    private fun showProgressDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_progress) // create simple progress layout xml
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog.show()
    }

    private fun dismissProgressDialog() {
        if (this::progressDialog.isInitialized) {
            progressDialog.dismiss()
        }
    }

    private fun showReportGeneratedDialog(file: File) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Successfully")
        builder.setMessage("Report generated into:\n${file.absolutePath}")

        builder.setPositiveButton("Share") { dialog, _ ->
            shareFile(file)
            dialog.dismiss()
        }

        builder.setNegativeButton("Open Report") { dialog, _ ->
            openFile(file)
            dialog.dismiss()
        }

        builder.show()
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = CallConvertUtil.getMimeType(file)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share Report"))
    }

    private fun openFile(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, CallConvertUtil.getMimeType(file))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private suspend fun savePdfToFile(callLogs: List<CallLogEntity>): File = withContext(Dispatchers.IO) {
        val fileName = "call_log_${System.currentTimeMillis()}.pdf"
        val file = File(getFilePath(), fileName)

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        val pdfDocument = PdfDocument()
        val paint = Paint().apply { textSize = 12f }
        val boldPaint = Paint().apply { textSize = 14f; typeface = Typeface.DEFAULT_BOLD }

        val headers = listOf("Name", "Number", "Date", "Duration", "CallType")
        val columnWidths = listOf(120f, 100f, 130f, 80f, 100f)

        val logoBitmap = loadLogo()  // <- call safe version here

        var pageNumber = 1
        var yPosition = 0f
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null

        fun startNewPage() {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page?.canvas
            pageNumber++

            if (logoBitmap != null) {
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 150, 150, true)
                canvas?.drawBitmap(scaledLogo, (pageWidth - scaledLogo.width) / 2f, margin, paint)
                yPosition = margin + 150f + 30f
            } else {
                yPosition = margin
            }

            var xPosition = margin
            for ((index, header) in headers.withIndex()) {
                canvas?.drawText(header, xPosition, yPosition, boldPaint)
                xPosition += columnWidths[index]
            }
            yPosition += 25f
        }

        startNewPage()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        for (log in callLogs) {
            if (yPosition > pageHeight - margin - 40) {
                page?.let { pdfDocument.finishPage(it) }
                startNewPage()
            }
            var xPosition = margin
            val values = listOf(
                log.name ?: "Unknown", log.number, dateFormat.format(log.date),
                CallConvertUtil.formatDuration(log.duration), CallConvertUtil.callTypeToString(log.callType)
            )
            for ((index, value) in values.withIndex()) {
                canvas?.drawText(value, xPosition, yPosition, paint)
                xPosition += columnWidths[index]
            }
            yPosition += 20f
        }
        page?.let { pdfDocument.finishPage(it) }
        file.outputStream().use { pdfDocument.writeTo(it) }
        pdfDocument.close()
        file
    }
    private fun loadLogo(): Bitmap? {
        return try {
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_launcher_background)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

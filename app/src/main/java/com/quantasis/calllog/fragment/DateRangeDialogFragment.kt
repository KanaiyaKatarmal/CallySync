package com.quantasis.calllog.fragment
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.quantasis.calllog.R
import java.text.SimpleDateFormat
import java.util.*

class DateRangeDialogFragment(
    private val onRangeSelected: (startDate: Date?, endDate: Date?) -> Unit
) : BottomSheetDialogFragment() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_date_range_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val today = Calendar.getInstance()

        view.findViewById<Button>(R.id.btnToday).setOnClickListener {
            val start = today.clone() as Calendar
            val end = today.clone() as Calendar
            onRangeSelected(start.toStartOfDay().time, end.toEndOfDay().time)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnYesterday).setOnClickListener {
            val start = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
            val end = start.clone() as Calendar
            onRangeSelected(start.toStartOfDay().time, end.toEndOfDay().time)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnCurrentWeek).setOnClickListener {
            val start = getMondayOfThisWeek()
            val end = Calendar.getInstance()
            onRangeSelected(start.toStartOfDay().time, end.toEndOfDay().time)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnPreviousWeek).setOnClickListener {
            val start = getMondayOfThisWeek().apply { add(Calendar.WEEK_OF_YEAR, -1) }
            val end = start.clone() as Calendar
            end.add(Calendar.DATE, 6)
            onRangeSelected(start.toStartOfDay().time, end.toEndOfDay().time)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnCurrentMonth).setOnClickListener {
            val start = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
            val end = Calendar.getInstance()
            onRangeSelected(start.toStartOfDay().time, end.toEndOfDay().time)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnCustom).setOnClickListener {
            showCustomDateRangeDialog()
        }

        view.findViewById<Button>(R.id.btnClearFilter).setOnClickListener {
            onRangeSelected(null, null) // clear filter
            dismiss()
        }
    }

    private fun showCustomDateRangeDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_custom_date_range, null)

        val startDateText = dialogView.findViewById<TextView>(R.id.txtStartDate)
        val endDateText = dialogView.findViewById<TextView>(R.id.txtEndDate)

        val startCal = Calendar.getInstance().toStartOfDay()
        val endCal = Calendar.getInstance().toEndOfDay()

        startDateText.text = formatter.format(startCal.time)
        endDateText.text = formatter.format(endCal.time)

        startDateText.setOnClickListener {
            pickDate(
                current = startCal,
                maxDate = endCal.timeInMillis
            ) {
                startCal.time = it.time
                startDateText.text = formatter.format(startCal.time)
            }
        }

        endDateText.setOnClickListener {
            pickDate(
                current = endCal,
                minDate = startCal.timeInMillis,
                maxDate = System.currentTimeMillis()
            ) {
                endCal.time = it.time
                endDateText.text = formatter.format(endCal.time)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Custom Range")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                onRangeSelected(
                    startCal.toStartOfDay().time,
                    endCal.toEndOfDay().time
                )
                dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getMondayOfThisWeek(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
    }

    private fun pickDate(
        current: Calendar,
        minDate: Long? = null,
        maxDate: Long? = null,
        onPicked: (Calendar) -> Unit
    ) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onPicked(picked)
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        )
        minDate?.let { datePickerDialog.datePicker.minDate = it }
        maxDate?.let { datePickerDialog.datePicker.maxDate = it }
        datePickerDialog.show()
    }

    private fun Calendar.toStartOfDay(): Calendar = apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun Calendar.toEndOfDay(): Calendar = apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}
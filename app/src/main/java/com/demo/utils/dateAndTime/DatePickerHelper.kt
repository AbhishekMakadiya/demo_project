package com.demo.utils.dateAndTime

import android.app.DatePickerDialog
import android.content.Context
import com.demo.utils.LogHelper
import java.util.*

/**
 * Created by in.......n on 22/8/2017.
 */

object DatePickerHelper {
    fun pickDate(mContext: Context?, mSelectedCalendar: Calendar?, requestCode: Int?, callback: OnDateSelected) {
        pickDate(mContext, mSelectedCalendar, null,null, requestCode, callback)
    }

    fun pickDate(mContext: Context?, mSelectedCalendar: Calendar?, minimumStartDate: Calendar?,featuredate: Calendar?, requestCode: Int?, callback: OnDateSelected) {
        if (mContext == null) {
            LogHelper.e(
                this.javaClass.simpleName,
                "Not able to show dialog because of null context"
            )
            return
        }

        val newCalendar = Calendar.getInstance().takeIf { mSelectedCalendar == null }
            ?: mSelectedCalendar?.clone() as Calendar

        val dateDialog = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
            callback.onDateSelected(newDate, requestCode)
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))
        if (minimumStartDate != null) {
            //dateDialog.datePicker.minDate = minimumStartDate.timeInMillis
            dateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
        if (featuredate != null) {
            //dateDialog.datePicker.maxDate = featuredate.timeInMillis
            dateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
        dateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dateDialog.show()
    }

    interface OnDateSelected {
        fun onDateSelected(calendar: Calendar, requestCode: Int?)
    }
}
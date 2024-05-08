package com.demo.utils.dateAndTime

import android.app.TimePickerDialog
import android.content.Context
import com.demo.utils.LogHelper
import java.util.*


object TimePickerHelper {
    fun pickTime(
        mContext: Context?,
        mSelectedCalendar: Calendar?,
        requestCode: Int?,
        callback: OnTimeSelected
    ) {
        pickTime(mContext, mSelectedCalendar, null, requestCode, callback)
    }

    private fun pickTime(mContext: Context?, mSelectedCalendar: Calendar?, minimumStartDate: Calendar?, requestCode: Int?, callback: OnTimeSelected) {
        if (mContext == null) {
            LogHelper.e(
                this.javaClass.simpleName,
                "Not able to show dialog because of null context"
            )
            return
        }

        val newCalendar = Calendar.getInstance().takeIf { mSelectedCalendar == null }
            ?: mSelectedCalendar?.clone() as Calendar

        val timeDialog = TimePickerDialog(
            mContext, TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DATE))
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                callback.onTimeSelected(cal, requestCode)
            },
            newCalendar.get(Calendar.HOUR_OF_DAY),
            newCalendar.get(Calendar.MINUTE),
            true // display 24 hours if true
        )


        timeDialog.show()
    }

    interface OnTimeSelected {
        fun onTimeSelected(calendar: Calendar, requestCode: Int?)
    }
}
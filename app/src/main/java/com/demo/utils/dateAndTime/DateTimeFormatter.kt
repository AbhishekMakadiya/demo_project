package com.demo.utils.dateAndTime

import com.demo.utils.LogHelper
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {
    val TAG = this.javaClass.simpleName

    const val UTC_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val USER_USAGE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val USER_USAGE_DATE_FORMAT1 = "HH:mm:ss, dd/MM/yyyy"
    const val USER_USAGE_DATE_FORMAT2 = "dd-MM-yyyy hh:mm aa"
    const val USER_USAGE_DATE_FORMAT3 = "dd-MMM-yyyy"
    const val USER_USAGE_DATE_FORMAT4 = "yyyy-MM-dd"
    const val USER_USAGE_DATE_FORMAT5 = "MMM yyyy"
    const val USER_USAGE_DATE_FORMAT6 = "MM-dd-yyyy"
    const val USER_USAGE_DATE_FORMAT7 = "YYYY-MM-DD"
    const val USER_USAGE_DATE_FORMAT8 = "dd/MM/yyyy"
    const val USER_USAGE_DATE_FORMAT9 = "MM/yyyy"
    const val DATE_FORMAT_D = "d"
    const val TIME_FORMAT_12_HOUR = "hh:mm aa"
    const val TIME_FORMAT_24_HOUR = "HH:mm"
    const val DEFAULT_PATTERN_DATE = "dd, MMM yyyy"
    const val DEFAULT_PATTERN_DATE1 = "yyyy-MM-dd"
    const val FORMAT1 = "MMM dd, hh:mm aa"

    fun getFormattedDate(calendar: Calendar, format: String): String {
        val dateFormatter = SimpleDateFormat(format, Locale.US)
        return dateFormatter.format(calendar.timeInMillis)
    }

    fun convertDateToDefaultPatern(date1: String?): String? {
        var finlDate: String? = null
        var myDate: Date? = null
        var formatter = SimpleDateFormat(DEFAULT_PATTERN_DATE1, Locale.US)
        try {
            myDate = formatter.parse(date1)
            formatter = SimpleDateFormat(DEFAULT_PATTERN_DATE, Locale.US)
            finlDate = formatter.format(myDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finlDate
    }

    fun getFormattedDateWithSuffix(calendar: Calendar): String {
        val dayNumberSuffix = getDayNumberSuffix(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        val dateFormatter = SimpleDateFormat("d'$dayNumberSuffix' MMMM,yyyy", Locale.US)
        return dateFormatter.format(calendar.timeInMillis)
    }

    fun getFormattedDateWithSuffix(date: String): String {
        val date1 = SimpleDateFormat(USER_USAGE_DATE_FORMAT4, Locale.US).parse(date)
        val day = SimpleDateFormat(DATE_FORMAT_D, Locale.US).format(date1)

        val dayNumberSuffix = getDayNumberSuffix(day.toInt())
        val dateFormatter = SimpleDateFormat("d'$dayNumberSuffix' MMMM yyyy", Locale.US)
        return dateFormatter.format(date1)
    }

    private fun getDayNumberSuffix(day: Int): String {
        /*
          //We are only using suffix not superscript that's why this code is commented.
          if (day in 11..13) {
              return "<sup>th</sup>"
          }
          return when (day % 10) {
              1 -> "<sup>st</sup>"
              2 -> "<sup>nd</sup>"
              3 -> "<sup>rd</sup>"
              else -> "<sup>th</sup>"
          }*/

        if (day in 11..13) {
            return "th"
        }
        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun getConvertedDateToLocalFromUTC(date: String?): String {
        if (date != null) {
            println(date.length)

            val inputFormat: DateFormat
            inputFormat = SimpleDateFormat(UTC_FORMAT, Locale.US)
            var localDateTime: String
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            val parsed: Date
            try {
                parsed = inputFormat.parse(date)
                val dateFormat = SimpleDateFormat(USER_USAGE_DATE_FORMAT2, Locale.US)
                localDateTime = dateFormat.format(parsed)
            } catch (e: Exception) {
                LogHelper.printStackTrace(e)
                localDateTime = ""
            }

            return localDateTime
        } else {
            return ""
        }
    }

    fun getConvertedToLocalFromUTC(date: String?): String {
        if (date != null) {
            println(date.length)

            val inputFormat: DateFormat
            inputFormat = SimpleDateFormat(UTC_FORMAT, Locale.US)
            var localDateTime: String
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            val parsed: Date
            try {
                parsed = inputFormat.parse(date)
                val dateFormat = SimpleDateFormat(USER_USAGE_DATE_FORMAT, Locale.US)
                localDateTime = dateFormat.format(parsed)
            } catch (e: Exception) {
                LogHelper.printStackTrace(e)
                localDateTime = ""
            }

            return localDateTime
        } else {
            return ""
        }
    }

    fun getConvertedDateToLocalFromUTC(date: String, outputFormat: String): String {

        val inputFormat: DateFormat
        inputFormat = SimpleDateFormat(USER_USAGE_DATE_FORMAT, Locale.US)
        var localDateTime: String
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val parsed: Date
        try {
            parsed = inputFormat.parse(date)
            val dateFormat = SimpleDateFormat(outputFormat, Locale.US)
            localDateTime = dateFormat.format(parsed)
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
            localDateTime = ""
        }

        return localDateTime
    }


    fun getCurrentDateTimeInUTC(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US)
        val yourDate =
            android.text.format.DateFormat.format(USER_USAGE_DATE_FORMAT, calendar).toString()
        return yourDate
    }

    fun currentMilisecondToFormat(millisecond: Long, format: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millisecond
        return android.text.format.DateFormat.format(format, calendar).toString()
    }

    fun getMilliSecondsFromDate(date: String, currentFormat: String): Long {
        return try {
            val mDate = SimpleDateFormat(currentFormat, Locale.US).parse(date)
            val timeInMilliSeconds = mDate.time
            LogHelper.i(TAG, "Date in milli :: $timeInMilliSeconds")
            timeInMilliSeconds
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }

    }

    fun convertDateString(strDate: String, inputFormat: String, outputFormat: String): Date? {
        var date: Date? = null
        try {
            val df = SimpleDateFormat(outputFormat, Locale.US)
//            date = df.format(SimpleDateFormat(inputFormat, Locale.US).parse(strDate))
            //date = SimpleDateFormat(inputFormat, Locale.US).parse(strDate)
            date = SimpleDateFormat(inputFormat, Locale.US).parse(strDate)
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
        return date
    }

    fun getConvertedDate(date: String, outputFormat: String): String {
        val inputFormat: DateFormat
        inputFormat = SimpleDateFormat(USER_USAGE_DATE_FORMAT4, Locale.US)
        var localDateTime: String
        val parsed: Date
        try {
            parsed = inputFormat.parse(date)
            val dateFormat = SimpleDateFormat(outputFormat, Locale.US)
            localDateTime = dateFormat.format(parsed)
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
            localDateTime = ""
        }

        return localDateTime
    }

    fun getMilliSecondsFromDate(date: String): Long {
        return try {
            val mDate = SimpleDateFormat(USER_USAGE_DATE_FORMAT4, Locale.US).parse(date)
            val timeInMilliSeconds = mDate.time
            LogHelper.i(TAG, "Date in milli :: $timeInMilliSeconds")
            timeInMilliSeconds
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    fun convert24HoursTo12Hours(time: String): String {
        return try {
            //val s = "12:18:00"
            val f1: DateFormat = SimpleDateFormat("HH:mm:ss") //HH for hour of the day (0 - 23)
            val date = f1.parse(time)
            val f2: DateFormat = SimpleDateFormat(TIME_FORMAT_12_HOUR)
            f2.format(date).toUpperCase() // "12:18 AM"
        } catch (e: ParseException) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    fun convert12HoursTo24Hours(time: String): String {
        return try {
            //val s = "12:18:00"
            val f1: DateFormat = SimpleDateFormat("HH:mm a") //HH for hour of the day (0 - 23)
            val date = f1.parse(time)
            val f2: DateFormat = SimpleDateFormat("HH:mm")
            f2.format(date).toUpperCase() // "12:18 AM"
        } catch (e: ParseException) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    fun convert12HoursTo24Hours2(time: String): String {
        val df: DateFormat = SimpleDateFormat("HH:mm")
        var mCalendar = Calendar.getInstance()
        mCalendar.time = df.parse(time)
        return android.text.format.DateFormat.format(TIME_FORMAT_24_HOUR, mCalendar).toString()

    }

    fun isToMinuteLessOrEqual(mCalanderFrom: Calendar, mCalanderTo: Calendar, minDiffMinute: Int): Boolean {
        return mCalanderTo.get(Calendar.MINUTE) <= mCalanderFrom.get(Calendar.MINUTE) + minDiffMinute
    }

    fun isToMinuteLessOrEqual(fromTime: String, toTime: String, minDiffMinute: Int): Boolean {
        //val f: DateFormat = SimpleDateFormat("HH:mm") //HH for hour of the day (0 - 23)

        val df: DateFormat = SimpleDateFormat("HH:mm")
        val calFrom = Calendar.getInstance()
        val calTo = Calendar.getInstance()
        val tmpCal = Calendar.getInstance()
        tmpCal.timeInMillis = System.currentTimeMillis()
        calFrom.time = df.parse(fromTime)
        calTo.time = df.parse(toTime)
        calFrom.set(tmpCal.get(Calendar.YEAR), tmpCal.get(Calendar.MONTH), tmpCal.get(Calendar.DATE))
        calTo.set(tmpCal.get(Calendar.YEAR), tmpCal.get(Calendar.MONTH), tmpCal.get(Calendar.DATE))
        //return calTo.get(Calendar.MINUTE) <= calFrom.get(Calendar.MINUTE) + minDiffMinute

        /*var isInvalid = false
        if (calTo.get(Calendar.HOUR) < calFrom.get(Calendar.HOUR)) {
            isInvalid = true
        } else if (calTo.get(Calendar.HOUR) == calFrom.get(Calendar.HOUR) && calTo.get(Calendar.MINUTE) <= calFrom.get(Calendar.MINUTE)) {
            isInvalid = true
        } else {
            isInvalid = false
        }
        return isInvalid*/

        return calTo.timeInMillis < calFrom.timeInMillis
    }
}
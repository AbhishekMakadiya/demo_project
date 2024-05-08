package com.demo.utils.otpFillUtils

import android.view.KeyEvent
import android.view.View
import com.demo.R
import com.demo.views.MyCustomEditText

class GenericKeyEvent internal constructor(private val currentView: MyCustomEditText, private val previousView: MyCustomEditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.edt1 && currentView.text.toString().isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}


// reference : https://stackoverflow.com/questions/38872546/edit-text-for-otp-with-each-letter-in-separate-positions/38873586
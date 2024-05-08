package com.demo.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.demo.constants.Const
import com.demo.R


class MyCustomRadioButton(mContext: Context, attrs: AttributeSet?) : AppCompatRadioButton(mContext, attrs) {
    //    private val TAG: String = this.javaClass.simpleName
    init {
//        val textStyle = attrs?.getAttributeIntValue(Const.ANDROID_SCHEMA, "textStyle", Typeface.NORMAL)

        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomRadioButton)
        val myFontFamily = array.getInt(R.styleable.MyCustomTextView_myFontFamily, Const.FONT_TYPE_REGULAR)
        array.recycle()

        applyFontFamily(myFontFamily)
    }

    private fun applyFontFamily(myFontFamily: Int) {
        when (myFontFamily) {
            Const.FONT_TYPE_REGULAR -> {
                typeface = Typeface.createFromAsset(context.assets, Const.FONT_PATH_REGULAR)
            }
            Const.FONT_TYPE_SEMI_BOLD -> {
                typeface = Typeface.createFromAsset(context.assets, Const.FONT_PATH_SEMI_BOLD)
            }
            Const.FONT_TYPE_BOLD -> {
                typeface = Typeface.createFromAsset(context.assets, Const.FONT_PATH_BOLD)
            }
            Const.FONT_TYPE_MEDIUM -> {
                typeface = Typeface.createFromAsset(context.assets, Const.FONT_PATH_MEDIUM)
            }
        }
    }

}

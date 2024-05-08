package com.demo.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import com.demo.constants.Const
import com.google.android.material.textfield.TextInputEditText
import com.demo.R

class MyCustomTextInputEditText(context: Context, attrs: AttributeSet?) : TextInputEditText(context, attrs) {
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomTextInputEditText)
        val myFontFamily = array.getInt(R.styleable.MyCustomTextInputEditText_myFontFamily, Const.FONT_TYPE_REGULAR)

        val default = resources.getDimension(R.dimen.default_corner_radius)
        var cornerRadiusTopLeft = default
        var cornerRadiusTopRight = default
        var cornerRadiusBottomRight = default
        var cornerRadiusBottomLeft = default
        val strokeSize = array.getInt(R.styleable.MyCustomTextInputEditText_MyTIETBackgroundStrokeSize, 0)
        val color =
            array.getColor(R.styleable.MyCustomTextInputEditText_MyTIETBackgroundColor, Color.TRANSPARENT)
        val strokeColor = array.getColor(
            R.styleable.MyCustomTextInputEditText_MyTIETBackgroundStrokeColor,
            Color.TRANSPARENT
        )
        cornerRadiusTopLeft = array.getDimension(
            R.styleable.MyCustomTextInputEditText_MyTIETCorRadiusTopLeft,
            cornerRadiusTopLeft
        )
        cornerRadiusTopRight = array.getDimension(
            R.styleable.MyCustomTextInputEditText_MyTIETCorRadiusTopRight,
            cornerRadiusTopRight
        )
        cornerRadiusBottomRight = array.getDimension(
            R.styleable.MyCustomTextInputEditText_MyTIETCorRadiusBottomRight,
            cornerRadiusBottomRight
        )
        cornerRadiusBottomLeft = array.getDimension(
            R.styleable.MyCustomTextInputEditText_MyTIETCorRadiusBottomLeft,
            cornerRadiusBottomLeft
        )


        if (color != Color.TRANSPARENT || strokeColor != Color.TRANSPARENT)
            background = DrawableHelper().getRoundedRectangleDrawableWithStroke(
                context,
                strokeColor,
                color,
                cornerRadiusTopLeft.toInt(),
                cornerRadiusTopRight.toInt(),
                cornerRadiusBottomRight.toInt(),
                cornerRadiusBottomLeft.toInt(),
                strokeSize
            )


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

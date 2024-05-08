package com.demo.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.demo.R
import com.demo.constants.Const


class MyCustomButton(context: Context, attrs: AttributeSet?) : AppCompatButton(context, attrs) {

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomButton)
        val myFontFamily = array.getInt(R.styleable.MyCustomButton_myFontFamily, Const.FONT_TYPE_REGULAR)

        val default = resources.getDimension(R.dimen.default_corner_radius)
        var cornerRadiusTopLeft = default
        var cornerRadiusTopRight = default
        var cornerRadiusBottomRight = default
        var cornerRadiusBottomLeft = default

        val color =
            array.getColor(R.styleable.MyCustomButton_MyBtnBackgroundColor, Color.TRANSPARENT)
        val strokeColor =
            array.getColor(R.styleable.MyCustomButton_MyBtnBackgroundStrokeColor, Color.TRANSPARENT)
        val strokeSize = array.getInt(R.styleable.MyCustomButton_MyBtnBackgroundStrokeSize, 0)
        cornerRadiusTopLeft = array.getDimension(
            R.styleable.MyCustomButton_MyBtnCorRadiusTopLeft,
            cornerRadiusTopLeft
        )
        cornerRadiusTopRight = array.getDimension(
            R.styleable.MyCustomButton_MyBtnCorRadiusTopRight,
            cornerRadiusTopRight
        )
        cornerRadiusBottomRight = array.getDimension(
            R.styleable.MyCustomButton_MyBtnCorRadiusBottomRight,
            cornerRadiusBottomRight
        )
        cornerRadiusBottomLeft = array.getDimension(
            R.styleable.MyCustomButton_MyBtnCorRadiusBottomLeft,
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
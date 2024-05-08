package com.demo.views

import android.content.Context
import android.graphics.Color

import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.R

class MyCustomConstraintLayout(mContext: Context, attrs: AttributeSet) :
    ConstraintLayout(mContext, attrs) {
    private var cornerRadiusTopLeft = resources.getDimension(R.dimen.default_corner_radius)
    private var cornerRadiusTopRight = resources.getDimension(R.dimen.default_corner_radius)
    private var cornerRadiusBottomRight = resources.getDimension(R.dimen.default_corner_radius)
    private var cornerRadiusBottomLeft = resources.getDimension(R.dimen.default_corner_radius)
    fun setColor(color:Int,strokeColor:Int,cornerRadiusTopLeft:Float,cornerRadiusTopRight:Float,cornerRadiusBootomRight:Float,cornerRadiusBootomLeft:Float,strokeSize:Int) {
        background = DrawableHelper().getRoundedRectangleDrawableWithStroke(
            context,
            strokeColor,
            color,
            cornerRadiusTopLeft.toInt(),
            cornerRadiusTopRight.toInt(),
            cornerRadiusBootomRight.toInt(),
            cornerRadiusBootomLeft.toInt(),
            strokeSize
        )
    }
    init {

        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomConstraintLayout)
        val strokeSize =
            array.getInt(R.styleable.MyCustomConstraintLayout_MyConBackgroundStrokeSize, 0)
        val strokeColor = array.getColor(
            R.styleable.MyCustomConstraintLayout_MyConBackgroundStrokeColor,
            Color.TRANSPARENT
        )
        val color = array.getColor(
            R.styleable.MyCustomConstraintLayout_MyConBackgroundColor,
            Color.TRANSPARENT/* ContextCompat.getColor(context, R.color.colorWhite)*/
        )
        cornerRadiusTopLeft = array.getDimension(
            R.styleable.MyCustomConstraintLayout_MyConCorRadiusTopLeft,
            cornerRadiusTopLeft
        )
        cornerRadiusTopRight = array.getDimension(
            R.styleable.MyCustomConstraintLayout_MyConCorRadiusTopRight,
            cornerRadiusTopRight
        )
        cornerRadiusBottomRight = array.getDimension(
            R.styleable.MyCustomConstraintLayout_MyConCorRadiusBottomRight,
            cornerRadiusBottomRight
        )
        cornerRadiusBottomLeft = array.getDimension(
            R.styleable.MyCustomConstraintLayout_MyConCorRadiusBottomLeft,
            cornerRadiusBottomLeft
        )
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
    }
}
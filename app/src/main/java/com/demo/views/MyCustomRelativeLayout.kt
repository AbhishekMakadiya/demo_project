package com.demo.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.demo.R

class MyCustomRelativeLayout(mContext: Context, attrs: AttributeSet) : RelativeLayout(mContext, attrs) {
    var cornerRadiusTopLeft = resources.getDimension(R.dimen.default_corner_radius)
    var cornerRadiusTopRight = resources.getDimension(R.dimen.default_corner_radius)
    var cornerRadiusBootomRight = resources.getDimension(R.dimen.default_corner_radius)
    var cornerRadiusBootomLeft = resources.getDimension(R.dimen.default_corner_radius)

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomRelativeLayout)
        val color = array.getColor(R.styleable.MyCustomRelativeLayout_MyRelBackgroundColor, Color.TRANSPARENT/* ContextCompat.getColor(context, R.color.colorWhite)*/)
        cornerRadiusTopLeft = array.getDimension(R.styleable.MyCustomRelativeLayout_MyRelCorRadiusTopLeft, cornerRadiusTopLeft)
        cornerRadiusTopRight = array.getDimension(R.styleable.MyCustomRelativeLayout_MyRelCorRadiusTopRight, cornerRadiusTopRight)
        cornerRadiusBootomRight = array.getDimension(R.styleable.MyCustomRelativeLayout_MyRelCorRadiusBottomRight, cornerRadiusBootomRight)
        cornerRadiusBootomLeft = array.getDimension(R.styleable.MyCustomRelativeLayout_MyRelCorRadiusBottomLeft, cornerRadiusBootomLeft)

        if (color != Color.TRANSPARENT)
            background = DrawableHelper().getRoundedRectangleDrawableWithStroke(context, color, color, cornerRadiusTopLeft.toInt(), cornerRadiusTopRight.toInt(), cornerRadiusBootomRight.toInt(), cornerRadiusBootomLeft.toInt(), 0)

        array.recycle()
    }
}
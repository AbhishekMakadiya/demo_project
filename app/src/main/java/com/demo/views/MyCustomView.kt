package com.demo.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.demo.R

class MyCustomView(mContext: Context, attrs: AttributeSet) : View(mContext, attrs) {

    init {
        var cornerRadius = resources.getDimension(R.dimen.default_corner_radius)

        val array = context.obtainStyledAttributes(attrs, R.styleable.MyCustomView)
        val color = array.getColor(
                R.styleable.MyCustomView_MyViewBackgroundColor,
                Color.TRANSPARENT/* ContextCompat.getColor(context, R.color.colorWhite)*/
        )
        cornerRadius = array.getDimension(R.styleable.MyCustomView_MyViewCorRadius, cornerRadius)

        background = DrawableHelper().getRoundedRectangleDrawableWithStroke(
                context,
                color,
                color,
                cornerRadius.toInt(),
                cornerRadius.toInt(),
                cornerRadius.toInt(),
                cornerRadius.toInt(),
                0
        )
        array.recycle()
    }
}
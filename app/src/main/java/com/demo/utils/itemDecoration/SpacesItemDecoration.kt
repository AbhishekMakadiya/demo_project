package com.demo.utils.itemDecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val spaceVertical: Int, private val spaceHorizontal: Int) : RecyclerView.ItemDecoration() {

    constructor(spaceVertical: Int) : this(spaceVertical, spaceVertical)

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spaceHorizontal
        outRect.right = spaceHorizontal
        outRect.bottom = spaceVertical

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = spaceVertical
        } else {
            outRect.top = 0
        }
    }
}
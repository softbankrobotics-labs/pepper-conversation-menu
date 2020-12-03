package com.softbankrobotics.dx.dynamicconversationmenu

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class SpanningGridLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    private val verticalSpace: Int
        get() = height - paddingBottom - paddingTop
    private val numberOfLines: Double
        get() = ceil(itemCount / spanCount.toDouble())
    private val marginsSize = 4

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(c: Context, attrs: AttributeSet): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(c, attrs))
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(lp))
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
        val layoutParams = generateDefaultLayoutParams()
        return super.checkLayoutParams(lp) &&
                layoutParams.width == lp.width &&
                layoutParams.height == lp.height
    }

    private fun spanLayoutSize(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        layoutParams.height = ((verticalSpace / numberOfLines).roundToInt()) - (marginsSize * 2)
        layoutParams.setMargins(marginsSize, marginsSize, marginsSize, marginsSize)
        return layoutParams
    }

    override fun canScrollVertically(): Boolean = false

    override fun canScrollHorizontally(): Boolean = false
}
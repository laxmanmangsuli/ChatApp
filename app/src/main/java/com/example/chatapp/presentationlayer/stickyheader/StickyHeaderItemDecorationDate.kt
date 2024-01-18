package com.example.chatapp.presentationlayer.stickyheader
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapps.R
import kotlin.math.max

class StickyHeaderItemDecorationDate(
    private val headerOffset: Int,
    private val sectionCallback: SectionCallback
) : RecyclerView.ItemDecoration() {

    private lateinit var headerView: View
    private lateinit var header: TextView

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSection(pos)) {
            outRect.top = headerOffset
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (!this::headerView.isInitialized) {
            headerView = inflateHeaderView(parent)
            header = headerView.findViewById(R.id.stickyDate)
            fixLayoutSize(headerView, parent)
        }

        val firstVisibleItemPosition =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
            val title = sectionCallback.getSectionHeader(firstVisibleItemPosition)
            header.text = title

            if (!title.isNullOrBlank() && sectionCallback.isSection(firstVisibleItemPosition)) {
                drawHeader(c, headerView)
            }
        }
    }

    private fun drawHeader(c: Canvas, headerView: View) {
        c.save()
        c.translate(0f, 0f) // Draw the header at the top
        headerView.draw(c)
        c.restore()
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_sticky_header_date,
                parent,
                false
            )
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            parent.width,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            parent.height,
            View.MeasureSpec.UNSPECIFIED
        )
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {
        fun isSection(position: Int): Boolean
        fun getSectionHeader(position: Int): CharSequence?
    }
}

package com.example.tugasakhirpam.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class ThreadLineDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = 0xFF5C6BC0.toInt()
        strokeWidth = 4f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val density = context.resources.displayMetrics.density
    private fun dp(value: Int) = (value * density)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as? CommentAdapter ?: return

        // Kumpulkan semua item yang visible beserta level-nya
        data class VisibleItem(val index: Int, val level: Int, val top: Int, val bottom: Int)

        val visibleItems = mutableListOf<VisibleItem>()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position < 0) continue
            val (_, level) = adapter.getItem(position)
            visibleItems.add(VisibleItem(position, level, child.top, child.bottom))
        }

        // Untuk setiap level > 0, cari grup berurutan dan gambar 1 garis per grup
        val maxLevel = visibleItems.maxOfOrNull { it.level } ?: return

        for (targetLevel in 1..maxLevel) {
            val lineX = dp(targetLevel * 48) - dp(16)

            var groupStart: Int? = null
            var groupEnd: Int? = null

            fun drawGroup() {
                if (groupStart != null && groupEnd != null) {
                    canvas.drawLine(lineX, groupStart!!.toFloat(), lineX, groupEnd!!.toFloat(), paint)
                }
            }

            for (item in visibleItems) {
                if (item.level >= targetLevel) {
                    if (groupStart == null) groupStart = item.top
                    groupEnd = item.bottom
                } else {
                    drawGroup()
                    groupStart = null
                    groupEnd = null
                }
            }
            drawGroup() // gambar grup terakhir
        }
    }
}
package com.dqdq.pictureslector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Created by DQDQ on 17/4/2022.
 */
class PictureSelector : View {

    private var redPointRadius = 20f
    private var maxItemCountPerLayer = 3
    private var pictureCount = 1
    private var itemWidth = 200
    private var itemTopMargin = 40
    private var items = mutableListOf<SelectorItem>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        var margin: Float =
            (width - (itemWidth * maxItemCountPerLayer)) / (2 + (maxItemCountPerLayer - 1)).toFloat()
        var x = margin
        var y = 50f
        for (i in 1..pictureCount) {
            if (i == 1)
                items.add(SelectorItem(x, y, "", null))
            else
                items.add(SelectorItem(x, y, null, null))
            if (i % maxItemCountPerLayer == 0) {
                x = margin
                y += itemWidth + 50f
            } else {
                x += itemWidth + margin
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var paint = Paint()
        var redPointPaint = Paint()
        paint.color = Color.BLACK
        redPointPaint.color = Color.RED

        for (item in items) {
            with(item) {
                canvas?.drawRect(x, y, (x + itemWidth), (y + itemWidth), paint)
                if (item.imgUrl != null) { //画X
                    canvas?.drawCircle(x + itemWidth, y, redPointRadius, redPointPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
            for ((index,item) in items.withIndex()) {
                with(item) {
                    var redX = (x + itemWidth) - redPointRadius
                    var redY = y - redPointRadius
                    if (event!!.x in (redX..redX + redPointRadius * 2)
                        && event!!.y in (redY..redY + redPointRadius * 2)
                    ) {
                        itemCanCelClickEvent(index,item)
                    } else if (event!!.x in (x..x + itemWidth)
                        && event!!.y in (y..y + itemWidth)
                    ) {
                        itemClickEvent(index,item)
                    }
                }
            }

        return super.onTouchEvent(event)
    }

    data class SelectorItem(val x: Float, val y: Float, var imgUrl: String?, var imgBitmap: Bitmap?)

    private fun itemClickEvent(index: Int,item: SelectorItem) {
        Log.i("test", "itemClickEvent ${item.x} ${item.y}")
    }

    private fun itemCanCelClickEvent(index: Int,item: SelectorItem) {
        item.imgUrl = null
        item.imgBitmap = null
        items[index] = item
        invalidate()
        Log.i("test", "itemCanCelClickEvent ${item.x} ${item.y}")
    }
}
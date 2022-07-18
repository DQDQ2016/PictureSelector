package com.dqdq.pictureslector

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Created by DQDQ on 17/4/2022.
 */
class PictureSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var redPointRadius = 20f
    private var maxItemCountPerLayer = 3 //每层最多item
    private var pictureCount = 6     //总item
    private var itemWidth = 200
    private var itemTopMargin = 50f
    private var cancelStrokeWidth = 4f
    private var xPadding = 10
    private var items = mutableListOf<SelectorItem>()

    private var pictureSelectFun: (() -> Unit)? = null

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()

        for ((index, item) in items.withIndex()) {
            if (item.imgUrl != null && item.imgUrl != "") {
                getItemXY(index).let { coordinate ->

                    item.imgBitmap?.let {
                        canvas?.drawBitmap(it, coordinate.first, coordinate.second, paint)
                    }
                    paint.color = Color.RED
                    canvas?.drawCircle(coordinate.first + itemWidth
                        , coordinate.second, redPointRadius, paint)
                    paint.color = Color.WHITE//画X
                    paint.strokeWidth = cancelStrokeWidth

                    val redPointX = coordinate.first + itemWidth - redPointRadius
                    val redPointY = coordinate.second - redPointRadius

                    canvas?.drawLine(
                        redPointX + xPadding,
                        redPointY + xPadding,
                        redPointX + redPointRadius * 2 - xPadding,
                        redPointY + redPointRadius * 2 - xPadding,
                        paint
                    )
                    canvas?.drawLine(
                        redPointX + redPointRadius * 2 - xPadding,
                        redPointY + xPadding,
                        redPointX + xPadding,
                        redPointY + redPointRadius * 2 - xPadding,
                        paint
                    )
                }
            }
        }

        if (items.size < pictureCount) {
            Log.i("test", items.size.toString())
            getItemXY(items.size).let {
                val res: Resources = resources
                val r = res.getDrawable(R.drawable.comment_addimg)
                val db: BitmapDrawable = r as BitmapDrawable
                val bgBitmap = db.bitmap.zoomImage(itemWidth, itemWidth)
                paint.color = Color.BLACK
                canvas?.drawBitmap(bgBitmap, it.first, it.second, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        for ((index, item) in items.withIndex()) {
            getItemXY(index).let {
                val redX = (it.first + itemWidth) - redPointRadius
                val redY = it.second - redPointRadius
                if (event!!.x in (redX..redX + redPointRadius * 2) //
                    && event!!.y in (redY..redY + redPointRadius * 2)
                ) {
                    itemCanCelClickEvent(item)
                    return super.onTouchEvent(event)
                }
            }
        }
        if (items.size < pictureCount) {
            getItemXY(items.size).let {
                if (event!!.x in (it.first..it.first + itemWidth)
                    && event!!.y in (it.second..it.second + itemWidth)
                ) {
                    itemClickEvent()
                    return super.onTouchEvent(event)
                }
            }
        }

        return super.onTouchEvent(event)
    }

    data class SelectorItem(
        var imgUrl: String?,
        var imgBitmap: Bitmap?
    )

    private fun itemClickEvent() {
        pictureSelectFun?.invoke()
    }

    private fun itemCanCelClickEvent(item: SelectorItem) {
        items.remove(item)
        invalidate()
    }

    fun setPictureSelectFun(f: () -> Unit) {
        this.pictureSelectFun = f
    }

    fun pushPicture(url: String, pic: Bitmap) {

        val newBitmap = pic.zoomImage(itemWidth, itemWidth)
        val item = SelectorItem(imgUrl = url, imgBitmap = newBitmap)
        items.add(item)
        invalidate()

        Log.i("test", "pushPicture")
    }

    private fun getItemXY(itemIndex: Int): Pair<Float, Float> {
        val margin: Float =
            (width - (itemWidth * maxItemCountPerLayer)) / (2 + (maxItemCountPerLayer - 1)).toFloat()
        var x = margin
        var y = itemTopMargin

        val itemCountHeight = itemTopMargin + itemWidth
        val itemCountWidth = itemWidth + margin

        x += itemIndex % maxItemCountPerLayer * itemCountWidth
        y += itemIndex / maxItemCountPerLayer * itemCountHeight

        return Pair(x, y)
    }

    fun getPictures(): List<SelectorItem> {
        return items.toList()
    }

}
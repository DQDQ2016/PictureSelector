package com.dqdq.pictureslector

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import java.util.*

/**
 * Created by DQDQ on 17/4/2022.
 */
class PictureSelector : View {

    private var redPointRadius = 20f
    private var maxItemCountPerLayer = 3
    private var pictureCount = 3
    private var itemWidth = 200
    private var itemTopMargin = 40
    private var cancelStrokeWidth = 4f
    private var xPadding = 10
    private var items = PriorityQueue<SelectorItem>()
    private lateinit var itemsPoint: MutableList<Array<Float>>

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        itemsPoint = MutableList(pictureCount) { arrayOf(0f, 0f) }

        var compareByUrl = Comparator<SelectorItem> { t, t1 ->
            Log.i("test","compare")
            if (t.imgUrl == "" && t1.imgUrl != "")
                -1
            else if(t.imgUrl != "" && t1.imgUrl == "")
                1
            else if(t.imgUrl == "" && t1.imgUrl == "")
                t.index - t1.index
            else if(t.imgUrl != "" && t1.imgUrl != "")
                t.index - t1.index
            else
                0
        }

        items = PriorityQueue<SelectorItem>(compareByUrl)

        val res: Resources = resources
        var r =  res.getDrawable(R.drawable.one)
        var db:BitmapDrawable = r as BitmapDrawable

        var margin: Float =
            (width - (itemWidth * maxItemCountPerLayer)) / (2 + (maxItemCountPerLayer - 1)).toFloat()
        var x = margin
        var y = 50f
        for (i in 0 until pictureCount) {
            itemsPoint[i][0] = x
            itemsPoint[i][1] = y

            if (i == 0) {
                items.add(SelectorItem(x, y, i, "", db.bitmap))
            } else if (i == 1) {
                r =  res.getDrawable(R.drawable.two)
                db = r as BitmapDrawable
                items.add(SelectorItem(x, y, i, "", db.bitmap))
            } else if (i == 2) {
                r =  res.getDrawable(R.drawable.three)
                db = r as BitmapDrawable
                items.add(SelectorItem(x, y, i, "", db.bitmap))
            }else
                items.add(SelectorItem(x, y, i, "", null))

            if (i % maxItemCountPerLayer == 0 && i != 0) {
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

        for ((index,item) in items.withIndex()) {
            item.x = itemsPoint[index][0]
            item.y = itemsPoint[index][1]

            with(item) {

                if (item.imgUrl != null) {
                    item.imgBitmap?.let {
                        canvas?.drawBitmap(it,x,y,paint)
                    }
                    paint.color = Color.RED
                    canvas?.drawCircle(x + itemWidth, y, redPointRadius, paint)
                    paint.color = Color.WHITE//画X
                    paint.strokeWidth = cancelStrokeWidth

                    var redPointX = x + itemWidth - redPointRadius
                    var redPointY = y - redPointRadius

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
                }else{
                    paint.color = Color.BLACK
                    canvas?.drawRect(x, y, (x + itemWidth), (y + itemWidth), paint) //todo 绘制背景图
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        for (item in items) {
            var redX = (item.x + itemWidth) - redPointRadius
            var redY = item.y - redPointRadius
            if (event!!.x in (redX..redX + redPointRadius * 2) //
                && event!!.y in (redY..redY + redPointRadius * 2)
            ) {
                itemCanCelClickEvent(item)
                break
            } else if (event!!.x in (item.x..item.x + itemWidth)
                && event!!.y in (item.y..item.y + itemWidth)
            ) {
                itemClickEvent(item)
                break
            }
        }

        return super.onTouchEvent(event)
    }

    data class SelectorItem(
        var x: Float,
        var y: Float,
        val index: Int,
        var imgUrl: String?,
        var imgBitmap: Bitmap?
    )

    private fun itemClickEvent(item: SelectorItem) { //todo 选择图片
        Log.i("test", "itemClickEvent ${item.x} ${item.y}")
    }

    private fun itemCanCelClickEvent(item: SelectorItem) {
        items.remove(item)
        item.imgUrl = null
        item.imgBitmap = null
        items.add(item)
        invalidate()
        Log.i("test", "itemCanCelClickEvent ${item.x} ${item.y}")
    }
}
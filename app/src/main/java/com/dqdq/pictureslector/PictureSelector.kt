package com.dqdq.pictureslector

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Created by DQDQ on 17/4/2022.
 */
class PictureSelector : View {

    private var redPointRadius = 20f
    private var maxItemCountPerLayer = 3 //每层最多item
    private var pictureCount = 5 //总item
    private var itemWidth = 200
    private var itemTopMargin = 40
    private var cancelStrokeWidth = 4f
    private var xPadding = 10
    private var items = PriorityQueue<SelectorItem>()
    private lateinit var itemsPoint: MutableList<Array<Float>>
    private var listener: (() -> Unit)? = null
    private var picCount = 0 //记录已选择的图片数量
    //todo 图片显示顺序逻辑

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        itemsPoint = MutableList(pictureCount) { arrayOf(0f, 0f) }

        var compareByUrl = Comparator<SelectorItem> { t, t1 ->
            Log.i("test", "compare")
            if (t.imgUrl == "" && t1.imgUrl != "")
                 1
            else if (t.imgUrl != "" && t1.imgUrl == "")
                -1
            else if (t.imgUrl != "" && t1.imgUrl != "")
                t.index - t1.index
            else
                t.index - t1.index
        }

        items = PriorityQueue<SelectorItem>(compareByUrl)

        var margin: Float =
            (width - (itemWidth * maxItemCountPerLayer)) / (2 + (maxItemCountPerLayer - 1)).toFloat()
        var x = margin
        var y = 50f
        for (i in 1..pictureCount) {
            itemsPoint[i - 1][0] = x
            itemsPoint[i - 1][1] = y
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

        for ((index, item) in items.withIndex()) {
            item.x = itemsPoint[index][0]
            item.y = itemsPoint[index][1]
            if (item.imgUrl != null && item.imgUrl != "") {
                item.imgBitmap?.let {
                    canvas?.drawBitmap(it, item.x, item.y, paint)
                }
                paint.color = Color.RED
                canvas?.drawCircle(item.x + itemWidth, item.y, redPointRadius, paint)
                paint.color = Color.WHITE//画X
                paint.strokeWidth = cancelStrokeWidth

                var redPointX = item.x + itemWidth - redPointRadius
                var redPointY = item.y - redPointRadius

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
            } else {
                val res: Resources = resources
                var r = res.getDrawable(R.drawable.comment_addimg)
                var db: BitmapDrawable = r as BitmapDrawable
                var bgBitmap = zoomImage(db.bitmap,itemWidth,itemWidth)
                paint.color = Color.BLACK
                canvas?.drawBitmap(bgBitmap,item.x, item.y ,paint)
                break
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
                && event!!.y in (item.y..item.y + itemWidth) && item.imgUrl == "") {
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
        listener?.invoke()
    }

    private fun itemCanCelClickEvent(item: SelectorItem) {
        items.remove(item)
        item.imgUrl = ""
        item.imgBitmap = null
        items.add(item)
        invalidate()
        Log.i("test", "itemCanCelClickEvent ${item.x} ${item.y}")
    }

    fun setPictureSelectListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun pushPicture(url: String, pic: Bitmap) {
        var item: SelectorItem? = null
        for (v in items) {
            if (v.imgUrl == null || v.imgUrl == "") {
                item = v
                break
            }
        }
        if (item != null) {
            var newBitmap = zoomImage(pic, itemWidth, itemWidth)
            items.remove(item)
            item.imgUrl = url
            item.imgBitmap = newBitmap
            var tempList = mutableListOf<SelectorItem>()
            tempList.add(item)
            tempList.addAll(items)
            items.clear()
            items.addAll(tempList)
            invalidate()
        }
        Log.i("test", "pushPicture")
    }

    fun getPictures(): List<SelectorItem> {
        return items.toList()
    }

    private fun zoomImage(
        bgImage: Bitmap, newWidth: Int,
        newHeight: Int
    ): Bitmap {
        // 获取这个图片的宽和高
        var width = bgImage.width;
        var height = bgImage.height;
        // 创建操作图片用的matrix对象
        var matrix = Matrix()
        // 计算宽高缩放率
        var scaleWidth = (newWidth.toFloat()) / width;
        var scaleHeight = (newHeight.toFloat()) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        var bitmap = Bitmap.createBitmap(
            bgImage, 0, 0, width,
            height, matrix, true
        );
        return bitmap;
    }

}
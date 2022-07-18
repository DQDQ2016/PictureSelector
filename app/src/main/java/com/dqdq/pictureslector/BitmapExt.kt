package com.dqdq.pictureslector

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by DQDQ on 18/7/2022
 */
fun Bitmap.zoomImage(
    newWidth: Int,
    newHeight: Int
): Bitmap {
    // 获取这个图片的宽和高
    var width = this.width;
    var height = this.height;
    // 创建操作图片用的matrix对象
    var matrix = Matrix()
    // 计算宽高缩放率
    var scaleWidth = (newWidth.toFloat()) / width;
    var scaleHeight = (newHeight.toFloat()) / height;
    // 缩放图片动作
    matrix.postScale(scaleWidth, scaleHeight);
    var bitmap = Bitmap.createBitmap(
        this, 0, 0, width,
        height, matrix, true
    );
    return bitmap;
}
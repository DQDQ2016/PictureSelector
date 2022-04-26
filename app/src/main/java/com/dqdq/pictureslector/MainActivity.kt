package com.dqdq.pictureslector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import android.graphics.BitmapFactory


class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 2568
    var pictureSelector: PictureSelector? = null
    private val RC_LOCATION_CONTACTS_PERM = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pictureSelector = findViewById(R.id.pic_selector)
        pictureSelector?.setPictureSelectListener {
            openSysAlbum()
        }

        var permission =
            ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode
            );
        }
    }

    /**
     * 打开系统相册
     */
    private fun openSysAlbum() {
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(
            albumIntent,
            RC_LOCATION_CONTACTS_PERM
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == RC_LOCATION_CONTACTS_PERM) {
                val uri = data!!.data
                var path = getPhotoFromPhotoAlbum.getRealPathFromUri(this, uri)
                val bitmap = BitmapFactory.decodeFile(path)
                pictureSelector?.pushPicture(path,bitmap)
            }
        }
    }
}
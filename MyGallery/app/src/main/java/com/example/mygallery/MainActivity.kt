package com.example.mygallery

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.timer

private const val REQUEST_READ_EXTERNAL_STORAGE = 1000

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //권한 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) { //권한 거부시
                //이전에 권한 거부했었으면 설명
                alert("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다.", "권한이 필요한 이유") {
                    yesButton {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        ) // 권한 요청
                    }
                    noButton { }
                }.show()
            } else ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        } else getAllPhotos()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) getAllPhotos() //권한 허용
                else toast("권한 거부됨")
                return
            }
        }
    }

    private fun getAllPhotos() {
        val fragments = mutableListOf<Fragment>()

        //모든 사진 정보 가져오기
        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,       //가져올 항목 배열
            null,       //조건
            null,   //조건
            "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"
        )    // 정렬. 날짜 내림차순

        // Scoped Storage 대응
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                fragments.add(PhotoFragment.newInstance(contentUri))
            }
        }

        val adapter = MyPagerAcapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter

        timer(period = 3000) {
            runOnUiThread {
                if (viewPager.currentItem < adapter.count - 1) viewPager.currentItem =
                    viewPager.currentItem + 1
                else viewPager.currentItem = 0
            }
        }
    }
}

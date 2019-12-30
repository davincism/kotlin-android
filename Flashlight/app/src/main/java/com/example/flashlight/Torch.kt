package com.example.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class Torch(context: Context) {
    private var cameraId: String? = null
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager //매니저 클래스(안드로이드 시스템에서 제공하는 각종 서비스를 관리)를 생성하는 메서드

    init {
        cameraId = getCameraId()
    }

    fun flashOn() {
        cameraId?.let { cameraManager.setTorchMode(it, true) }
    }

    fun flashOff() {
        cameraId?.let { cameraManager.setTorchMode(it, false) }
    }

    private fun getCameraId(): String? {
        val cameraIds = cameraManager.cameraIdList
        for (id in cameraIds) {
            val info = cameraManager.getCameraCharacteristics(id)
            val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING) //렌즈 방향
            if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) return id
        }
        return null
    }
}
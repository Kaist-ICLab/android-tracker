package dev.iclab.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class PermissionActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher 초기화
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionsResult(permissions)
        }

        // 퍼미션이 필요한 경우 요청
//        val permissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        )
//        if (!hasPermissions(permissions)) {
//            requestPermissionsWithRationale(permissions)
//        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsWithRationale(permissions: Array<String>) {
        val showRationale = permissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (showRationale) {
            // 권한 요청 이유 설명
            showRationaleDialog(permissions)
        } else {
            // 권한 요청 바로 진행
            permissionLauncher.launch(permissions)
        }
    }

    private fun showRationaleDialog(permissions: Array<String>) {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("이 기능을 사용하려면 권한이 필요합니다. 권한을 허용하시겠습니까?")
            .setPositiveButton("허용") { _, _ ->
                // 권한 요청
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton("거부") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        permissions.entries.forEach { entry ->
            val permissionName = entry.key
            val isGranted = entry.value
            if (isGranted) {
                // 퍼미션이 허용된 경우
                Log.d("Permissions", "$permissionName is granted.")
            } else {
                // 퍼미션이 거부된 경우
                Log.d("Permissions", "$permissionName is denied.")
            }
        }
    }
}
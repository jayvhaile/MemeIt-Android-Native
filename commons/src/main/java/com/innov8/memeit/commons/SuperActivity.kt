package com.innov8.memeit.commons

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

abstract class SuperActivity : AppCompatActivity() {
    private val activityResult = mutableMapOf<Int, (Int, Intent?) -> Unit>()
    private val permissionResult = mutableMapOf<Int, (IntArray, Boolean) -> Unit>()


    fun startActivityForResult(intent: Intent?, requestCode: Int, onActivityResult: (Int, Intent?) -> Unit) {
        activityResult[requestCode] = onActivityResult
        super.startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResult[requestCode]?.let {
            activityResult.remove(requestCode)
            it(resultCode, data)
        }
    }

    fun requestPerm(permissions: Array<String>, requestCode: Int, onPermissionResult: (IntArray, Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val granted = permissions.map { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
                    .all { it }
            if (granted)
                onPermissionResult(IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }, true)
            else {
                permissionResult[requestCode] = onPermissionResult
                requestPermissions(permissions, requestCode)
            }
        } else onPermissionResult(IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }, true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResult[requestCode]?.let {
            permissionResult.remove(requestCode)
            it(grantResults, grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED })
        }
    }
}
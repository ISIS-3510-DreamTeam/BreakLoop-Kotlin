package com.dreamteam.breakloop.data.system

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Process
import androidx.core.content.ContextCompat

class PermissionsDataSource (
    private val context: Context
){
    fun hasUsageAccess(): Boolean {
        val appsOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var mode = AppOpsManager.MODE_ERRORED
        if (Build.VERSION.SDK_INT >=29){
            mode = appsOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else{
            mode = appsOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }

        val answer: Boolean = when (mode){
            AppOpsManager.MODE_ALLOWED -> true
            AppOpsManager.MODE_DEFAULT -> context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS ) == PackageManager.PERMISSION_GRANTED
            else -> false
        }
        return answer
    }
    fun hasLocationAccess(): Boolean{
        return ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    }
}
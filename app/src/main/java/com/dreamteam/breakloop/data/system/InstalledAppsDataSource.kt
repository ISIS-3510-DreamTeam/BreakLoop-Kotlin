package com.dreamteam.breakloop.data.system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class InstalledAppsDataSource (
    private val context: Context
){
    fun obtainIncludedPackages(): Set<String> {
        val packageManager = context.packageManager
        val intentLauncher = Intent(Intent.ACTION_MAIN)
        intentLauncher.addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = packageManager.queryIntentActivities(intentLauncher,0)
        val packages : MutableSet<String> = mutableSetOf()
        for (activity in activities){
            packages.add(activity.activityInfo.packageName)
        }

        val intentHome = Intent(Intent.ACTION_MAIN)
        intentHome.addCategory(Intent.CATEGORY_HOME)
        val activityHome = packageManager.resolveActivity(intentHome, PackageManager.MATCH_DEFAULT_ONLY)
        if (activityHome != null){
            packages.remove(activityHome.activityInfo.packageName)
        }
        return packages
    }
}
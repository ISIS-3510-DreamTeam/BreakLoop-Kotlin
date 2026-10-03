package com.dreamteam.breakloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dreamteam.breakloop.background.worker.UsageAggregationWorker
import com.dreamteam.breakloop.background.worker.UsageWorkScheduler
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        UsageWorkScheduler.schedule(this)
        WorkManager.getInstance(this).enqueueUniqueWork("usage_aggregation_on_open",
            ExistingWorkPolicy.KEEP,OneTimeWorkRequestBuilder<UsageAggregationWorker>().build(),)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BreakLoopTheme {
                BreakLoopApp()
            }
        }
    }
}
// TODO: acá debe vivir el splash

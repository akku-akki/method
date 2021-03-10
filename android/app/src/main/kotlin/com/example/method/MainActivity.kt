package com.example.method
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class MainActivity: FlutterActivity() {
    private val CHANNEL = "dailyLimit"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
            call, result ->
            if (call.method == "dailyLimit") {
                 val value = getDailyUsage()
//                val batteryLevel = getBatteryLevel()
//                if (batteryLevel != -1) {
//                    result.success(batteryLevel)
//                } else {
//                    result.error("UNAVAILABLE", "Battery level not available.", null)
//                }
            }
            else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun getDailyUsage() : Boolean {
        val limit = 900;
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = System.currentTimeMillis()
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)
//        val stats = usageStatsManager.queryUsageStats(0,)

        val total = Duration.ofMillis(stats.values.map { it.totalTimeInForeground }.sum())
        return total.seconds>limit

    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }
}
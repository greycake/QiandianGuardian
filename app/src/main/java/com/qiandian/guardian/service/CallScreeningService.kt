package com.qiandian.guardian.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.qiandian.guardian.R
import com.qiandian.guardian.model.BlockRecord
import com.qiandian.guardian.repository.BlockRecordRepository
import com.qiandian.guardian.util.GuizhouNumberDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatch.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 电话拦截服务
 * 使用CallScreeningService拦截来电
 */
class CallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Main)
    private lateinit var repository: BlockRecordRepository

    companion object {
        private const val TAG = "CallScreeningService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "call_blocking_channel"
    }

    override fun onCreate() {
        super.onCreate()
        repository = BlockRecordRepository(applicationContext)
        createNotificationChannel()
        Log.d(TAG, "CallScreeningService created")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: return

        Log.d(TAG, "Incoming call from: $phoneNumber")

        // 检查是否启用拦截
        if (!isBlockEnabled()) {
            Log.d(TAG, "Block is disabled, allowing call")
            return
        }

        // 检查是否为贵州号码
        if (GuizhouNumberDetector.isGuizhouNumber(phoneNumber)) {
            Log.d(TAG, "Guizhou number detected, blocking: $phoneNumber")

            // 拦截电话
            val response = Call.Response.Builder()
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()

            respondToCall(callDetails, response)

            // 记录拦截
            val record = BlockRecord(
                phoneNumber = phoneNumber,
                timestamp = System.currentTimeMillis(),
                type = "call"
            )

            serviceScope.launch {
                repository.addBlockRecord(record)
                Log.d(TAG, "Blocked call recorded: $phoneNumber")
            }

            // 显示通知
            showBlockNotification(phoneNumber)
        } else {
            Log.d(TAG, "Not a Guizhou number, allowing call: $phoneNumber")
        }
    }

    /**
     * 检查是否启用拦截
     */
    private fun isBlockEnabled(): Boolean {
        val prefs = getSharedPreferences("block_settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("block_call_enabled", false)
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "电话拦截通知",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "拦截贵州号码来电的通知"
                setShowBadge(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 显示拦截通知
     */
    private fun showBlockNotification(phoneNumber: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("已拦截来电")
            .setContentText("已拦截贵州号码: $phoneNumber")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CallScreeningService destroyed")
    }
}

package com.qiandian.guardian.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.qiandian.guardian.model.BlockRecord
import com.qiandian.guardian.repository.BlockRecordRepository
import com.qiandian.guardian.util.GuizhouNumberDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 短信拦截广播接收器
 */
class SmsReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Main)

    companion object {
        private const val TAG = "SmsReceiver"
        const val SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        if (intent.action != SMS_RECEIVED_ACTION) {
            return
        }

        Log.d(TAG, "SMS received")

        // 检查是否启用短信拦截
        if (!isSmsBlockEnabled(context)) {
            Log.d(TAG, "SMS block is disabled")
            return
        }

        // 获取短信内容
        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return

        val messages = mutableListOf<SmsMessage>()
        for (pdu in pdus) {
            val pduBytes = pdu as? ByteArray ?: continue
            val format = bundle.getString("format")
            val message = if (format != null) {
                SmsMessage.createFromPdu(pduBytes, format)
            } else {
                SmsMessage.createFromPdu(pduBytes)
            }
            if (message != null) {
                messages.add(message)
            }
        }

        if (messages.isEmpty()) {
            return
        }

        // 处理每条短信
        for (message in messages) {
            val phoneNumber = message.originatingAddress ?: continue
            val messageBody = message.messageBody ?: ""

            Log.d(TAG, "SMS from: $phoneNumber, content: $messageBody")

            // 检查是否为贵州号码
            if (GuizhouNumberDetector.isGuizhouNumber(phoneNumber)) {
                Log.d(TAG, "Guizhou SMS detected, blocking: $phoneNumber")

                // 拦截短信
                abortBroadcast()

                // 记录拦截
                val record = BlockRecord(
                    phoneNumber = phoneNumber,
                    timestamp = System.currentTimeMillis(),
                    content = messageBody,
                    type = "sms"
                )

                val repository = BlockRecordRepository(context)
                receiverScope.launch {
                    repository.addBlockRecord(record)
                    Log.d(TAG, "Blocked SMS recorded: $phoneNumber")
                }
            } else {
                Log.d(TAG, "Not a Guizhou number, allowing SMS: $phoneNumber")
            }
        }
    }

    /**
     * 检查是否启用短信拦截
     */
    private fun isSmsBlockEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("block_settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("block_sms_enabled", false)
    }
}

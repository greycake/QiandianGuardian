package com.qiandian.guardian.model

import java.text.SimpleDateFormat
import java.util.*

/**
 * 拦截记录数据类
 */
data class BlockRecord(
    val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val content: String = "",
    val type: String // "call" or "sms"
) {
    /**
     * 格式化时间
     */
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * 获取类型描述
     */
    fun getTypeDescription(): String {
        return if (type == "call") "来电" else "短信"
    }
}

package com.qiandian.guardian.repository

import android.content.ContentValues
import android.content.Context
import com.qiandian.guardian.database.BlockRecordDatabase
import com.qiandian.guardian.model.BlockRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 拦截记录仓库
 */
class BlockRecordRepository(context: Context) {

    private val database = BlockRecordDatabase(context)

    /**
     * 添加拦截记录
     */
    suspend fun addBlockRecord(record: BlockRecord) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(BlockRecordDatabase.COLUMN_PHONE_NUMBER, record.phoneNumber)
            put(BlockRecordDatabase.COLUMN_TIME, record.timestamp)
            put(BlockRecordDatabase.COLUMN_CONTENT, record.content)
            put(BlockRecordDatabase.COLUMN_TYPE, record.type)
        }

        val table = if (record.type == "call") {
            BlockRecordDatabase.TABLE_CALLS
        } else {
            "blocked_sms"
        }

        database.writableDatabase.insert(table, null, values)
    }

    /**
     * 获取所有拦截记录
     */
    suspend fun getAllRecords(): List<BlockRecord> = withContext(Dispatchers.IO) {
        val records = mutableListOf<BlockRecord>()

        // 获取电话拦截记录
        database.readableDatabase.query(
            BlockRecordDatabase.TABLE_CALLS,
            null,
            null,
            null,
            null,
            null,
            "${BlockRecordDatabase.COLUMN_TIME} DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                records.add(BlockRecord(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_ID)),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_PHONE_NUMBER)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_TIME)),
                    content = "",
                    type = "call"
                ))
            }
        }

        // 获取短信拦截记录
        database.readableDatabase.query(
            "blocked_sms",
            null,
            null,
            null,
            null,
            null,
            "${BlockRecordDatabase.COLUMN_TIME} DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                records.add(BlockRecord(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_ID)),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_PHONE_NUMBER)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_TIME)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(BlockRecordDatabase.COLUMN_CONTENT)) ?: "",
                    type = "sms"
                ))
            }
        }

        // 按时间排序
        records.sortedByDescending { it.timestamp }
    }

    /**
     * 清空所有记录
     */
    suspend fun clearAllRecords() = withContext(Dispatchers.IO) {
        database.clearAllRecords()
    }

    /**
     * 获取记录数量
     */
    suspend fun getRecordCount(): Int = withContext(Dispatchers.IO) {
        database.getRecordCount()
    }
}

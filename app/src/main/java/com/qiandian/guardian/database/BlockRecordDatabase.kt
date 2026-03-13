package com.qiandian.guardian.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 拦截记录数据库
 */
class BlockRecordDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "block_records.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CALLS = "blocked_calls"
        const val TABLE_SMS = { "blocked_sms" }

        // 表字段
        const val COLUMN_ID = "id"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_TIME = "timestamp"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TYPE = "type" // call or sms
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建拦截电话记录表
        val createCallsTable = """
            CREATE TABLE $TABLE_CALLS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PHONE_NUMBER TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_TYPE TEXT DEFAULT 'call'
            )
        """.trimIndent()

        // 创建拦截短信记录表
        val createSmsTable = """
            CREATE TABLE blocked_sms (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PHONE_NUMBER TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_CONTENT TEXT,
                $COLUMN_TYPE TEXT DEFAULT 'sms'
            )
        """.trimIndent()

        db.execSQL(createCallsTable)
        db.execSQL(createSmsTable)

        // 创建索引
        db.execSQL("CREATE INDEX idx_calls_time ON $TABLE_CALLS($COLUMN_TIME)")
        db.execSQL("CREATE INDEX idx_sms_time ON blocked_sms($COLUMN_TIME)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 升级逻辑
        if (oldVersion < 2) {
            // 未来版本升级
        }
    }

    /**
     * 清空所有记录
     */
    fun clearAllRecords() {
        writableDatabase.delete(TABLE_CALLS, null, null)
        writableDatabase.delete("blocked_sms", null, null)
    }

    /**
     * 获取拦截记录数量
     */
    fun getRecordCount(): Int {
        val callCount = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_CALLS",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }

        val smsCount = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM blocked_sms",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }

        return callCount + smsCount
    }
}

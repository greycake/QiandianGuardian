package com.qiandian.guardian

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
Android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiandian.guardian.adapter.BlockRecordAdapter
import com.qiandian.guardian.model.BlockRecord
import com.qiandian.guardian.repository.BlockRecordRepository
import kotlinx.coroutines.launch

/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {

    private lateinit var switchBlockCall: Switch
    private lateinit var switchBlockSms: Switch
    private lateinit var tvRecordCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearRecords: Button
    private lateinit var btnAbout: Button

    private lateinit var repository: BlockRecordRepository
    private lateinit var adapter: BlockRecordAdapter

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private const val REQUEST_CODE_DEFAULT_CALL_APP = 1002

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_SMS
        )

        private val PERMISSIONS_POST_13 = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = BlockRecordRepository(this)
        initViews()
        setupListeners()
        checkPermissions()
        loadRecords()
    }

    private fun initViews() {
        switchBlockCall = findViewById(R.id.switch_block_call)
        switchBlockSms = findViewById(R.id.switch_block_sms)
        tvRecordCount = findViewById(R.id.tv_record_count)
        recyclerView = findViewById(R.id.recycler_view)
        btnClearRecords = findViewById(R.id.btn_clear_records)
        btnAbout = findViewById(R.id.btn_about)

        // 设置RecyclerView
        adapter = BlockRecordAdapter { record ->
            showRecordDetail(record)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 加载设置
        val prefs = getSharedPreferences("block_settings", MODE_PRIVATE)
        switchBlockCall.isChecked = prefs.getBoolean("block_call_enabled", false)
        switchBlockSms.isChecked = prefs.getBoolean("block_sms_enabled", false)
    }

    private fun setupListeners() {
        switchBlockCall.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("block_settings", MODE_PRIVATE)
            prefs.edit().putBoolean("block_call_enabled", isChecked).apply()

            if (isChecked) {
                checkDefaultCallApp()
                Toast.makeText(this, R.string.block_call_enabled, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.block_call_disabled, Toast.LENGTH_SHORT).show()
            }
        }

        switchBlockSms.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("block_settings", MODE_PRIVATE)
            prefs.edit().putBoolean("block_sms_enabled", isChecked).apply()

            if (isChecked) {
                Toast.makeText(this, R.string.block_sms_enabled, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.block_sms_disabled, Toast.LENGTH_SHORT).show()
            }
        }

        btnClearRecords.setOnClickListener {
            showClearRecordsDialog()
        }

        btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    /**
     * 检查权限
     */
    private fun checkPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            missingPermissions.addAll(PERMISSIONS_POST_13.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            })
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /**
     * 检查并请求设置为默认通话应用
     */
    private fun checkDefaultCallApp() {
        val roleManager = getSystemService(android.app.role.RoleManager::class.java)
        if (roleManager.isRoleAvailable(android.app.role.RoleManager.ROLE_CALL_SCREENING)) {
            if (!roleManager.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING)) {
                val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_CALL_SCREENING)
                startActivityForResult(intent, REQUEST_CODE_DEFAULT_CALL_APP)
            }
        }
    }

    /**
     * 加载拦截记录
     */
    private fun loadRecords() {
        lifecycleScope.launch {
            val records = repository.getAllRecords()
            adapter.submitList(records)

            val count = repository.getRecordCount()
            tvRecordCount.text = getString(R.string.title_blocked_calls) + " ($count)"
        }
    }

    /**
     * 显示记录详情
     */
    private fun showRecordDetail(record: BlockRecord) {
        val message = buildString {
            append("号码: ${record.phoneNumber}\n")
            append("类型: ${record.getTypeDescription()}\n")
            append("时间: ${record.getFormattedTime()}\n")
            if (record.content.isNotEmpty()) {
                append("内容: ${record.content}")
            }
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.guizhou_number)
            .setMessage(message)
            .setPositiveButton(R.string.yes, null)
            .show()
    }

    /**
     * 显示清空记录对话框
     */
    private fun showClearRecordsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_records)
            .setMessage(R.string.confirm_clear)
            .setPositiveButton(R.string.yes) { _, _ ->
                lifecycleScope.launch {
                    repository.clearAllRecords()
                    loadRecords()
                    Toast.makeText(this@MainActivity, "记录已清空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    /**
     * 显示关于对话框
     */
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about)
            .setMessage(R.string.about_content)
            .setPositiveButton(R.string.yes, null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!allGranted) {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DEFAULT_CALL_APP) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "已设置为默认通话应用", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未设置为默认通话应用，拦截功能可能受限", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }
}

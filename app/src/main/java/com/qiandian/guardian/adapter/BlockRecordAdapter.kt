package com.qiandian.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qiandian.guardian.R
import com.qiandian.guardian.model.BlockRecord

/**
 * 拦截记录适配器
 */
class BlockRecordAdapter(
    private val onItemClick: (BlockRecord) -> Unit
) : ListAdapter<BlockRecord, BlockRecordAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tv_phone_number)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_block_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.tvPhoneNumber.text = record.phoneNumber
        holder.tvTime.text = record.getFormattedTime()
        holder.tvType.text = record.getTypeDescription()

        holder.itemView.setOnClickListener {
            onItemClick(record)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BlockRecord>() {
        override fun areItemsTheSame(oldItem: BlockRecord, newItem: BlockRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BlockRecord, newItem: BlockRecord): Boolean {
            return oldItem == newItem
        }
    }
}

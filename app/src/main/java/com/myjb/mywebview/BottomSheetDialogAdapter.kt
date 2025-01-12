package com.myjb.mywebview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myjb.mywebview.databinding.BottomSheetDialogItemBinding

private const val TAG = "BottomSheetDialogAdapter"

class BottomSheetDialogAdapter(private val unit: (String) -> Unit) : ListAdapter<String, RecyclerView.ViewHolder>(BottomSheetDialogItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BottomSheetDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BottomSheetViewHolder).bind(getItem(position), unit)
    }

    class BottomSheetViewHolder(private val binding: BottomSheetDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, unit: (String) -> Unit) {
            binding.text = item
            binding.root.setOnClickListener { onSingleClick(it, unit) }
            binding.executePendingBindings()
        }

        private fun onSingleClick(view: View?, unit: (String) -> Unit) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) {
                return
            }

            if (binding.root == view) {
                Log.e(TAG, "[onSingleClick] text : ${binding.text}")
                unit(binding.text!!)
            }
        }
    }
}

class BottomSheetDialogItemDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem == newItem
    }
}
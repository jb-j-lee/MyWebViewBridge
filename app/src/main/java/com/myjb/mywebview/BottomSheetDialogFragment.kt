package com.myjb.mywebview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myjb.mywebview.databinding.BottomSheetBinding

class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "BottomSheetDialogFragment"
    }

    private val binding: BottomSheetBinding by lazy {
        BottomSheetBinding.inflate(layoutInflater)
    }

    private val bottomSheetDialogAdapter = BottomSheetDialogAdapter(unit = {})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.bottom_sheet_close) {
                dismiss()
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)

            bottomSheetDialogAdapter.submitList(
                resources.getStringArray(R.array.arrays_menu).toList()
            )
            adapter = bottomSheetDialogAdapter
        }
    }
}
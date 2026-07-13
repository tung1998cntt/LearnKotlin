package com.example.learnkotlin.presentation.base.baseDropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.learnkotlin.R

class MultiSelectDropdownAdapter<T>(
    context: Context,
    private val items: List<T>,
    private val selectedItems: MutableSet<T>, // Sử dụng Set để tránh trùng lặp
    private val textProvider: (T) -> String?,
    private val allItem: T? = null // Item đại diện cho "Tất cả"
) : ArrayAdapter<T>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_dropdown_multi, parent, false)

        val tv = view.findViewById<AppCompatTextView>(R.id.tvItemTitle)
        val ivCheckbox = view.findViewById<AppCompatImageView>(R.id.ivCheckboxChecked) // Giả định ID checkbox của bạn
        ivCheckbox.visibility = View.VISIBLE
        val item = items[position]
        val isSelected = selectedItems.contains(item)

        tv.text = textProvider(item)

        // Cập nhật giao diện row (background hồng nhạt nếu chọn)
        view.isActivated = isSelected

        // Cập nhật icon checkbox dựa trên trạng thái
        ivCheckbox.setImageResource(
            if (isSelected) R.drawable.ic_form_checkbox else R.drawable.ic_empty_checkbox
        )

        return view
    }

    fun updateData() {
        notifyDataSetChanged()
    }
}
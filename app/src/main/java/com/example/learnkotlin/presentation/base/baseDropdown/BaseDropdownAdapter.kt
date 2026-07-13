package com.example.learnkotlin.presentation.base.baseDropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.example.learnkotlin.R

class BaseDropdownAdapter<T>(
    context: Context,
    private val items: List<T>,
    private var selected: T?,
    private val textProvider: (T) -> String?,
    private val selectedComparator: (T, T?) -> Boolean = { item, sel -> item == sel }
) : ArrayAdapter<T>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_dropdown, parent, false)

        val tv = view.findViewById<AppCompatTextView>(R.id.tvItemTitle)
        val item = items[position] ?: return view
        val activated = selectedComparator(item, selected)
        tv.isActivated  = activated
        tv.text = textProvider(item)
        return view
    }

    fun submitList(items: List<T>) {
        clear()
        addAll(items)
        notifyDataSetChanged()
    }

    fun setSelected(value: T?) {
        selected = value
        notifyDataSetChanged()
    }
}

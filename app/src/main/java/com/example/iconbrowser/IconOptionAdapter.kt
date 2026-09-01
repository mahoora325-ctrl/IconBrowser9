package com.example.iconbrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.iconbrowser.databinding.ItemIconOptionBinding

class IconOptionAdapter(
    private val options: List<IconOption>,
    initialSelectedId: String,
    private val onSelect: (IconOption) -> Unit
) : RecyclerView.Adapter<IconOptionAdapter.OptionViewHolder>() {

    private var selectedId: String = initialSelectedId

    inner class OptionViewHolder(val binding: ItemIconOptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemIconOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        val isSelected = option.id == selectedId

        holder.binding.optionIcon.setImageResource(option.iconRes)
        holder.binding.optionLabel.text = option.label
        holder.binding.optionCheck.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        holder.binding.root.setBackgroundResource(
            if (isSelected) R.drawable.row_background_selected else R.drawable.row_background
        )

        holder.binding.root.setOnClickListener {
            val previous = selectedId
            selectedId = option.id
            notifyItemChanged(options.indexOfFirst { it.id == previous })
            notifyItemChanged(position)
            onSelect(option)
        }
    }

    override fun getItemCount(): Int = options.size
}

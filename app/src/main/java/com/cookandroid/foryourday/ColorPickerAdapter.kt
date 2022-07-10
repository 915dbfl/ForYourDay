package com.cookandroid.foryourday

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.adapter.ColorViewBinding
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.ColorUtil
import com.github.dhaval2404.colorpicker.util.setVisibility

class ColorPickerAdapter(private val colors: List<String>): RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>(){
    private var color = ""
    private var isDarkColor = false
    private var isTickColorPerCard = false
    private var colorShape = ColorShape.CIRCLE

    init {
        val darkColors = colors.count { ColorUtil.isDarkColor(it) }
        isDarkColor = (darkColors * 2) >= colors.size
    }

    fun getSelectedColor() = color


    fun getItem(position: Int) = colors[position]

    override fun getItemCount() = colors.size


    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view){
        private val colorView = view.findViewById<CardView>(R.id.colorView)
        private val checkIcon = view.findViewById<AppCompatImageView>(R.id.checkIcon)

        init {
            view.setOnClickListener {
                val newIndex = it.tag as Int
                val color = getItem(newIndex)

                val oldIndex = colors.indexOf(this@ColorPickerAdapter.color)
                this@ColorPickerAdapter.color = color

                notifyItemChanged(oldIndex)
                notifyItemChanged(newIndex)
            }
        }

        fun bind(position: Int){
            val color = getItem(position)

            view.tag = position

            ColorViewBinding.setBackgroundColor(colorView, color)
            ColorViewBinding.setCardRadius(colorView, colorShape)

            val isChecked = color == this@ColorPickerAdapter.color
            checkIcon.setVisibility(isChecked)

            var darkColor = isDarkColor
            if(isTickColorPerCard){
                darkColor = ColorUtil.isDarkColor(color)
            }

            checkIcon.setColorFilter(if(darkColor) Color.WHITE else Color.BLACK)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerAdapter.ViewHolder {
        val view = ColorViewBinding.inflateAdapterItemView(parent)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.ColorPickerAdapter
import com.cookandroid.foryourday.R
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.ColorUtil

class ColorPickerDialog(val context: Context, private val colorListener: ColorListener?){
    private val dialog = Dialog(context)

    class Builder(val context: Context){
        private var colorListener: ColorListener? = null

        fun setColorListener(listener: (Int, String) -> Unit): Builder {
            this.colorListener = object : ColorListener {
                override fun onColorSelected(color: Int, colorHex: String) {
                    listener(color, colorHex)
                }
            }
            return this
        }

        fun build(): ColorPickerDialog{
            return ColorPickerDialog(context, colorListener)
        }

        fun show(){
            build().show()
        }
    }
    fun show(){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_color_picker)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val colorPicker = dialog.findViewById<RecyclerView>(R.id.color_picker)
        val btnPickerCancel = dialog.findViewById<TextView>(R.id.btn_picker_cancel)
        val btnPickerComplete = dialog.findViewById<TextView>(R.id.btn_picker_complete)

        val colorList = ColorUtil.getColors(context, ColorSwatch._300.value)
        val adapter = ColorPickerAdapter(colorList)

        colorPicker.adapter = adapter

        btnPickerCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnPickerComplete.setOnClickListener {
            val color = adapter.getSelectedColor()
            if(color.isNotBlank()){
                colorListener?.onColorSelected(ColorUtil.parseColor(color), color)
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}
package com.cookandroid.foryourday.todo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import java.util.ArrayList
import java.util.HashMap

class TodoRecyclerView: LinearLayout {
    lateinit var todoCategoryRecycler: RecyclerView

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs){
        init(context!!)
    }
    private fun init(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.todo_recyclerview, this)
        todoCategoryRecycler = findViewById(R.id.todo_recycler_category)

        val todoItemRecyclerViewAdapter = TodoCategoryRecyclerViewAdapter(getSampleData(), context)
        todoCategoryRecycler.layoutManager = LinearLayoutManager(context)
        todoCategoryRecycler.adapter = todoItemRecyclerViewAdapter
    }

    private fun getSampleData(): ArrayList<HashMap<String, ArrayList<String>>>{
        val temptdata = ArrayList<String>()
        temptdata.add("안드로이드 개발하기")
        temptdata.add("시프 과제 마무리하기")
        val tempdata2 = ArrayList<String>()
        tempdata2.add("삼시세끼 꼭 챙겨먹기")
        tempdata2.add("물 세컵 마시기")
        val hash1 = HashMap<String, ArrayList<String>>()
        val hash2 = HashMap<String, ArrayList<String>>()
        hash1.put("중요", temptdata)
        hash2.put("루틴", tempdata2)
        val dataSet = ArrayList<HashMap<String, ArrayList<String>>>()
        dataSet.add(hash1)
        dataSet.add(hash2)

        return dataSet
    }
}
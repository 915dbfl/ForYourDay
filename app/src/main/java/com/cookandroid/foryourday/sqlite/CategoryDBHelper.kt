package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CategoryDBHelper(context: Context) : SQLiteOpenHelper(context, "Categories.db", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = """
            create table CategoriesTable
            (id integer primary key,
            title text not null,
            value text not null)
        """.trimIndent()

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}
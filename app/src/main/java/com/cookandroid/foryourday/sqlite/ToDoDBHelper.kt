package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDoDBHelper(context: Context) : SQLiteOpenHelper(context, "Todos.db", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = """
            create table TodosTable
            (id integer primary key,
            date String not null,
            content text not null,
            complete integer not null,
            categoryId integer not null)
        """.trimIndent()

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}
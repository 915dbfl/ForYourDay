package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DDayDBHelper(context: Context) : SQLiteOpenHelper(context, "DDays.db", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = """
            create table DDaysTable
            (id integer primary key,
            userId integer not null,
            main boolean not null,
            categoryId integer not null,
            ddate String not null,
            content String not null)
        """.trimIndent()

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}
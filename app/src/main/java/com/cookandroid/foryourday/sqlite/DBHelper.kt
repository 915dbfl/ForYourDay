package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper: SQLiteOpenHelper {

    constructor(context: Context): super(context, "User.db", null, 1)

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = """
            create table UserTable
            (accessToken text primary key,
             refreshToken text not null,
             userId integer not null,
             userName text not null,
             email text not null)
        """.trimIndent()

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}
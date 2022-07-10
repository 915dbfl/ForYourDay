package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserInfoDBHelper(context: Context) : SQLiteOpenHelper(context, "User.db", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = """
            create table UserTable
            (userId integer primary key,
            email text not null,
            userName text not null,
            imagePath text,
            accessToken text not null,
            refreshToken text not null)
        """.trimIndent()

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int){
        TODO("Not yet implemented")
    }
}
package com.example.loginpage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

abstract class DBHelper(context: Context, DATABASE_NAME: String?, DATABASE_VERSION: Int) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val COLUMN_USER_PASSWORD="password"
        private const val COLUMN_USER_NAME="username"
        private const val COLUMN_USER_ID="_id"
        private const val TABLE_USER="users"
        private const val COLUMN_USER_EMAIL="email"
    }
     val CREATE_USER_TABLE=("CREATE TABLE"+TABLE_USER+"("+COLUMN_USER_ID+"INTEGER PRIMARY KEY AUTOINCREMENT,"+COLUMN_USER_NAME+"TEXT,"+COLUMN_USER_EMAIL+"TEXT,"+COLUMN_USER_PASSWORD+"TEXT"+")")

    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(CREATE_USER_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL(this.DROP_USER_TABLE)
            onCreate(db)
        }
    }
    fun addUser(username: String, password: String) {
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, username)
        values.put(COLUMN_USER_PASSWORD, password)
        val db = this.writableDatabase
        db.insert(TABLE_USER, null, values)
        db.close()
    }
    @SuppressLint("Range")
    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_PASSWORD),
            "$COLUMN_USER_NAME=?",
            arrayOf(username),
            null,
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD))
            User(id, username, password)
        } else {
            null
        }
    }

    data class User(val id: Int, val username: String, val password: String)

}
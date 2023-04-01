package com.example.loginpage.others

import android.content.Context
import com.example.loginpage.data.UserData
import com.google.gson.Gson


class SharedPref(context: Context) {

    val sharedpref = context.getSharedPreferences("attendancemark_pref", Context.MODE_PRIVATE)
    val editor = sharedpref.edit()

    val USERDATA = "userdata"
    val USERLOGIN = "userlogin"


    fun setUserData(userData: UserData){
        val gson =  Gson()
        val json = gson.toJson(userData)
        editor.putString(USERDATA, json)
        editor.putBoolean(USERLOGIN,true)
        editor.commit()
    }

    fun setUserLoginStatus(status : Boolean) {
        editor.apply {
            putBoolean(USERLOGIN,status)
            apply()
        }
    }

    fun getUserLoginStatus() : Boolean{
        return sharedpref.getBoolean(USERLOGIN,false)
    }

    fun getUserData() : UserData{
        val gson = Gson()
        val json = sharedpref.getString(USERDATA, "null")
        return gson.fromJson(json, UserData::class.java)
    }





}
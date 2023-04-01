package com.example.loginpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginpage.adapter.JoinedMemberAdapter
import com.example.loginpage.data.UserData
import com.example.loginpage.databinding.ActivityHomeBinding
import com.example.loginpage.databinding.ActivityStudent1Binding
import com.example.loginpage.firebase.MainViewModel
import com.example.loginpage.others.Constants

class StudentActivity1 : AppCompatActivity() {

    lateinit var binding : ActivityStudent1Binding
    lateinit var viewmodel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityStudent1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        val data = Constants.curClassData

        val joinedClassAdapter = JoinedMemberAdapter()

        binding.rvJoinedMembers.adapter = joinedClassAdapter
        binding.rvJoinedMembers.layoutManager = LinearLayoutManager(this)

        joinedClassAdapter.membersList = getMemberNames(data.joinedMembers)

        viewmodel.getAttendanceHistory(data.uniqueName)


        viewmodel.attendanceHistoryLive.observe(this, androidx.lifecycle.Observer {
            binding.tvTotalclassattended.text = "Total classes attended : ${it.size}"
        })

        viewmodel.errorAttendanceHistoryLive.observe(this, androidx.lifecycle.Observer {

        })


    }

    private fun getMemberNames(joinedMembers: List<UserData>): List<String> {

        return  joinedMembers.map {
            it.fullName
        }

    }


}
package com.example.loginpage.data

data class AttendanceHistoryData(
    val id : String = "",
    val presentMembers : List<String> = listOf()
)

data class AttendanceData(
    val name : String = ""
)

const val PRESENT = "Present"
const val ABSENT = "Absent"
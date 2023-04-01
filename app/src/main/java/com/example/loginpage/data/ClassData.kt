package com.example.loginpage.data

import com.google.firebase.firestore.auth.User

data class ClassData(
    val className: String = "",
    val subjectName: String = "" ,
    val uniqueName: String = "",
    val joinedMembers : List<UserData> = listOf(),
    val joinedMembeIds : List<String> = listOf(),
    val creatorId : String = ""
)

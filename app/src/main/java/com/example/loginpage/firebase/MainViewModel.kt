package com.example.loginpage.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginpage.data.AttendanceHistoryData
import com.example.loginpage.data.ClassData
import com.example.loginpage.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel() {


    private val firestore = FirebaseFirestore.getInstance()

    private val usersCollection = firestore.collection("users")
    private val classesCollection = firestore.collection("classes")


    private var _userCreatedLive = MutableLiveData<UserData>()
    var userCreatedLive : LiveData<UserData> = _userCreatedLive
    private var _errorUserCreatedLive = MutableLiveData<String>()
    var errorUserCreatedLive : LiveData<String> = _errorUserCreatedLive


    fun createUser(userData: UserData) =  viewModelScope.launch(Dispatchers.IO) {

        try {

            val users = usersCollection.whereEqualTo("email",userData.email).get().await().toObjects(UserData::class.java)

            if(users.isEmpty()){

                usersCollection.document(userData.id).set(userData).addOnSuccessListener {
                    _userCreatedLive.postValue(userData)
                }.addOnFailureListener {
                    _errorUserCreatedLive.postValue(it.message)
                }

            }else{

                _errorUserCreatedLive.postValue("Email already taken, Please try with some other email")

            }

        } catch (e: Exception) {
            _errorUserCreatedLive.postValue(e.message)
        }
    }

    fun signInUser(email : String, password : String) =  viewModelScope.launch(Dispatchers.IO) {

        try {

            val users = usersCollection.whereEqualTo("email",email).whereEqualTo("password",password).get().await().toObjects(UserData::class.java)
            val users2 = usersCollection.whereEqualTo("username",email).whereEqualTo("password",password).get().await().toObjects(UserData::class.java)

            if(users.isEmpty() && users2.isEmpty()){

                _errorUserCreatedLive.postValue("Invalid Credentials")

            }else{

                if (users.isNotEmpty()){
                    _userCreatedLive.postValue(users[0])
                }else{
                    _userCreatedLive.postValue(users2[0])
                }


            }

        } catch (e: Exception) {

            _errorUserCreatedLive.postValue(e.message)

        }

    }

    private var _classCreatedLive = MutableLiveData<ClassData>()
    var classCreatedLive : LiveData<ClassData> = _classCreatedLive
    private var _errorClassCreatedLive = MutableLiveData<String>()
    var errorClassCreatedLive : LiveData<String> = _errorClassCreatedLive

    fun createClass(classData : ClassData) =  viewModelScope.launch(Dispatchers.IO) {

        try {

            classesCollection.document(classData.uniqueName).set(classData).addOnSuccessListener {
                _classCreatedLive.postValue(classData)
            }.addOnFailureListener {
                _errorClassCreatedLive.postValue(it.message)
            }

        } catch (e: Exception) {
            _errorClassCreatedLive.postValue(e.message)
        }

    }

    private var _markAttendanceLive = MutableLiveData<String>()
    var markAttendanceLive : LiveData<String> = _markAttendanceLive
    private var _errorMarkAttendanceLive = MutableLiveData<String>()
    var errorMarkAttendanceLive : LiveData<String> = _errorMarkAttendanceLive

    fun markAttendance(classId : String,attendanceId : String,attendanceHistoryData: AttendanceHistoryData) =  viewModelScope.launch(Dispatchers.IO) {

        try {

            classesCollection.document(classId).collection("attendance").document(attendanceId).set(attendanceHistoryData).addOnSuccessListener {
                _markAttendanceLive.postValue("Successfully marked")
            }.addOnFailureListener {
                _errorMarkAttendanceLive.postValue(it.message)
            }

        } catch (e: Exception) {
            _errorMarkAttendanceLive.postValue(e.message)
        }

    }



    private var _classJoinedLive = MutableLiveData<ClassData>()
    var classJoinedLive : LiveData<ClassData> = _classJoinedLive
    private var _errorClassJoinedLive = MutableLiveData<String>()
    var errorClassJoinedLive : LiveData<String> = _errorClassJoinedLive

    fun joinClass(classCode: String,userData: UserData) =  viewModelScope.launch(Dispatchers.IO) {

        try {
            val classes = classesCollection.document(classCode).get().await().toObject(ClassData::class.java)
            if (classes != null){

                val joinedMembers = classes.joinedMembers.toMutableSet()
                val joinedMemberIds = classes.joinedMembeIds.toMutableList()

                joinedMembers.add(userData)
                joinedMemberIds.add(userData.id)

                classesCollection.document(classCode).update(
                    mapOf(
                        "joinedMembers" to joinedMembers.toList(),
                        "joinedMembeIds" to joinedMemberIds.toList()
                    )
                ).addOnSuccessListener {
                    _classJoinedLive.postValue(classes!!)
                }.addOnFailureListener {
                    _errorClassJoinedLive.postValue(it.message)
                }

            }else{

                _errorClassJoinedLive.postValue("Sorry, no class found")

            }

        } catch (e: Exception) {
            _errorClassJoinedLive.postValue(e.message)
        }

    }

    private var _classesCreatedLive = MutableLiveData<List<ClassData>>()
    var classesCreatedLive : LiveData<List<ClassData>> = _classesCreatedLive
    private var _errorClassesCreatedLive = MutableLiveData<String>()
    var errorClassesCreatedLive : LiveData<String> = _errorClassesCreatedLive


    fun getCreatedClasses(userId : String) =  viewModelScope.launch(Dispatchers.IO) {

        try {
            val properties = classesCollection.whereEqualTo("creatorId",userId).get().await().toObjects(ClassData::class.java)
            _classesCreatedLive.postValue(properties)
        } catch (e: Exception) {
            _errorClassesCreatedLive.postValue(e.message)
        }

    }

    private var _todayAttendanceLive = MutableLiveData<AttendanceHistoryData>()
    var todayAttendanceLive : LiveData<AttendanceHistoryData> = _todayAttendanceLive
    private var _errorTodayAttendanceLive = MutableLiveData<String>()
    var errorTodayAttendanceLive : LiveData<String> = _errorTodayAttendanceLive


    fun getTodayAttendance(classId : String,attendanceId : String) =  viewModelScope.launch(Dispatchers.IO) {

        try {
            val properties = classesCollection.document(classId).collection("attendance").document(attendanceId).get().await().toObject(AttendanceHistoryData::class.java)
            _todayAttendanceLive.postValue(properties!!)
        } catch (e: Exception) {
            _errorTodayAttendanceLive.postValue(e.message)
        }

    }

    private var _AttendanceHistoryLive = MutableLiveData<List<AttendanceHistoryData>>()
    var attendanceHistoryLive : LiveData<List<AttendanceHistoryData>> = _AttendanceHistoryLive
    private var _errorAttendanceHistoryLive = MutableLiveData<String>()
    var errorAttendanceHistoryLive : LiveData<String> = _errorAttendanceHistoryLive


    fun getAttendanceHistory(classId : String) =  viewModelScope.launch(Dispatchers.IO) {

        try {
            val properties = classesCollection.document(classId).collection("attendance").get().await().toObjects(AttendanceHistoryData::class.java)
            _AttendanceHistoryLive.postValue(properties)
        } catch (e: Exception) {
            _errorAttendanceHistoryLive.postValue(e.message)
        }

    }



    private var _classesJoinedLive = MutableLiveData<List<ClassData>>()
    var classesJoinedLive : LiveData<List<ClassData>> = _classesJoinedLive
    private var _errorClassesJoinedLive = MutableLiveData<String>()
    var errorClassesJoinedLive : LiveData<String> = _errorClassesJoinedLive


    fun getJoinedClasses(userId : String) =  viewModelScope.launch(Dispatchers.IO) {

        try {
            val properties = classesCollection.whereArrayContains("joinedMembeIds",userId).get().await().toObjects(ClassData::class.java)
            _classesJoinedLive.postValue(properties)
        } catch (e: Exception) {
            _errorClassesJoinedLive.postValue(e.message)
        }

    }


}
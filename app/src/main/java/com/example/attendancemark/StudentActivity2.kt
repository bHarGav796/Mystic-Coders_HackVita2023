package com.example.loginpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginpage.adapter.AttendanceAdapter
import com.example.loginpage.adapter.JoinedMemberAdapter
import com.example.loginpage.data.AttendanceHistoryData
import com.example.loginpage.data.UserData
import com.example.loginpage.databinding.ActivityStudent1Binding
import com.example.loginpage.databinding.ActivityStudent2Binding
import com.example.loginpage.firebase.MainViewModel
import com.example.loginpage.others.Constants
import com.example.loginpage.others.MyDialog
import com.example.loginpage.others.SharedPref
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StudentActivity2 : AppCompatActivity() {

    lateinit var binding : ActivityStudent2Binding
    lateinit var sharedPref : SharedPref
    lateinit var userData: UserData
    lateinit var viewmodel: MainViewModel
    lateinit var myDialog: MyDialog
    lateinit var  attendanceAdapter: AttendanceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityStudent2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPref(this)
        myDialog = MyDialog(this)

        userData = sharedPref.getUserData()

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)



        val data = Constants.curClassData

        val joinedClassAdapter = JoinedMemberAdapter()

        attendanceAdapter = AttendanceAdapter()

        binding.rvAttendance.adapter = attendanceAdapter
        binding.rvAttendance.layoutManager = LinearLayoutManager(this)

        binding.rvJoinedMembers.adapter = joinedClassAdapter
        binding.rvJoinedMembers.layoutManager = LinearLayoutManager(this)

        joinedClassAdapter.membersList = getMemberNames(data.joinedMembers)

        viewmodel.getTodayAttendance(data.uniqueName,getTodayDate())

        attendanceAdapter.membersList = getMemberNames(data.joinedMembers)

        setCallbacks()

        binding.btSubmitattendance.setOnClickListener {

            myDialog.showProgressDialogForActivity("Please wait , updating",this)

            viewmodel.markAttendance(data.uniqueName,getTodayDate(),AttendanceHistoryData(getTodayDate(), Constants.presentMembers.toList()))

        }

        binding.btDownload.setOnClickListener {

            myDialog.showProgressDialogForActivity("Please wait , fetching data",this)
            viewmodel.getAttendanceHistory(data.uniqueName)

        }

    }

    private fun setCallbacks() {

        viewmodel.todayAttendanceLive.observe(this, androidx.lifecycle.Observer {

            Constants.presentMembers = it.presentMembers.toMutableSet()
            attendanceAdapter.notifyDataSetChanged()
            //   Navigation.findNavController(requireActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_loginFragment_to_mainFragment)
        })

        viewmodel.errorTodayAttendanceLive.observe(this, androidx.lifecycle.Observer {
            myDialog.showErrorAlertDialog(it)
//            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        })

        viewmodel.markAttendanceLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
         //   Toast.makeText(this,"Successfully submitted",Toast.LENGTH_SHORT).show()
        })

        viewmodel.errorMarkAttendanceLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })


        viewmodel.attendanceHistoryLive.observe(this, androidx.lifecycle.Observer {
            if (it.isEmpty()){
                myDialog.dismissProgressDialog()
                myDialog.showErrorAlertDialog("No records found")
              //  Toast.makeText(this,,Toast.LENGTH_SHORT).show()
            }else{
                exportDatabaseToCSVFile(it)
            }
        })

        viewmodel.errorAttendanceHistoryLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })



    }

    private fun getMemberNames(joinedMembers: List<UserData>): List<String> {

        return  joinedMembers.map {
            it.fullName
        }

    }


    private fun exportDatabaseToCSVFile(list : List<AttendanceHistoryData>) {
   //     val csvFile = generateFile(this, Constants.curClassData.className + ".csv")
        val csvFile = generateFile(this, "${Constants.curClassData.className}.csv")
        if (csvFile != null) {
            exportDataToCSVFile(csvFile,list)
            Toast.makeText(this, "csv generated", Toast.LENGTH_LONG).show()

            myDialog.dismissProgressDialog()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                setDataAndType(FileProvider.getUriForFile(this@StudentActivity2, "com.example.loginpage.provider.app", csvFile), "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivity(intent)

      //   goToFileIntent(this, csvFile)
         //   startActivity(Intent.createChooser(intent, "share by"))
            //  startActivity(intent)
        } else {
            myDialog.dismissProgressDialog()
            Toast.makeText(this, "not generated", Toast.LENGTH_LONG).show()
        }
    }

    fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        val contentUri = FileProvider.getUriForFile(context, "com.example.loginpage.provider.app", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        intent.setDataAndType(contentUri, mimeType)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "share by"))
        return intent
    }

    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.externalCacheDir, fileName)
        csvFile.createNewFile()

        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }

    }

    fun exportDataToCSVFile(csvFile: File,list : List<AttendanceHistoryData>) {
        csvWriter().open(csvFile, append = false) {
            // Header
        //    writeRow(listOf(Constants.curClassData.className))
            writeRow(listOf("SI","Date", "No.of Present students", "Present student names"))
            list.forEachIndexed { index, attendance ->
                writeRow(listOf((index + 1) .toString(), attendance.id, attendance.presentMembers.size, attendance.presentMembers.toString()))
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTodayDate() : String{
        val date = Calendar.getInstance().timeInMillis
        val yearFormat = SimpleDateFormat("dd MMM yy")
        return yearFormat.format(date)
    }

}
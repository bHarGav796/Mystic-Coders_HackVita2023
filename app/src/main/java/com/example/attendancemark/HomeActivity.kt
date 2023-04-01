package com.example.attendancemark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.adapter.ClassAdapter2
import com.example.loginpage.data.ClassData
import com.example.loginpage.data.UserData
import com.example.loginpage.databinding.ActivityHomeBinding
import com.example.loginpage.databinding.ActivityLoginBinding
import com.example.loginpage.firebase.MainViewModel
import com.example.loginpage.others.Constants
import com.example.loginpage.others.MyDialog
import com.example.loginpage.others.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapter2
    private lateinit var joinedClassAdapter: ClassAdapter2
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var classItems = ArrayList<ClassData>()
    private var joinedClassItems = ArrayList<ClassData>()

    private lateinit var class_edt: EditText
    private lateinit var subject_edt: EditText
    private lateinit var Unique_edt: EditText

    lateinit var sharedPref : SharedPref
    lateinit var userData: UserData
    lateinit var viewmodel: MainViewModel
    lateinit var myDialog: MyDialog
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPref(this)
        myDialog = MyDialog(this)

        userData = sharedPref.getUserData()

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)


        fab = findViewById(R.id.fab_main)
        fab.setOnClickListener {
            showDialog2()
        }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        binding.recyclerviewJoinedclass.layoutManager = LinearLayoutManager(this)
        classAdapter = ClassAdapter2()
        joinedClassAdapter = ClassAdapter2()
       // joinedClassAdapter = ClassAdapter(this, joinedClassItems)
        recyclerView.adapter = classAdapter
        binding.recyclerviewJoinedclass.adapter = joinedClassAdapter

        setCallbacks()

        viewmodel.getCreatedClasses(userData.id)
        viewmodel.getJoinedClasses(userData.id)

        classAdapter.setOnItemClickListener {
            Constants.curClassData = it
            startActivity(Intent(this, StudentActivity2::class.java))
        }

        joinedClassAdapter.setOnItemClickListener {
            Constants.curClassData = it
            startActivity(Intent(this, StudentActivity1::class.java))
        }

    }

    private fun showDialog2() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_2, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val create = view.findViewById<Button>(R.id.Create_btn)
        val join = view.findViewById<Button>(R.id.Join_btn)

        create.setOnClickListener {
            showDialog()
            dialog.dismiss()
        }

        join.setOnClickListener {
            showDialog3()
            dialog.dismiss()
        }


    }

    private fun showDialog3() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_3, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val cancel = view.findViewById<Button>(R.id.cancel_btn)
        val addBt = view.findViewById<Button>(R.id.add_btn)
        val uniqueEd = view.findViewById<EditText>(R.id.Unique_edt)


        addBt.setOnClickListener {

            val uniqueCode = uniqueEd.text.toString()

            if (uniqueCode.isEmpty()){
                Toast.makeText(this,"Please enter code before join",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            myDialog.showProgressDialogForActivity("Joining in class...Please wait",this)

            viewmodel.joinClass(uniqueCode,userData)

            //    addClass()
            dialog.dismiss()

        }

        cancel.setOnClickListener { dialog.dismiss() }
    }







    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        class_edt = view.findViewById(R.id.class_edt)
        subject_edt = view.findViewById(R.id.subject_edt)
        Unique_edt = view.findViewById(R.id.Unique_edt)

        val cancel = view.findViewById<Button>(R.id.cancel_btn)
        val add = view.findViewById<Button>(R.id.add_btn)

        cancel.setOnClickListener { dialog.dismiss() }
        add.setOnClickListener {

            val className = class_edt.text.toString()
            val subject = subject_edt.text.toString()
            val uniqueName = Unique_edt.text.toString()

            if (className.isEmpty() || subject.isEmpty() || uniqueName.isEmpty()){
                Toast.makeText(this,"Please enter all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val classData = ClassData(className,subject,uniqueName, creatorId = userData.id )

            myDialog.showProgressDialogForActivity("Class creating...Please wait",this)

            viewmodel.createClass(classData)

        //    addClass()
            dialog.dismiss()
        }
    }


    private fun setCallbacks() {


        viewmodel.classCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            Toast.makeText(this,"Class created successfully",Toast.LENGTH_SHORT).show()
            viewmodel.getCreatedClasses(userData.id)
        })

        viewmodel.errorClassCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })

        viewmodel.classJoinedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            Toast.makeText(this,"Joined successfully",Toast.LENGTH_SHORT).show()
            viewmodel.getJoinedClasses(userData.id)
        })

        viewmodel.errorClassJoinedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })


        viewmodel.classesCreatedLive.observe(this, androidx.lifecycle.Observer {
            binding.progressbarHome.visibility = View.GONE
            classAdapter.membersList = it

            Log.d("HomeActivity","List $it")

        })

        viewmodel.errorClassesCreatedLive.observe(this, androidx.lifecycle.Observer {
            binding.progressbarHome.visibility = View.GONE
            myDialog.showErrorAlertDialog(it)
        })


        viewmodel.classesJoinedLive.observe(this, androidx.lifecycle.Observer {
            binding.progressbarHome.visibility = View.GONE
            joinedClassAdapter.membersList = it
        })

        viewmodel.errorClassesJoinedLive.observe(this, androidx.lifecycle.Observer {
            binding.progressbarHome.visibility = View.GONE
            myDialog.showErrorAlertDialog(it)
        })




    }



}

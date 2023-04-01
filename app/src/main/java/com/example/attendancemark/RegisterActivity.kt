package com.example.attendancemark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.loginpage.data.UserData
import com.example.loginpage.databinding.ActivityRegisterBinding
import com.example.loginpage.firebase.MainViewModel
import com.example.loginpage.others.MyDialog
import com.example.loginpage.others.SharedPref
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var sharedPref : SharedPref
    lateinit var viewmodel: MainViewModel
    lateinit var myDialog: MyDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPref = SharedPref(this)
        myDialog = MyDialog(this)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val fullname = binding.etFullname.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || fullname.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this,"Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword){
                Toast.makeText(this,"Password mismatching", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            myDialog.showProgressDialogForActivity("Registering in... Please wait",this).hashCode()

           val userId = UUID.randomUUID().toString().substring(0,15)

            viewmodel.createUser(UserData(userId,fullname,email,username,password))

        }


        setCallbacks()

    }

    private fun setCallbacks() {


        viewmodel.userCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            sharedPref.setUserData(it)
            startActivity(Intent(this, HomeActivity::class.java))
        })

        viewmodel.errorUserCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })

    }

}

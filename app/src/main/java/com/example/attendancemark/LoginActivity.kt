package com.example.attendancemark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.loginpage.databinding.ActivityLoginBinding
import com.example.loginpage.databinding.ActivityMainBinding
import com.example.loginpage.firebase.MainViewModel
import com.example.loginpage.others.MyDialog
import com.example.loginpage.others.SharedPref

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var sharedPref : SharedPref
    lateinit var viewmodel: MainViewModel
    lateinit var myDialog: MyDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.tvHaventAccount.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        sharedPref = SharedPref(this)
        myDialog = MyDialog(this)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)



        binding.btnLogin.setOnClickListener {

            val usernameOrEmail = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (usernameOrEmail.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please enter all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            myDialog.showProgressDialogForActivity("Logging in... Please wait",this).hashCode()

            viewmodel.signInUser(usernameOrEmail,password)

        }


        setCallbacks()

    }

    private fun setCallbacks() {


        viewmodel.userCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            sharedPref.setUserData(it)
            startActivity(Intent(this, HomeActivity::class.java))
            //   Navigation.findNavController(requireActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_loginFragment_to_mainFragment)
        })

        viewmodel.errorUserCreatedLive.observe(this, androidx.lifecycle.Observer {
            myDialog.dismissProgressDialog()
            myDialog.showErrorAlertDialog(it)
        })

    }

}
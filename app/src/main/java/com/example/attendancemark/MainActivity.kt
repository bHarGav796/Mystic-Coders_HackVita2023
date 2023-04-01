package com.example.attendancemark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.loginpage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

package com.myquill.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Auth.userId(this) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_login)
        val id: EditText = findViewById(R.id.identifierInput)
        val pass: EditText = findViewById(R.id.passwordInput)
        val login: Button = findViewById(R.id.loginBtn)
        val goSignup: Button = findViewById(R.id.goSignupBtn)
        login.setOnClickListener {
            val identifier = id.text.toString().trim()
            val pwd = pass.text.toString().trim()
            if (identifier.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "enter credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val user = withContext(Dispatchers.IO) {
                        AppDatabase.get(this@LoginActivity).userDao().findByEmailOrUsernameAndPassword(identifier, pwd)
                    }
                    if (user == null) {
                        Toast.makeText(this@LoginActivity, "invalid credentials", Toast.LENGTH_SHORT).show()
                    } else {
                        Auth.setUser(this@LoginActivity, user.id)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (t: Throwable) {
                    Toast.makeText(this@LoginActivity, "login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        goSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }
}
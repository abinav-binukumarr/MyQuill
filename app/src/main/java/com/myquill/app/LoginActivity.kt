package com.myquill.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val identifier: EditText = findViewById(R.id.identifierInput)
        val password: EditText = findViewById(R.id.passwordInput)
        val login: Button = findViewById(R.id.loginBtn)
        val goSignup: Button = findViewById(R.id.goSignupBtn)

        login.setOnClickListener {
            val id = identifier.text.toString().trim()
            val pass = password.text.toString().trim()
            if (id.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "enter email/username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    AppDatabase.get(this@LoginActivity).userDao().findByEmailOrUsernameAndPassword(id, pass)
                }
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "invalid credentials", Toast.LENGTH_SHORT).show()
                } else {
                    Auth.setUser(this@LoginActivity, user.id)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }

        goSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}

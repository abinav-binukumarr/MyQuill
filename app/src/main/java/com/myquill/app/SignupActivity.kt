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

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val first: EditText = findViewById(R.id.firstInput)
        val last: EditText = findViewById(R.id.lastInput)
        val username: EditText = findViewById(R.id.usernameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val pass: EditText = findViewById(R.id.passInput)
        val signup: Button = findViewById(R.id.signupBtn)
        val goLogin: Button = findViewById(R.id.goLoginBtn)

        signup.setOnClickListener {
            val f = first.text.toString().trim()
            val l = last.text.toString().trim()
            val u = username.text.toString().trim()
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()

            if (f.isEmpty() || l.isEmpty() || u.isEmpty() || e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "all fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val id = withContext(Dispatchers.IO) {
                        val dao = AppDatabase.get(this@SignupActivity).userDao()
                        val exists = dao.existsByEmailOrUsername(e, u) > 0
                        if (exists) -1L else dao.insert(User(0, f, l, u, e, p))
                    }
                    if (id <= 0L) {
                        Toast.makeText(this@SignupActivity, "email or username already used", Toast.LENGTH_SHORT).show()
                    } else {
                        Auth.setUser(this@SignupActivity, id)
                        startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (t: Throwable) {
                    Toast.makeText(this@SignupActivity, "signup failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        goLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

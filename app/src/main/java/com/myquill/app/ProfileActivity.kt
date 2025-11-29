package com.myquill.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private var currentUserId: Long = -1L
    private var viewedUserId: Long = -1L
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val storedId = Auth.userId(this)
        if (storedId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        currentUserId = storedId
        viewedUserId = intent.getLongExtra("userId", currentUserId)
        val viewingSelf = viewedUserId == currentUserId

        val nameText: TextView = findViewById(R.id.profileName)
        val emailText: TextView = findViewById(R.id.profileEmail)
        val usernameText: TextView = findViewById(R.id.profileUsername)

        val genderValue: TextView = findViewById(R.id.profileGenderValue)
        val ageValue: TextView = findViewById(R.id.profileAgeValue)
        val bioValue: TextView = findViewById(R.id.profileBioValue)

        val genderInput: EditText = findViewById(R.id.profileGender)
        val ageInput: EditText = findViewById(R.id.profileAge)
        val bioInput: EditText = findViewById(R.id.profileBio)

        val img: ImageView = findViewById(R.id.profileImage)
        val changePhoto: Button = findViewById(R.id.changePhotoBtn)
        val saveBtn: Button = findViewById(R.id.saveProfileBtn)
        val friendsBtn: Button = findViewById(R.id.manageFriendsBtn)

        if (!viewingSelf) {
            changePhoto.visibility = View.GONE
            saveBtn.visibility = View.GONE
            friendsBtn.visibility = View.GONE

            genderInput.visibility = View.GONE
            ageInput.visibility = View.GONE
            bioInput.visibility = View.GONE

            genderValue.visibility = View.VISIBLE
            ageValue.visibility = View.VISIBLE
            bioValue.visibility = View.VISIBLE
        } else {
            genderValue.visibility = View.GONE
            ageValue.visibility = View.GONE
            bioValue.visibility = View.GONE
        }

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                AppDatabase.get(this@ProfileActivity).userDao().getById(viewedUserId)
            }
            if (user == null) {
                Toast.makeText(this@ProfileActivity, "user not found", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            nameText.text = "${user.firstName} ${user.lastName}"
            emailText.text = user.email
            usernameText.text = "@${user.username}"

            val gender = user.gender ?: ""
            val ageString = user.age?.toString() ?: ""
            val bio = user.bio ?: ""

            genderValue.text = gender
            ageValue.text = ageString
            bioValue.text = bio

            genderInput.setText(gender)
            ageInput.setText(ageString)
            bioInput.setText(bio)

            if (!user.profileImageUri.isNullOrEmpty()) {
                imageUri = Uri.parse(user.profileImageUri)
                img.setImageURI(imageUri)
            }
        }

        if (viewingSelf) {
            val picker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                    }
                    imageUri = uri
                    val imgView: ImageView = findViewById(R.id.profileImage)
                    imgView.setImageURI(uri)
                }
            }

            changePhoto.setOnClickListener {
                picker.launch(arrayOf("image/*"))
            }

            saveBtn.setOnClickListener {
                val gender = genderInput.text.toString().trim().ifEmpty { null }
                val ageTextVal = ageInput.text.toString().trim()
                val age = ageTextVal.toIntOrNull()
                val bio = bioInput.text.toString().trim().ifEmpty { null }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        AppDatabase.get(this@ProfileActivity).userDao().updateProfile(
                            userId = currentUserId,
                            imageUri = imageUri?.toString(),
                            gender = gender,
                            age = age,
                            bio = bio
                        )
                    }
                    Toast.makeText(this@ProfileActivity, "profile saved", Toast.LENGTH_SHORT).show()
                }
            }

            friendsBtn.setOnClickListener {
                startActivity(Intent(this, FriendsActivity::class.java))
            }
        }
    }
}

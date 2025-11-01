package com.myquill.app

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class EditEntryActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var lat: Double? = null
    private var lng: Double? = null
    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)
        val title: EditText = findViewById(R.id.titleInput)
        val body: EditText = findViewById(R.id.bodyInput)
        val img: ImageView = findViewById(R.id.imagePreview)
        val locText: TextView = findViewById(R.id.locationText)
        val pickImage: Button = findViewById(R.id.pickImageBtn)
        val getLocation: Button = findViewById(R.id.getLocationBtn)
        val save: Button = findViewById(R.id.saveEntryBtn)

        editingId = intent.getLongExtra("id", -1L)
        if (editingId > 0) {
            lifecycleScope.launch {
                val e = withContext(Dispatchers.IO) { AppDatabase.get(this@EditEntryActivity).entryDao().getById(editingId) }
                if (e != null) {
                    title.setText(e.title)
                    body.setText(e.body)
                    if (!e.imageUri.isNullOrEmpty()) {
                        imageUri = Uri.parse(e.imageUri)
                        img.setImageURI(imageUri)
                    }
                    lat = e.lat
                    lng = e.lng
                    locText.text = if (lat != null && lng != null) "Location: $lat, $lng" else "Location: unavailable"
                }
            }
        }

        val picker = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                imageUri = it
                img.setImageURI(it)
            }
        }
        pickImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33) {
                picker.launch("image/*")
            } else {
                val perm = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
                    picker.launch("image/*")
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(perm), 1001)
                }
            }
        }
        getLocation.setOnClickListener {
            val fine = Manifest.permission.ACCESS_FINE_LOCATION
            val coarse = Manifest.permission.ACCESS_COARSE_LOCATION
            val ok = (ContextCompat.checkSelfPermission(this, fine) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, coarse) == PackageManager.PERMISSION_GRANTED)
            if (!ok) {
                ActivityCompat.requestPermissions(this, arrayOf(fine, coarse), 1002)
                return@setOnClickListener
            }
            val client = LocationServices.getFusedLocationProviderClient(this)
            client.lastLocation.addOnSuccessListener { l ->
                if (l != null) {
                    lat = l.latitude
                    lng = l.longitude
                    locText.text = "Location: " + lat + ", " + lng
                } else {
                    locText.text = "Location: unavailable"
                }
            }.addOnFailureListener {
                locText.text = "Location: error"
            }
        }
        save.setOnClickListener {
            val t = title.text.toString().trim()
            val b = body.text.toString().trim()
            if (t.isEmpty()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = Auth.userId(this)
            if (uid == null) {
                Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val dao = AppDatabase.get(this@EditEntryActivity).entryDao()
                    if (editingId > 0) {
                        dao.update(Entry(editingId, uid, t, b, System.currentTimeMillis(), imageUri?.toString(), lat, lng))
                    } else {
                        dao.insert(Entry(0, uid, t, b, System.currentTimeMillis(), imageUri?.toString(), lat, lng))
                    }
                }
                finish()
            }
        }
    }
}
package com.myquill.app

import android.Manifest
import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.location.Geocoder

class EditEntryActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var videoUri: Uri? = null
    private var lat: Double? = null
    private var lng: Double? = null
    private var address: String? = null
    private var companions: String? = null
    private var editingId: Long = -1L

    private val mapPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val pickedLat = data.getDoubleExtra("lat", Double.NaN)
            val pickedLng = data.getDoubleExtra("lng", Double.NaN)
            if (!pickedLat.isNaN() && !pickedLng.isNaN()) {
                lat = pickedLat
                lng = pickedLng
                address = data.getStringExtra("address")
                val locText: TextView = findViewById(R.id.locationText)
                val label = if (!address.isNullOrEmpty()) {
                    "Location: $address"
                } else {
                    "Location: $lat, $lng"
                }
                locText.text = label
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)

        val title: EditText = findViewById(R.id.titleInput)
        val body: EditText = findViewById(R.id.bodyInput)
        val img: ImageView = findViewById(R.id.imagePreview)
        val locText: TextView = findViewById(R.id.locationText)
        val companionsText: TextView = findViewById(R.id.companionsText)
        val pickImage: Button = findViewById(R.id.pickImageBtn)
        val removeImage: Button = findViewById(R.id.removeImageBtn)
        val getLocation: Button = findViewById(R.id.getLocationBtn)
        val chooseOnMap: Button = findViewById(R.id.chooseOnMapBtn)
        val pickFriends: Button = findViewById(R.id.pickFriendsBtn)
        val save: Button = findViewById(R.id.saveEntryBtn)

        editingId = intent.getLongExtra("id", -1L)

        val uid = Auth.userId(this)
        if (uid == null) {
            Toast.makeText(this, "session expired", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
                img.setImageURI(uri)
            }
        }

        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                imageUri = cameraImageUri
                img.setImageURI(imageUri)
            }
        }

        val recordVideo = registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success && videoUri != null) {
                imageUri = videoUri
                img.setImageURI(imageUri)
                Toast.makeText(this, "Video saved", Toast.LENGTH_SHORT).show()
            }
        }

        if (editingId > 0) {
            lifecycleScope.launch {
                val e = withContext(Dispatchers.IO) {
                    AppDatabase.get(this@EditEntryActivity).entryDao().getById(editingId)
                }
                if (e != null) {
                    title.setText(e.title)
                    body.setText(e.body)
                    if (!e.imageUri.isNullOrEmpty()) {
                        imageUri = Uri.parse(e.imageUri)
                        img.setImageURI(imageUri)
                    }
                    lat = e.lat
                    lng = e.lng
                    address = e.address
                    companions = e.companions
                    val hasLatLng = lat != null && lng != null
                    val locLabel = when {
                        !address.isNullOrEmpty() -> "Location: $address"
                        hasLatLng -> "Location: ${lat}, ${lng}"
                        else -> "Location: unavailable"
                    }
                    locText.text = locLabel
                    val compLabel = if (!companions.isNullOrEmpty()) {
                        "With: $companions"
                    } else {
                        "With: nobody"
                    }
                    companionsText.text = compLabel
                }
            }
        } else {
            companionsText.text = "With: nobody"
        }

        pickImage.setOnClickListener {
            // Offer choice: take photo, record video, or choose from gallery
            val options = arrayOf("Take photo", "Record video", "Choose from gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
                .setTitle("Add media")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Take photo
                            val cameraPerm = Manifest.permission.CAMERA
                            val hasCamera = ContextCompat.checkSelfPermission(this, cameraPerm) == PackageManager.PERMISSION_GRANTED
                            if (!hasCamera && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(this, arrayOf(cameraPerm), 1003)
                                return@setItems
                            }
                            try {
                                val uri = createImageFileUri()
                                cameraImageUri = uri
                                takePicture.launch(uri)
                            } catch (t: Throwable) {
                                Toast.makeText(this, "unable to open camera", Toast.LENGTH_SHORT).show()
                            }
                        }
                        1 -> {
                            // Record video
                            val cameraPerm = Manifest.permission.CAMERA
                            val hasCamera = ContextCompat.checkSelfPermission(this, cameraPerm) == PackageManager.PERMISSION_GRANTED
                            if (!hasCamera && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(this, arrayOf(cameraPerm), 1003)
                                return@setItems
                            }
                            try {
                                val uri = createVideoFileUri()
                                videoUri = uri
                                recordVideo.launch(uri)
                            } catch (t: Throwable) {
                                Toast.makeText(this, "unable to open camera", Toast.LENGTH_SHORT).show()
                            }
                        }
                        2 -> {
                            // Choose gallery
                            if (Build.VERSION.SDK_INT >= 33) {
                                picker.launch(arrayOf("image/*"))
                            } else {
                                val perm = Manifest.permission.READ_EXTERNAL_STORAGE
                                val granted = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
                                if (granted) {
                                    picker.launch(arrayOf("image/*"))
                                } else {
                                    ActivityCompat.requestPermissions(this, arrayOf(perm), 1001)
                                }
                            }
                        }
                        else -> dialog.dismiss()
                    }
                }
            builder.show()
        }

        removeImage.setOnClickListener {
            imageUri = null
            img.setImageDrawable(null)
        }

        chooseOnMap.setOnClickListener {
            val intent = Intent(this, MapSelectActivity::class.java)
            mapPicker.launch(intent)
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
            client.lastLocation
                .addOnSuccessListener { l ->
                    if (l != null) {
                        lat = l.latitude
                        lng = l.longitude
                        lifecycleScope.launch {
                            val addr = withContext(Dispatchers.IO) {
                                try {
                                    val geocoder = Geocoder(this@EditEntryActivity, Locale.getDefault())
                                    val list = geocoder.getFromLocation(lat ?: 0.0, lng ?: 0.0, 1)
                                    if (!list.isNullOrEmpty()) list[0].getAddressLine(0) else null
                                } catch (t: Throwable) {
                                    null
                                }
                            }
                            address = addr
                            val label = if (!addr.isNullOrEmpty()) {
                                "Location: $addr"
                            } else {
                                "Location: address not available"
                            }
                            locText.text = label
                        }
                    } else {
                        locText.text = "Location: unavailable"
                    }
                }
                .addOnFailureListener {
                    locText.text = "Location: error"
                }
        }

        pickFriends.setOnClickListener {
            lifecycleScope.launch {
                val friends = withContext(Dispatchers.IO) {
                    AppDatabase.get(this@EditEntryActivity).friendDao().friends(uid)
                }
                if (friends.isEmpty()) {
                    Toast.makeText(this@EditEntryActivity, "no friends to add yet", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val names = friends.map { "${it.firstName} ${it.lastName} (@${it.username})" }.toTypedArray()
                val checked = BooleanArray(names.size)
                val builder = AlertDialog.Builder(this@EditEntryActivity)
                    .setTitle("Pick friends")
                    .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                        checked[which] = isChecked
                    }
                    .setPositiveButton("OK") { _, _ ->
                        val list = mutableListOf<String>()
                        for (i in names.indices) {
                            if (checked[i]) {
                                list.add(names[i])
                            }
                        }
                        companions = if (list.isEmpty()) null else list.joinToString(", ")
                        val label = if (!companions.isNullOrEmpty()) {
                            "With: $companions"
                        } else {
                            "With: nobody"
                        }
                        companionsText.text = label
                    }
                    .setNegativeButton("Cancel", null)
                builder.show()
            }
        }

        save.setOnClickListener {
            val t = title.text.toString().trim()
            val b = body.text.toString().trim()
            if (t.isEmpty()) {
                Toast.makeText(this, "title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val dao = AppDatabase.get(this@EditEntryActivity).entryDao()
                    val entry = Entry(
                        id = if (editingId > 0) editingId else 0,
                        userId = uid,
                        title = t,
                        body = b,
                        createdAt = System.currentTimeMillis(),
                        imageUri = imageUri?.toString(),
                        lat = lat,
                        lng = lng,
                        address = address,
                        companions = companions
                    )
                    if (editingId > 0) {
                        dao.update(entry)
                    } else {
                        dao.insert(entry)
                    }
                }
                val homeIntent = Intent(this@EditEntryActivity, HomeActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(homeIntent)
                finish()
            }
        }
    }

    private fun createImageFileUri(): Uri {
        val imagesDir = File(cacheDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFile = File(imagesDir, "IMG_$timeStamp.jpg")
        imageFile.createNewFile()
        return androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
    }

    private fun createVideoFileUri(): Uri {
        val videosDir = File(cacheDir, "videos")
        if (!videosDir.exists()) videosDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val videoFile = File(videosDir, "VID_$timeStamp.mp4")
        videoFile.createNewFile()
        return androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", videoFile)
    }
}

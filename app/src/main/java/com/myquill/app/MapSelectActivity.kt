package com.myquill.app

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapSelectActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val addressText: TextView = findViewById(R.id.mapAddressText)
        val confirmBtn: Button = findViewById(R.id.mapConfirmBtn)
        val cancelBtn: Button = findViewById(R.id.mapCancelBtn)

        cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        confirmBtn.setOnClickListener {
            val latLng = selectedLatLng
            if (latLng == null) {
                Toast.makeText(this, "pick a place on the map first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val data = intent
            data.putExtra("lat", latLng.latitude)
            data.putExtra("lng", latLng.longitude)
            data.putExtra("address", selectedAddress)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val start = LatLng(0.0, 0.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 1f))

        googleMap.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = googleMap.addMarker(MarkerOptions().position(latLng))
            selectedLatLng = latLng
            lifecycleScope.launch {
                val addr = withContext(Dispatchers.IO) {
                    try {
                        val geocoder = Geocoder(this@MapSelectActivity, Locale.getDefault())
                        val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        if (!list.isNullOrEmpty()) list[0].getAddressLine(0) else null
                    } catch (t: Throwable) {
                        null
                    }
                }
                selectedAddress = addr
                val text = if (!addr.isNullOrEmpty()) {
                    addr
                } else {
                    "Address not available"
                }

                findViewById<TextView>(R.id.mapAddressText).text = text
                selectedAddress = text
            }
        }
    }
}

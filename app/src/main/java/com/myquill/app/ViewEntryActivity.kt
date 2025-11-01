package com.myquill.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class ViewEntryActivity : AppCompatActivity() {
    private var entryId: Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_entry)
        entryId = intent.getLongExtra("id", -1L)
        val title: TextView = findViewById(R.id.viewTitle)
        val date: TextView = findViewById(R.id.viewDate)
        val loc: TextView = findViewById(R.id.viewLocation)
        val img: ImageView = findViewById(R.id.viewImage)
        val body: TextView = findViewById(R.id.viewBody)
        val edit: Button = findViewById(R.id.editEntryBtn)
        val delete: Button = findViewById(R.id.deleteEntryBtn)

        lifecycleScope.launch {
            val e = withContext(Dispatchers.IO) { AppDatabase.get(this@ViewEntryActivity).entryDao().getById(entryId) }
            if (e != null) {
                title.text = e.title
                date.text = DateFormat.getDateTimeInstance().format(Date(e.createdAt))
                val hasLoc = e.lat != null && e.lng != null
                loc.text = if (hasLoc) "Location: ${e.lat}, ${e.lng}" else "Location: unavailable"
                if (!e.imageUri.isNullOrEmpty()) img.setImageURI(Uri.parse(e.imageUri))
                body.text = e.body
            } else {
                finish()
            }
        }

        edit.setOnClickListener {
            val i = Intent(this, EditEntryActivity::class.java)
            i.putExtra("id", entryId)
            startActivity(i)
        }
        delete.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) { AppDatabase.get(this@ViewEntryActivity).entryDao().deleteById(entryId) }
                finish()
            }
        }
    }
}
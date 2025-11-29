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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

class ViewEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_entry)

        val title: TextView = findViewById(R.id.viewTitle)
        val body: TextView = findViewById(R.id.viewBody)
        val date: TextView = findViewById(R.id.viewDate)
        val loc: TextView = findViewById(R.id.viewLocation)
        val companions: TextView = findViewById(R.id.viewCompanions)
        val img: ImageView = findViewById(R.id.viewImage)
        val edit: Button = findViewById(R.id.editEntryBtn)
        val delete: Button = findViewById(R.id.deleteEntryBtn)

        val entryId = intent.getLongExtra("id", -1L)
        if (entryId <= 0) {
            finish()
            return
        }

        lifecycleScope.launch {
            val entry = withContext(Dispatchers.IO) {
                AppDatabase.get(this@ViewEntryActivity).entryDao().getById(entryId)
            }
            if (entry == null) {
                finish()
                return@launch
            }
            title.text = entry.title
            body.text = entry.body
            date.text = DateFormat.getDateTimeInstance().format(Date(entry.createdAt))

            val locText = if (!entry.address.isNullOrEmpty()) {
                "Location: ${entry.address}"
            } else {
                "Location: not set"
            }
            loc.text = locText

            val compText = if (!entry.companions.isNullOrEmpty()) {
                "With: ${entry.companions}"
            } else {
                "With: nobody"
            }
            companions.text = compText

            if (!entry.imageUri.isNullOrEmpty()) {
                img.setImageURI(Uri.parse(entry.imageUri))
            } else {
                img.setImageDrawable(null)
            }

            edit.setOnClickListener {
                val i = Intent(this@ViewEntryActivity, EditEntryActivity::class.java)
                i.putExtra("id", entryId)
                startActivity(i)
            }
            delete.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        AppDatabase.get(this@ViewEntryActivity).entryDao().deleteById(entryId)
                    }
                    finish()
                }
            }
        }
    }
}

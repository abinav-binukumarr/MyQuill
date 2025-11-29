package com.myquill.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), EntryAdapter.OnItemClick {

    private lateinit var list: RecyclerView
    private lateinit var adapter: EntryAdapter
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val uid = Auth.userId(this)
        if (uid == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        userId = uid

        list = findViewById(R.id.entryList)
        adapter = EntryAdapter(this)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<Button>(R.id.addBtn).setOnClickListener {
            startActivity(Intent(this, EditEntryActivity::class.java))
        }
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            Auth.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.deleteAccountBtn).setOnClickListener {
            lifecycleScope.launch {
                val db = AppDatabase.get(this@HomeActivity)
                db.entryDao().deleteByUser(userId)
                db.userDao().deleteById(userId)
                Auth.clear(this@HomeActivity)
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
        }
        findViewById<Button>(R.id.profileBtn).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<Button>(R.id.friendsBtn).setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val items = AppDatabase.get(this@HomeActivity).entryDao().getForUser(userId)
            adapter.submit(items)
        }
    }

    override fun onItemClick(entryId: Long) {
        val i = Intent(this, ViewEntryActivity::class.java)
        i.putExtra("id", entryId)
        startActivity(i)
    }
}

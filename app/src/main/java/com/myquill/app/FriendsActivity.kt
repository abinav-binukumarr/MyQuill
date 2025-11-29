package com.myquill.app

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsActivity : AppCompatActivity() {

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        val uid = Auth.userId(this)
        if (uid == null) {
            finish()
            return
        }
        userId = uid

        val searchInput: EditText = findViewById(R.id.searchUserInput)
        val searchBtn: Button = findViewById(R.id.searchUserBtn)
        val searchContainer: LinearLayout = findViewById(R.id.searchResultsContainer)
        val incomingContainer: LinearLayout = findViewById(R.id.incomingRequestsContainer)
        val friendsContainer: LinearLayout = findViewById(R.id.friendsListContainer)

        fun reloadAll() {
            lifecycleScope.launch {
                loadIncoming(incomingContainer)
                loadFriends(friendsContainer)
            }
        }

        searchBtn.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "enter a name or email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val usersWithRel = withContext(Dispatchers.IO) {
                    val db = AppDatabase.get(this@FriendsActivity)
                    val uDao = db.userDao()
                    val fDao = db.friendDao()
                    val list = uDao.search(query, userId)
                    list.map { u ->
                        val rel = fDao.relationBetween(userId, u.id)
                        u to rel
                    }
                }
                searchContainer.removeAllViews()
                val padding = (8 * resources.displayMetrics.density).toInt()
                val labelColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
                val btnTextColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
                val chipBg = ContextCompat.getColor(this@FriendsActivity, R.color.chip_bg)

                for ((u, rel) in usersWithRel) {
                    val row = LinearLayout(this@FriendsActivity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(padding)
                        setBackgroundColor(chipBg)
                    }
                    val label = TextView(this@FriendsActivity).apply {
                        text = "${u.firstName} ${u.lastName} (@${u.username})"
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        setTextColor(labelColor)
                    }
                    val status = rel?.status ?: "none"
                    val btn = Button(this@FriendsActivity).apply {
                        setTextColor(btnTextColor)
                        setBackgroundResource(R.drawable.bg_button_secondary)
                    }
                    when (status) {
                        "accepted" -> {
                            btn.text = "Friends"
                            btn.isEnabled = false
                        }
                        "pending" -> {
                            btn.text = "Requested"
                            btn.isEnabled = false
                        }
                        else -> {
                            btn.text = "Add"
                            btn.isEnabled = true
                            btn.setOnClickListener {
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        AppDatabase.get(this@FriendsActivity).friendDao()
                                            .upsert(FriendRequest(userId, u.id, "pending"))
                                    }
                                    Toast.makeText(this@FriendsActivity, "request sent", Toast.LENGTH_SHORT).show()
                                    reloadAll()
                                }
                            }
                        }
                    }
                    row.addView(label)
                    row.addView(btn)
                    searchContainer.addView(row)
                }
            }
        }

        reloadAll()
    }

    private fun loadIncoming(container: LinearLayout) {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                val db = AppDatabase.get(this@FriendsActivity)
                val requests = db.friendDao().incomingRequests(userId)
                val users = db.userDao()
                requests.mapNotNull { r ->
                    val fromUser = users.getById(r.fromUserId)
                    if (fromUser == null) null else fromUser to r
                }
            }
            container.removeAllViews()
            val padding = (8 * resources.displayMetrics.density).toInt()
            val labelColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
            val btnTextColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
            val dangerText = ContextCompat.getColor(this@FriendsActivity, R.color.accent_secondary)
            val chipBg = ContextCompat.getColor(this@FriendsActivity, R.color.chip_bg)

            for ((user, req) in items) {
                val row = LinearLayout(this@FriendsActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(padding)
                    setBackgroundColor(chipBg)
                }
                val label = TextView(this@FriendsActivity).apply {
                    text = "${user.firstName} ${user.lastName} (@${user.username})"
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setTextColor(labelColor)
                }
                val accept = Button(this@FriendsActivity).apply {
                    text = "Accept"
                    setTextColor(btnTextColor)
                    setBackgroundResource(R.drawable.bg_button_secondary)
                }
                val reject = Button(this@FriendsActivity).apply {
                    text = "Reject"
                    setTextColor(dangerText)
                    setBackgroundResource(R.drawable.bg_button_secondary)
                }

                accept.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(this@FriendsActivity).friendDao()
                                .updateStatus(req.fromUserId, req.toUserId, "accepted")
                        }
                        loadIncoming(container)
                        loadFriends(findViewById(R.id.friendsListContainer))
                    }
                }
                reject.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(this@FriendsActivity).friendDao()
                                .updateStatus(req.fromUserId, req.toUserId, "rejected")
                        }
                        loadIncoming(container)
                    }
                }

                row.addView(label)
                row.addView(accept)
                row.addView(reject)
                container.addView(row)
            }
        }
    }

    private fun loadFriends(container: LinearLayout) {
        lifecycleScope.launch {
            val friends = withContext(Dispatchers.IO) {
                AppDatabase.get(this@FriendsActivity).friendDao().friends(userId)
            }
            container.removeAllViews()
            val padding = (8 * resources.displayMetrics.density).toInt()
            val labelColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
            val btnTextColor = ContextCompat.getColor(this@FriendsActivity, R.color.text_primary)
            val dangerText = ContextCompat.getColor(this@FriendsActivity, R.color.accent_secondary)
            val chipBg = ContextCompat.getColor(this@FriendsActivity, R.color.chip_bg)

            for (u in friends) {
                val row = LinearLayout(this@FriendsActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(padding)
                    setBackgroundColor(chipBg)
                }
                val label = TextView(this@FriendsActivity).apply {
                    text = "${u.firstName} ${u.lastName} (@${u.username})"
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setTextColor(labelColor)
                }
                val viewBtn = Button(this@FriendsActivity).apply {
                    text = "View profile"
                    setTextColor(btnTextColor)
                    setBackgroundResource(R.drawable.bg_button_secondary)
                }
                val removeBtn = Button(this@FriendsActivity).apply {
                    text = "Remove"
                    setTextColor(dangerText)
                    setBackgroundResource(R.drawable.bg_button_secondary)
                }

                viewBtn.setOnClickListener {
                    val i = Intent(this@FriendsActivity, ProfileActivity::class.java)
                    i.putExtra("userId", u.id)
                    startActivity(i)
                }
                removeBtn.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(this@FriendsActivity).friendDao()
                                .removeRelation(userId, u.id)
                        }
                        loadFriends(container)
                    }
                }

                row.addView(label)
                row.addView(viewBtn)
                row.addView(removeBtn)
                container.addView(row)
            }
        }
    }
}

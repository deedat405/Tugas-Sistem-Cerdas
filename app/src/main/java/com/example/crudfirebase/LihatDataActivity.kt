package com.example.crudfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudfirebase.databinding.ActivityLihatDataBinding
import com.google.firebase.database.*

class LihatDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLihatDataBinding
    private val database = FirebaseDatabase
        // 🔹 Pastikan URL sesuai region project kamu
        .getInstance("https://crudfirebase-3a19d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("users")

    private val userList = ArrayList<User>()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Siapkan RecyclerView
        adapter = UserAdapter(userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 🔹 Ambil data dari Firebase Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)

                    if (user != null) {
                        // ⚠️ Pastikan setiap user punya ID untuk update/delete
                        user.id = data.key
                        userList.add(user)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@LihatDataActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

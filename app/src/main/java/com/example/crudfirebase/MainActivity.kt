package com.example.crudfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.crudfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Contoh simpan data ke Firebase
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString()

            if (name.isNotEmpty() && age.isNotEmpty()) {
                val user = mapOf("name" to name, "age" to age)
                database.child("users").push().setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        binding.etName.text.clear()
                        binding.etAge.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Isi semua data dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

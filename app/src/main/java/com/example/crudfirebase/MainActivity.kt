package com.example.crudfirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crudfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase
        .getInstance("https://crudfirebase-3a19d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "✅ onCreate dijalankan")

        // 🔹 Tes koneksi Firebase
        FirebaseDatabase.getInstance().getReference("tes_koneksi")
            .setValue("berhasil")
            .addOnSuccessListener {
                Toast.makeText(this, "Tes koneksi Firebase ✅", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Tes koneksi Firebase sukses")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Tes koneksi gagal ❌", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Tes koneksi Firebase gagal")
            }

        // 🔹 Tombol SIMPAN
        binding.btnSimpan.setOnClickListener {
            Log.d("MainActivity", "Tombol SIMPAN diklik ✅")

            val nim = binding.etNim.text.toString().trim()
            val nama = binding.etNama.text.toString().trim()
            val jurusan = binding.etJurusan.text.toString().trim()
            val jenisKelamin = binding.etJenisKelamin.text.toString().trim()
            val alamat = binding.etAlamat.text.toString().trim()

            if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty() ||
                jenisKelamin.isEmpty() || alamat.isEmpty()
            ) {
                Toast.makeText(this, "⚠ Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
                Log.w("MainActivity", "Input kosong, data tidak disimpan")
                return@setOnClickListener
            }

            val id = database.push().key
            Log.d("MainActivity", "ID yang dihasilkan: $id")

            if (id == null) {
                Toast.makeText(this, "Gagal membuat ID user", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "ID null, gagal push ke database")
                return@setOnClickListener
            }

            val user = User(id, nim, nama, jurusan, jenisKelamin, alamat)
            Log.d("MainActivity", "Data yang akan disimpan: $user")

            database.child(id).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Data berhasil disimpan ke Firebase")
                    binding.etNim.text.clear()
                    binding.etNama.text.clear()
                    binding.etJurusan.text.clear()
                    binding.etJenisKelamin.text.clear()
                    binding.etAlamat.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "❌ Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Gagal menyimpan data: ${e.message}", e)
                }
                .addOnCanceledListener {
                    Log.e("MainActivity", "❌ Operasi penyimpanan dibatalkan oleh sistem atau user")
                }
        }

        // 🔹 Tombol LIHAT DATA
        binding.btnLihat.setOnClickListener {
            Log.d("MainActivity", "Tombol LIHAT diklik ✅")
            val intent = Intent(this, LihatDataActivity::class.java)
            startActivity(intent)
        }

        // 🔹 Tombol LOGOUT
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Logout ditekan, activity ditutup")
            finish()
        }
    }
}

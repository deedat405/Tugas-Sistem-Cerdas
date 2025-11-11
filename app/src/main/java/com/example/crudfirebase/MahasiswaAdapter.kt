package com.example.crudfirebase

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crudfirebase.databinding.ItemUserBinding
import com.google.firebase.database.FirebaseDatabase

class MahasiswaAdapter(private val list: ArrayList<User>) :
    RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference("users")

    inner class ViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]

        // 🔹 Tampilkan data dari User.kt
        holder.binding.tvNim.text = "NIM: ${user.nim}"
        holder.binding.tvNama.text = "Nama: ${user.nama}"
        holder.binding.tvJurusan.text = "Jurusan: ${user.jurusan}"

        // 🔹 Tombol Update (tombol ini harus ada di item_user.xml)
        holder.binding.btnUpdate.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)

            val etNim = dialogView.findViewById<EditText>(R.id.etEditNim)
            val etNama = dialogView.findViewById<EditText>(R.id.etEditNama)
            val etJurusan = dialogView.findViewById<EditText>(R.id.etEditJurusan)
            val etJenis = dialogView.findViewById<EditText>(R.id.etEditJenisKelamin)
            val etAlamat = dialogView.findViewById<EditText>(R.id.etEditAlamat)

            etNim.setText(user.nim)
            etNama.setText(user.nama)
            etJurusan.setText(user.jurusan)
            etJenis.setText(user.jenisKelamin)
            etAlamat.setText(user.alamat)

            AlertDialog.Builder(context)
                .setTitle("Update Data Mahasiswa")
                .setView(dialogView)
                .setPositiveButton("Update") { dialog, _ ->
                    val updatedUser = User(
                        user.id,
                        etNim.text.toString(),
                        etNama.text.toString(),
                        etJurusan.text.toString(),
                        etJenis.text.toString(),
                        etAlamat.text.toString()
                    )
                    user.id?.let {
                        database.child(it).setValue(updatedUser)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // 🔻 Klik lama untuk hapus data
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Hapus Data")
                .setMessage("Yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    user.id?.let {
                        database.child(it).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Data dihapus", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
                .show()
            true
        }
    }
}

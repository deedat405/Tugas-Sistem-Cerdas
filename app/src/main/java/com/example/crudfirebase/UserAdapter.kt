package com.example.crudfirebase

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crudfirebase.databinding.ItemUserBinding
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val database = FirebaseDatabase
        .getInstance("https://crudfirebase-3a19d-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .getReference("users")

    inner class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        //  Tampilkan data sesuai item_user.xml
        holder.binding.tvNim.text = "NIM: ${user.nim}"
        holder.binding.tvNama.text = "Nama: ${user.nama}"
        holder.binding.tvJurusan.text = "Jurusan: ${user.jurusan}"

        //  Tombol Update
        holder.binding.btnUpdate.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)

            val etNim = dialogView.findViewById<EditText>(R.id.etEditNim)
            val etNama = dialogView.findViewById<EditText>(R.id.etEditNama)
            val etJurusan = dialogView.findViewById<EditText>(R.id.etEditJurusan)
            val etJenis = dialogView.findViewById<EditText>(R.id.etEditJenisKelamin)
            val etAlamat = dialogView.findViewById<EditText>(R.id.etEditAlamat)

            // Isi form dengan data lama
            etNim.setText(user.nim)
            etNama.setText(user.nama)
            etJurusan.setText(user.jurusan)
            etJenis.setText(user.jenisKelamin)
            etAlamat.setText(user.alamat)

            AlertDialog.Builder(context)
                .setTitle("Update Data Mahasiswa")
                .setView(dialogView)
                .setPositiveButton("Update") { dialog, _ ->
                    val updatedNim = etNim.text.toString().trim()
                    val updatedNama = etNama.text.toString().trim()
                    val updatedJurusan = etJurusan.text.toString().trim()
                    val updatedJenis = etJenis.text.toString().trim()
                    val updatedAlamat = etAlamat.text.toString().trim()

                    if (updatedNim.isEmpty() || updatedNama.isEmpty() || updatedJurusan.isEmpty()) {
                        Toast.makeText(context, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val updatedUser = User(
                        user.id,
                        updatedNim,
                        updatedNama,
                        updatedJurusan,
                        updatedJenis,
                        updatedAlamat
                    )

                    user.id?.let {
                        database.child(it).setValue(updatedUser)
                            .addOnSuccessListener {
                                Toast.makeText(context, "✅ Data berhasil diperbarui", Toast.LENGTH_SHORT).show()

                                // Perbarui tampilan di RecyclerView tanpa reload Firebase
                                userList[position] = updatedUser
                                notifyItemChanged(position)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "❌ Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        //  Klik = hapus data
        holder.binding.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val alert = AlertDialog.Builder(context)
                .setTitle("Hapus Data")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    user.id?.let {
                        database.child(it).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                // Hapus dari list dan update tampilan RecyclerView
                                userList.removeAt(position)
                                notifyItemRemoved(position)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
                .create()

            alert.show()
        }
    }
}

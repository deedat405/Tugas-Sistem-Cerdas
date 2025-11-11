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

    private val database = FirebaseDatabase.getInstance().getReference("users")

    inner class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.tvName.text = user.nama
        holder.binding.tvAge.text = "Umur: ${user.umur}"

        // 🔹 Tombol Hapus
        holder.binding.btnDelete.setOnClickListener {
            user.id?.let {
                database.child(it).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Data dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(holder.itemView.context, "Gagal hapus data", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // 🔹 Tombol Edit
        holder.binding.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)
            val etName = dialogView.findViewById<EditText>(R.id.etEditName)
            val etAge = dialogView.findViewById<EditText>(R.id.etEditAge)

            etName.setText(user.nama)
            etAge.setText(user.umur)

            AlertDialog.Builder(context)
                .setTitle("Edit Data")
                .setView(dialogView)
                .setPositiveButton("Simpan") { dialog, _ ->
                    val newName = etName.text.toString()
                    val newAge = etAge.text.toString()

                    if (newName.isNotEmpty() && newAge.isNotEmpty()) {
                        val updatedUser = User(user.id, newName, newAge)
                        user.id?.let {
                            database.child(it).setValue(updatedUser)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Data diperbarui", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}

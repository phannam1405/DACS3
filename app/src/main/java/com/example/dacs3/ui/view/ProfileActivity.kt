package com.example.dacs3.ui.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.remote.CloudinaryHelper
import com.example.dacs3.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEditableFields(intent.getBooleanExtra("is_editable", true))
        loadUserProfile()
        setupImagePicker()

        binding.btnUpdate.setOnClickListener { updateUserProfile() }
        binding.btnReturn.setOnClickListener { finish() }
        binding.btnPicture.setOnClickListener { imagePickerLauncher.launch("image/*") }
    }

    private fun setupEditableFields(isEditable: Boolean) {
        if (!isEditable) {
            lockAllEditTexts()
        }
    }

    private fun loadUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot.takeIf { it.exists() }?.let {
                binding.edtName.setText(it.child("userName").value?.toString() ?: "")
                binding.txtEmail.text = it.child("email").value?.toString() ?: ""
                binding.edtSDT.setText(it.child("phoneNumber").value?.toString() ?: "")
                binding.edtMota.setText(it.child("des").value?.toString() ?: "")

                it.child("avatarUrl").value?.toString()?.let { avatarUrl ->
                    Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.icon_avatar)
                        .error(R.drawable.icon_avatar)
                        .circleCrop()
                        .into(binding.imgAvatar)
                }
            }
        }.addOnFailureListener {
            showToast("Lỗi khi tải thông tin người dùng")
        }
    }

    private fun updateUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updatedData = mapOf<String, Any>(
            "userName" to binding.edtName.text.toString(),
            "phoneNumber" to binding.edtSDT.text.toString(),
            "des" to binding.edtMota.text.toString()
        )

        databaseRef.updateChildren(updatedData)
            .addOnSuccessListener { showToast("Cập nhật thông tin thành công") }
            .addOnFailureListener { showToast("Cập nhật thất bại") }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.imgAvatar.setImageURI(it)
                uploadAvatarToFirebase(it)
            }
        }
    }

    private fun uploadAvatarToFirebase(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val cloudinary = CloudinaryHelper.getCloudinary()

        Thread {
            try {
                val uploadResult = cloudinary.uploader().upload(inputStream, emptyMap<String, Any>())
                val avatarUrl = uploadResult["secure_url"] as String

                runOnUiThread {
                    saveAvatarUrlToFirebase(avatarUrl)
                    Glide.with(this).load(avatarUrl).into(binding.imgAvatar)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { showToast("Lỗi khi upload ảnh: ${e.message}") }
            }
        }.start()
    }

    private fun saveAvatarUrlToFirebase(avatarUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updates = mapOf<String, Any>("avatarUrl" to avatarUrl)
        databaseRef.updateChildren(updates)
            .addOnSuccessListener { showToast("Ảnh đại diện đã cập nhật") }
            .addOnFailureListener { showToast("Lưu ảnh thất bại") }
    }

    private fun lockAllEditTexts() {
        binding.edtName.isEnabled = false
        binding.edtSDT.isEnabled = false
        binding.edtMota.isEnabled = false
        binding.btnUpdate.visibility = View.INVISIBLE
        binding.btnPicture.visibility = View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

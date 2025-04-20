package com.example.dacs3.ui.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
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

        loadUserProfile()
        setupImagePicker()

        binding.btnUpdate.setOnClickListener {
            updateUserProfile()
        }
        binding.btnReturn.setOnClickListener{
            finish()
        }
        binding.btnPicture.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

    }
    private fun loadUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase
            .getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                binding.edtName.setText(snapshot.child("userName").value?.toString() ?: "")
                binding.txtEmail.text = snapshot.child("email").value?.toString() ?: ""
                binding.edtSDT.setText(snapshot.child("phoneNumber").value?.toString() ?: "")
                binding.edtMota.setText(snapshot.child("des").value?.toString() ?: "")

                val avatarUrl = snapshot.child("avatarUrl").value?.toString()
                if (!avatarUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.icon_avatar)
                        .error(R.drawable.icon_avatar)
                        .circleCrop() 
                        .into(binding.imgAvatar)

                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase
            .getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updatedData = mapOf<String, Any>(
            "userName" to binding.edtName.text.toString(),
            "phoneNumber" to binding.edtSDT.text.toString(),
            "des" to binding.edtMota.text.toString()
            // Bạn có thể thêm "avatarUrl" nếu có logic cập nhật ảnh
        )

        databaseRef.updateChildren(updatedData).addOnSuccessListener {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imgAvatar.setImageURI(uri)

                // Gọi hàm upload ảnh lên Firebase nếu muốn:
                uploadAvatarToFirebase(uri)
                loadUserProfile()
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
                runOnUiThread {
                    Toast.makeText(this, "Lỗi khi upload ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun saveAvatarUrlToFirebase(avatarUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase
            .getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updates = mapOf<String, Any>("avatarUrl" to avatarUrl)
        databaseRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this, "Ảnh đại diện đã cập nhật", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show()
        }
    }



}
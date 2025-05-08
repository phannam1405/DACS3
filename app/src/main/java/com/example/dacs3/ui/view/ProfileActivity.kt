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

        initUI()
        setupListeners()
    }






    // Khởi tạo giao diện và tải dữ liệu
    private fun initUI() {
        setupEditableFields(intent.getBooleanExtra("is_editable", true))
        loadUserProfile()
        setupImagePicker()
    }




    // Thiết lập các sự kiện click
    private fun setupListeners() {
        binding.btnUpdate.setOnClickListener { updateUserProfile() }
        binding.btnReturn.setOnClickListener { finish() }
        binding.btnPicture.setOnClickListener { imagePickerLauncher.launch("image/*") }
    }

    // Khóa các trường nếu không cho phép chỉnh sửa
    private fun setupEditableFields(isEditable: Boolean) {
        if (!isEditable) lockAllEditTexts()
    }







    // Tải thông tin người dùng từ Firebase
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
                    loadAvatarImage(avatarUrl)
                }
            }
        }.addOnFailureListener {
            showToast("Lỗi khi tải thông tin người dùng")
        }
    }






    // Cập nhật thông tin người dùng lên Firebase
    private fun updateUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updatedData = mapOf(
            "userName" to binding.edtName.text.toString(),
            "phoneNumber" to binding.edtSDT.text.toString(),
            "des" to binding.edtMota.text.toString()
        )

        databaseRef.updateChildren(updatedData)
            .addOnSuccessListener { showToast("Cập nhật thông tin thành công") }
            .addOnFailureListener { showToast("Cập nhật thất bại") }
    }






    // Thiết lập chọn ảnh
    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.imgAvatar.setImageURI(it)
                uploadAvatarToFirebase(it)
            }
        }
    }






    // Tải ảnh đại diện
    private fun loadAvatarImage(avatarUrl: String) {
        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.icon_avatar)
            .error(R.drawable.icon_avatar)
            .circleCrop()
            .into(binding.imgAvatar)
    }




    // Upload ảnh lên Cloudinary
    private fun uploadAvatarToFirebase(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val cloudinary = CloudinaryHelper.getCloudinary()

        Thread {
            try {
                val uploadResult = cloudinary.uploader().upload(inputStream, emptyMap<String, Any>())
                val avatarUrl = uploadResult["secure_url"] as String

                runOnUiThread {
                    saveAvatarUrlToFirebase(avatarUrl)
                    loadAvatarImage(avatarUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { showToast("Lỗi khi upload ảnh: ${e.message}") }
            }
        }.start()
    }




    // Lưu link ảnh đại diện vào Firebase
    private fun saveAvatarUrlToFirebase(avatarUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User")
            .child(uid)

        val updates = mapOf("avatarUrl" to avatarUrl)
        databaseRef.updateChildren(updates)
            .addOnSuccessListener { showToast("Ảnh đại diện đã cập nhật") }
            .addOnFailureListener { showToast("Lưu ảnh thất bại") }
    }



    // Khóa các trường nhập liệu
    private fun lockAllEditTexts() {
        binding.edtName.isEnabled = false
        binding.edtSDT.isEnabled = false
        binding.edtMota.isEnabled = false
        binding.btnUpdate.visibility = View.INVISIBLE
        binding.btnPicture.visibility = View.INVISIBLE
    }



    // Hiển thị Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

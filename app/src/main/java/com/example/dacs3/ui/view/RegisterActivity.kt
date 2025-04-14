package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.dacs3.data.model.DataUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.txtRe.setOnClickListener{
            val i1 = Intent(this, LoginActivity::class.java)
            startActivity(i1)
        }
        mAuth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {
            register()
        }

    }

    private fun register() {
        val email = binding.edtLoginUsername.text.toString().trim()
        val pass = binding.edtLoginPwd.text.toString().trim()
        val confirmPass = binding.edtLoginConfirmPwd.text.toString().trim()

        // Kiểm tra input không được để trống
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập password!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Vui lòng nhập lại password!", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra password và confirm password phải giống nhau
        if (pass != confirmPass) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show()
            return
        }

        // Đăng ký tài khoản với Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid  // Lấy trực tiếp từ result
                    val user = DataUser(
                        userName = "Test User",
                        email = email,
                        phoneNumber = "",
                        des = "",
                        avatar = ""
                    )
                    if (uid != null) {
                        val databaseRef = FirebaseDatabase
                            .getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("User")
                        databaseRef.child(uid).setValue(user)
                            .addOnCompleteListener { saveTask ->
                                if (saveTask.isSuccessful) {
                                    Log.d("FirebaseDB", "Dữ liệu lưu thành công cho UID: $uid")
                                    Toast.makeText(this, "Đăng ký và lưu thông tin thành công!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    Log.e("FirebaseDB", "Lỗi lưu DB: ${saveTask.exception?.message}")
                                    Toast.makeText(this, "Lưu thông tin người dùng thất bại!", Toast.LENGTH_SHORT).show()
                                }
                            }

                    } else {
                        Toast.makeText(this, "Không lấy được UID!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                    android.util.Log.e("FirebaseAuthError", "Lỗi auth: ${task.exception?.message}")
                }
            }

    }


}
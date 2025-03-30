package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth


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
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                    // Chuyển hướng sang MainActivity sau khi đăng ký thành công
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // Đăng ký thất bại, hiển thị lỗi từ Firebase
                    Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
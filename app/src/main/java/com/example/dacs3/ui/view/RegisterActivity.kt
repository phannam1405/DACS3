package com.example.dacs3.ui.view
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

        binding.txtRe.setOnClickListener {
            val i1 = Intent(this, LoginActivity::class.java)
            startActivity(i1)
        }

        // Khởi tạo Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance()

        // Sự kiện khi nhấn vào nút đăng ký
        binding.btnRegister.setOnClickListener {
            register()
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    // Hàm xử lý đăng ký người dùng
    private fun register() {

        if(!isNetworkAvailable()){
            Toast.makeText(this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy các giá trị từ các EditText
        val email = binding.edtLoginUsername.text.toString().trim()
        val pass = binding.edtLoginPwd.text.toString().trim()
        val confirmPass = binding.edtLoginConfirmPwd.text.toString().trim()

        // Kiểm tra nếu các trường nhập liệu còn trống
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

        // Kiểm tra password và confirm password có trùng nhau không
        if (pass != confirmPass) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show()
            return
        }

        // Đăng ký người dùng với Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid  // Lấy UID của người dùng từ Firebase
                    val user = DataUser(
                        userName = "Test User",
                        email = email,
                        phoneNumber = "",
                        des = "",
                        avatarUrl = ""
                    )

                    // Kiểm tra nếu lấy được UID và lưu thông tin người dùng vào Firebase Realtime Database
                    if (uid != null) {
                        val databaseRef = FirebaseDatabase
                            .getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("User")
                        databaseRef.child(uid).setValue(user)  // Lưu thông tin người dùng
                            .addOnCompleteListener { saveTask ->
                                if (saveTask.isSuccessful) {
                                    Log.d("FirebaseDB", "Dữ liệu lưu thành công cho UID: $uid")
                                    Toast.makeText(this, "Đăng ký và lưu thông tin thành công!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                } else {
                                    Log.e("FirebaseDB", "Lỗi lưu DB: ${saveTask.exception?.message}")
                                    Toast.makeText(this, "Lưu thông tin người dùng thất bại!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Không lấy được UID!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseAuth", "Lỗi đăng ký: ${task.exception?.message}")
                }
            }
    }
}

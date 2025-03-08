package com.example.dacs3
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtCA.setOnClickListener{
            val i1 = Intent(this, RegisterActivity::class.java)
            startActivity(i1)
        }
        binding.buttonLogin.setOnClickListener{
            val i1 = Intent(this, MainActivity::class.java)
            startActivity(i1)
        }
    }
}
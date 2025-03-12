package com.example.dacs3


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //drawer
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_feedback ->{
                    Toast.makeText(this, "feedback", Toast.LENGTH_SHORT).show()}
                R.id.nav_info ->{
                    Toast.makeText(this, "about us", Toast.LENGTH_SHORT).show()}
                R.id.nav_settings ->{
                    Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()}
                R.id.nav_logout ->{
                    Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show()}
                R.id.nav_profile ->{
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()}
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.itemHome

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    val intent = Intent(this, FavouriteActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.itemHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.itemPlaylist -> {
                    val intent = Intent(this, PlayerActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    //drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

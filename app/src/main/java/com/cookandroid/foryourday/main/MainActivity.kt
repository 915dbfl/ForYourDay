package com.cookandroid.foryourday.main

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.databinding.ActivityMainBinding
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val sqlite = SQLite(this)
    lateinit var header: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        header = navView.getHeaderView(0)
        val textviewUserId = header.findViewById<TextView>(R.id.user_id)
        val userProfileImg = header.findViewById<ImageView>(R.id.user_profile_img)

        CoroutineScope(Dispatchers.Default).launch {
            val userInfo = sqlite.getUserInfo().await()
            val userName = userInfo.user.userName
            val userImg = userInfo.user.imagePath
            textviewUserId.text = userName
            if (userImg != ""){
                withContext(Dispatchers.Main){
                    Glide.with(this@MainActivity).load(userImg).error(R.drawable.ic_baseline_person_24).into(userProfileImg)
                }
            }else{
                userProfileImg.setImageResource(R.drawable.ic_baseline_person_24)
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_add_category, R.id.nav_add_todo, R.id.nav_add_d_day, R.id.nav_setting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}
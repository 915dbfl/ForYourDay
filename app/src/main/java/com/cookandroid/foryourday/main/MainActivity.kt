package com.cookandroid.foryourday.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.databinding.ActivityMainBinding
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.Categories
import com.cookandroid.foryourday.retrofit.ColorData
import com.cookandroid.foryourday.retrofit.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getSerializableExtra("data") as UserData
        viewModel.setData(data)
        viewModel.updateCategories()

        setSupportActionBar(binding.appBarMain.toolbar)
//        binding.appBarMain.fab.setOnClickListener {
//            Navigation.createNavigateOnClickListener(R.id.nav_add_todo, null)
//        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val header = navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.user_id).text = data.user.userName

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_add_todo, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
package com.by.pawsitive.main_files.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.by.pawsitive.R
import com.by.pawsitive.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import javax.net.ssl.SSLSessionBindingEvent

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
//    private var dbViewModel: DBViewModel? = null
//    private var appViewModel: AppViewModel? = null
    private lateinit var name: TextView
    private lateinit var img: CircleImageView
    private lateinit var viewProfile: LinearLayout
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#FFFFFFFF")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.appBar.toolbar.title = ""
        setSupportActionBar(binding.appBar.toolbar)

//        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
//        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navViewNormalUser
        val navController = findNavController(R.id.nav_host_fragment_content)

        img = navView.getHeaderView(0).findViewById(R.id.profileImage)
        name = navView.getHeaderView(0).findViewById(R.id.profileName)
        viewProfile = navView.getHeaderView(0).findViewById(R.id.viewProfile)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_feed,
                R.id.nav_track,
                R.id.nav_heartbeat_monitor,
                R.id.nav_training_centers,
                R.id.nav_grooming_centers,
                R.id.nav_kennels,
                R.id.nav_hospitals,
                R.id.nav_about_us,
                R.id.nav_contact_us,
                R.id.nav_future_scope
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        appViewModel!!.userdata.observe(this) { user ->
//            if (user != null) {
//                getDataFromDatabase(user)
//            }
//        }

//        viewProfile.setOnClickListener {
//            startActivity(Intent(this, UserProfileActivity::class.java))
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
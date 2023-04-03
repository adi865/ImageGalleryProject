package com.example.imagegalleryproject

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.imagegalleryproject.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private lateinit var mAuth: FirebaseAuth

    lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView //instantiate drawerlayout

        binding.appBarMain.toolbar.overflowIcon?.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)

       navHostFragment =
            supportFragmentManager.findFragmentById(R.id.imageNavHostContainer) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(R.navigation.nav_graph)



        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )


        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        drawerLayout.addDrawerListener(toggle)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment, R.id.galleryFragment, R.id.imageFragment, R.id.imageViewerFragment
            ), drawerLayout
        )

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupWithNavController(navView, navController)


        val headerTop = binding.navView.getHeaderView(0)
        val drawerLayoutUName = headerTop.findViewById<TextView>(R.id.tvDrawerUserName)

        if (mAuth.currentUser != null) {
            drawerLayoutUName.text = mAuth.currentUser!!.email.toString()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        navView.setNavigationItemSelectedListener {
             when (it.itemId) {
                R.id.nav_fav -> navController.navigate(R.id.imageFragment)
                R.id.nav_gallery -> navController.navigate(R.id.galleryFragment)
                R.id.home -> navController.navigate(R.id.mainFragment)
                else -> navController.navigate(R.id.mainFragment)
            }
            // Close the navigation drawer
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                if (mAuth.currentUser != null) {
                    mAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                } else if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }
}
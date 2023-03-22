package com.example.imagegalleryproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.imagegalleryproject.databinding.ActivityMainBinding
import com.example.imagegalleryproject.db.DatabaseInstance
import com.example.imagegalleryproject.db.ImageDao
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.fragments.GalleryFragment
import com.example.imagegalleryproject.fragments.ImageFragment
import com.example.imagegalleryproject.fragments.MainFragment
import com.example.imagegalleryproject.viewmodel.ImageViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController


    private lateinit var mAuth: FirebaseAuth

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView //instantiate drawerlayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.imageNavHostContainer) as NavHostFragment
        navController = findNavController(R.id.imageNavHostContainer)
        //val navController = findNavController(R.id.imageNavHostContainer)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
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
                R.id.mainFragment, R.id.galleryFragment, R.id.imageFragment
            ), drawerLayout
        )

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupWithNavController(navView, navController)


       val headerTop = binding.navView.getHeaderView(0)
       val drawerLayoutUName = headerTop.findViewById<TextView>(R.id.tvDrawerUserName)

        if(mAuth.currentUser != null) {
            drawerLayoutUName.text = mAuth.currentUser!!.email.toString()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        navView.setNavigationItemSelectedListener {
            var fragment: Fragment? = null
            val fragmentClass: Class<*> = when (it.itemId) {
                R.id.nav_fav -> ImageFragment::class.java
                R.id.nav_gallery -> GalleryFragment::class.java
                else -> MainFragment::class.java
            }

            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Insert the fragment by replacing any existing fragment

            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commit()
            // Highlight the selected item has been done by NavigationView

            // Highlight the selected item has been done by NavigationView
            it.isChecked = true
            // Set action bar title
            // Set action bar title
            title = it.title
            // Close the navigation drawer
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
//            val navController = findNavController(R.id.imageNavHostContainer)
            return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.signOut -> {
                if(mAuth.currentUser != null) {
                    mAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                } else if(GoogleSignIn.getLastSignedInAccount(this)!=null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener {
                        val intent= Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
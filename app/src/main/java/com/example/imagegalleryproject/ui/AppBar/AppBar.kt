package com.example.imagegalleryproject.ui.AppBar

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.screens.PagesWithIconAndTitles
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppBar(
    onNavigationIconClick: () -> Unit,
    title: String,
    mAuth: FirebaseAuth,
    navController: NavController
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

}
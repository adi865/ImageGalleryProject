package com.example.imagegalleryproject

import androidx.compose.material3.DrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.imagegalleryproject.screens.Pages
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun TopBar(title: String, drawerState: DrawerState, navController: NavController) {
    var localShowMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val mAuth = FirebaseAuth.getInstance()
    androidx.compose.material.TopAppBar(
        title = {
            Text(text = title, color = Color.White)
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle DrawerLayout",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                localShowMenu = !localShowMenu
            }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = localShowMenu,
                onDismissRequest = { localShowMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = "Sign Out", color = Color.White)
                    },
                    onClick = {
                        if (mAuth.currentUser != null) {
                            mAuth.signOut()
                            navController.navigate(Pages.SignIn.route)
                        }
                    }
                )
            }
        }
    )
}
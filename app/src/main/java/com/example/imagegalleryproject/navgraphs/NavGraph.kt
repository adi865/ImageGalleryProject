package com.example.imagegalleryproject.navgraphs

import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.screens.SINGLE_IMAGE_PAGE_ARG
import com.example.imagegalleryproject.ui.pages.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val scrollState = rememberLazyStaggeredGridState()
    var mAuth = FirebaseAuth.getInstance()
    if (mAuth.currentUser != null) {
        NavHost(
            navController = navController,
            startDestination = Pages.Gallery.route
        ) {
            composable(Pages.Gallery.route) {
                GalleryPage(navController = navController, scrollState = scrollState)
            }
            composable(Pages.Favorites.route) {
                FavoriteImagesPage(navController = navController, scrollState = scrollState)
            }

            composable(Pages.ProfileManagement.route) {
                ProfileManagement(navController = navController)
            }

            composable(Pages.SignIn.route) {
                SignInPage(navController = navController)
            }

            composable(Pages.SingleImagePage.route,
                    arguments = listOf(navArgument(SINGLE_IMAGE_PAGE_ARG){
                        type = NavType.StringType
                    })) {
                Log.d("Nav Arguments", it.arguments?.getString(SINGLE_IMAGE_PAGE_ARG).toString())
                var imgId = it.arguments?.getString(SINGLE_IMAGE_PAGE_ARG).toString()
                SingleImagePage(navController = navController, imgId)
            }
        }
    } else {
        NavHost(navController = navController, startDestination = Pages.SignIn.route) {
            composable(Pages.SignIn.route) {
                SignInPage(navController = navController)
            }

            composable(Pages.SignUp.route) {
                SignUpPage(navController = navController)
            }
            
            composable(Pages.Gallery.route) {
                GalleryPage(navController = navController, scrollState)
            }
        }
    }
}
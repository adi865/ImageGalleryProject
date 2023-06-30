package com.example.imagegalleryproject.navgraphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.screens.PagesWithIconAndTitles
import com.example.imagegalleryproject.screens.SINGLE_IMAGE_PAGE_ARG
import com.example.imagegalleryproject.ui.pages.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SetupNavGraph(navController: NavHostController) {
    var mAuth = FirebaseAuth.getInstance()
    if (mAuth.currentUser != null) {
        NavHost(
            navController = navController,
            startDestination = PagesWithIconAndTitles.Gallery.route
        ) {
            composable(Pages.Gallery.route) {
                GalleryPage(navController = navController)
            }
            composable(PagesWithIconAndTitles.Favorites.route) {
                FavoriteImagesPage(navController = navController)
            }

            composable(PagesWithIconAndTitles.ProfileManagement.route) {
                ProfileManagement(navController = navController)
            }

            composable(Pages.SignIn.route) {
                SignInPage(navController = navController)
            }

            composable(PagesWithIconAndTitles.SingleImagePage.route,
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
                GalleryPage(navController = navController)
            }
        }
    }
}
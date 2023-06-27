package com.example.imagegalleryproject.screens

sealed class Pages(val route: String) {
    object SignIn: Pages(route = "SignInPage")
    object SignUp: Pages(route = "SignUpPage")
    object Gallery: Pages(route = "GalleryPage")
    object Favorites: Pages(route = "Favorites")
    object ProfileManagement: Pages(route = "ProfileManagement")
}

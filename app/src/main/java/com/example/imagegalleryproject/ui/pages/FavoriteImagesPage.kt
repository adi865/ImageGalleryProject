package com.example.imagegalleryproject.ui.pages

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.imagegalleryproject.BottomBar.BottomBar
import com.example.imagegalleryproject.TopBar
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.ui.AppBar.ContextualTopBar
import com.example.imagegalleryproject.ui.drawerlayout.DrawerBody
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.example.imagegalleryproject.util.Status
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.widgets.FAB
import com.google.firebase.auth.FirebaseAuth
import com.google.relay.compose.RowScopeInstanceImpl.align
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun FavoriteImagesPage(
    navController: NavController,
    scrollState: LazyStaggeredGridState
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isContextualActionModeActive = remember { mutableStateOf(false) }
    val countOfSelectedItems = remember { mutableStateOf(0) }

    val selectedFavorites = ArrayList<FavoriteImage>()

    val favoriteViewModel = FavoriteViewModel()
    val mAuth = FirebaseAuth.getInstance()
    if(mAuth.currentUser != null) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column {
                        DrawerHeader()
                        DrawerBody(
                            items = listOf(
                                Pages.Gallery,
                                Pages.Favorites,
                                Pages.ProfileManagement
                            ),
                            onItemClick = {
                                scope.launch {
                                    navController.navigate(it.route) {
                                        popUpTo = navController.graph.getStartDestination()
                                        launchSingleTop = true
                                    }
                                    drawerState.close()
                                }
                            }
                        )
                    }
                }
            },
            content = {
                androidx.compose.material.Scaffold(
                    topBar = {
                        if (isContextualActionModeActive.value) {
                            // Render contextual action mode topBar
                            // Replace with your desired implementation
                            ContextualTopBar(countOfSelectedItems = countOfSelectedItems, imageVector = Icons.Default.Delete, performAction = {favoriteViewModel.deleteFavorites(selectedFavorites)})
                        } else {
                            TopBar(title = "Favorites", drawerState = drawerState, navController = navController)
                        }
                    },
                    floatingActionButton = {
                        if(scrollState.firstVisibleItemIndex == 0) {
                            FAB()
                        }
                    },
                    isFloatingActionButtonDocked = true,
                    floatingActionButtonPosition = FabPosition.Center,
                    bottomBar = {
                        if(scrollState.firstVisibleItemIndex == 0) {
                            BottomBar(navController)
                        }
                    }
                ) {
                    showFavorites(
                        navController = navController,
                        favViewModel = favoriteViewModel,
                        isContextualActionModeActive,
                        countOfSelectedItems,
                        selectedFavorites,
                        scrollState
                    )
                }
            }
        )
    } else {
        navController.navigate(Pages.SignIn.route)
    }

}

@Composable
fun showFavorites(
    navController: NavController,
    favViewModel: FavoriteViewModel,
    isContextualActionModeActive: MutableState<Boolean>,
    countOfSelectedItems: MutableState<Int>,
    selectedFavorite: ArrayList<FavoriteImage>,
    scrollState: LazyStaggeredGridState
) {
    val getFavorites = favViewModel.fetchedImages.observeAsState()
    val message = favViewModel.message.observeAsState()

    val isLongPressActive = remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<FavoriteImage>() }

    val scope = rememberCoroutineScope()

    val backHandler = LocalOnBackPressedDispatcherOwner.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isContextualActionModeActive.value) {
                    isContextualActionModeActive.value = false
                    isLongPressActive.value = false
                    selectedItems.clear()
                } else {
                    scope.launch {
                        navController.popBackStack()
                        drawerState.close()
                    }
                }
            }
        }
        backHandler!!.onBackPressedDispatcher.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    LaunchedEffect(selectedItems.size, isLongPressActive.value, selectedItems) {
        isContextualActionModeActive.value = isLongPressActive.value
        countOfSelectedItems.value = selectedItems.size
        selectedItems.forEach {
            selectedFavorite.add(it)
        }
    }

    favViewModel.getAllFavorites()
    val checkList = ArrayList<FavoriteImage>()
    getFavorites.value?.let { list ->
        list.data?.let { favorites ->
            checkList.addAll(favorites)
        }
    }
    if (checkList.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxSize()
                .background(Color(240, 244, 244))
        ) {
            Text(
                "First Add Favorites from the Gallery",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            state = scrollState,
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(240, 244, 244)),
            contentPadding = PaddingValues(16.dp)
        ) {
            getFavorites.value?.let {
                message.value?.let {
                    it.getContentIfNotHandled()?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }
                when (it.status) {
                    Status.LOADING -> {

                    }
                    Status.SUCCESS -> {
                        it.data?.let { favoritesList ->
                            items(favoritesList) {favorite ->
                                FavItem(
                                    navController,
                                    favorite,
                                    selectedItems,
                                    isLongPressActive
                                ) { isSelected ->
                                    if (isSelected) {
                                        selectedItems.add(favorite)
                                    } else {
                                        selectedItems.remove(favorite)
                                    }
                                }
                            }
                        }
                    }
                    Status.ERROR -> {

                    }
                }
            }
        }
    }
}

@Composable
fun FavItem(
    navController: NavController,
    favoriteImage: FavoriteImage,
    selectedItems: MutableList<FavoriteImage>,
    isLongPressActive: MutableState<Boolean>,
    onSelectionChange: (Boolean) -> Unit
) {
    val isSelected = selectedItems.contains(favoriteImage)

    val isLongPressEnabled = remember { mutableStateOf(false) }

    val longPressDuration = 500 // Adjust the duration as needed
    val coroutineScope = rememberCoroutineScope()
    var longPressJob: Job? by remember { mutableStateOf(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    // Handle regular tap here
                    if (isLongPressActive.value) {
                        isLongPressActive.value = false
                    } else {
                        val encodedUrl = URLEncoder.encode(
                            favoriteImage.favorite,
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate(
                            route = Pages.SingleImagePage.passImgId(encodedUrl)
                        )
                    }
                },
                onLongPress = {
                    isLongPressEnabled.value = true
                    longPressJob = coroutineScope.launch {
                        delay(longPressDuration.toLong())
                        if (isLongPressEnabled.value) {
                            isLongPressActive.value = true
                        }
                    }
                }
            )
        }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(favoriteImage.favorite)
                .crossfade(true)
                .build(),
            contentDescription = "barcode image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(
                RoundedCornerShape(10.dp)
            )
        )
        if (isLongPressActive.value) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChange(it) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFavoriteImagesPage() {
    FavoriteImagesPage(rememberNavController(), rememberLazyStaggeredGridState())
}
package com.example.imagegalleryproject.ui.pages

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.screens.PagesWithIconAndTitles
import com.example.imagegalleryproject.ui.MainActivity
import com.example.imagegalleryproject.ui.drawerlayout.DrawerBody
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.example.imagegalleryproject.util.DataStatus
import com.example.imagegalleryproject.util.Status
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
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
    navController: NavController
) {
    val mAuth = FirebaseAuth.getInstance()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val bottomNavigationItems = listOf(
        PagesWithIconAndTitles.Gallery,
        PagesWithIconAndTitles.Favorites,
        PagesWithIconAndTitles.ProfileEdit,
        PagesWithIconAndTitles.ProfileManagement
    )

    val isContextualActionModeActive = remember { mutableStateOf(false) }
    val countOfSelectedItems = remember { mutableStateOf(0) }

    val selectedFavorites = ArrayList<FavoriteImage>()

    val context = LocalContext.current

    val favoriteViewModel = FavoriteViewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    DrawerHeader()
                    DrawerBody(
                        items = listOf(
                            PagesWithIconAndTitles.Gallery,
                            PagesWithIconAndTitles.Favorites,
                            PagesWithIconAndTitles.ProfileManagement
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
                        androidx.compose.material.TopAppBar(
                            title = { Text(text = "${countOfSelectedItems.value} selected") },
                            actions = {
                                IconButton(onClick = { /* Handle action */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                        modifier = Modifier.clickable {
                                            favoriteViewModel.deleteFavorites(selectedFavorites)
                                        }
                                    )
                                }
                            }
                        )
                    } else {
                        androidx.compose.material.TopAppBar(
                            title = {
                                Text(text = "Favorites", color = Color.White)
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
                                    showMenu = !showMenu
                                }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More Options",
                                        tint = Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
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
                },
                floatingActionButton = {
                    androidx.compose.material.FloatingActionButton(
                        onClick = {
                            (context as? MainActivity)?.requestCameraPermission()
                        },
                        backgroundColor = Color(0xFFFFAC5F),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_photo_camera_24),
                            contentDescription = "fab"
                        )
                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = {
                    androidx.compose.material.BottomAppBar(
                        backgroundColor = Color.White,
                        cutoutShape = CircleShape,
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.arguments?.getString(PagesWithIconAndTitles.Favorites.route)
                        BottomNavigation(
                            backgroundColor = Color.Transparent,
                            elevation = 0.dp
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val weightLeft = 1f / 2 // Two items on the left side
                                val weightRight = 1f / 2 // Two items on the right side
                                bottomNavigationItems.subList(0, 2).forEach { screen ->
                                    BottomNavigationItem(
                                        selected = (currentRoute == screen.route),
                                        icon = {
                                            Icon(
                                                screen.icon,
                                                screen.route,
                                                tint = Color.DarkGray
                                            )
                                        },
                                        modifier = Modifier.weight(weightLeft),
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo = navController.graph.getStartDestination()
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }

                                Spacer(Modifier.weight(0.5f))

                                bottomNavigationItems.subList(2, bottomNavigationItems.size)
                                    .forEach { screen ->
                                        BottomNavigationItem(
                                            selected = (currentRoute == screen.route),
                                            icon = {
                                                Icon(
                                                    screen.icon,
                                                    screen.route,
                                                    tint = Color.DarkGray
                                                )
                                            },
                                            modifier = Modifier.weight(weightRight),
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo =
                                                        navController.graph.getStartDestination()
                                                    launchSingleTop = true
                                                }
                                            }
                                        )
                                    }
                            }
                        }
                    }
                }
            ) {
                showFavorites(
                    navController = navController,
                    favViewModel = favoriteViewModel,
                    isContextualActionModeActive,
                    countOfSelectedItems,
                    selectedFavorites
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showFavorites(
    navController: NavController,
    favViewModel: FavoriteViewModel,
    isContextualActionModeActive: MutableState<Boolean>,
    countOfSelectedItems: MutableState<Int>,
    selectedFavorite: ArrayList<FavoriteImage>
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
                                    isLongPressActive,
                                    isContextualActionModeActive
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
    isContextualActionModeActive: MutableState<Boolean>,
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
                            route = PagesWithIconAndTitles.SingleImagePage.passImgId(encodedUrl)
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
    FavoriteImagesPage(rememberNavController())
}
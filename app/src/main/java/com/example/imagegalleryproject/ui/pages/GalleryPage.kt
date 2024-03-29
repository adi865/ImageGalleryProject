package com.example.imagegalleryproject.ui.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Animatable
import android.hardware.camera2.*
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.imagegalleryproject.BottomBar.BottomBar
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.ui.AppBar.ContextualTopBar
import com.example.imagegalleryproject.ui.drawerlayout.DrawerBody
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.example.imagegalleryproject.util.Status
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.example.imagegalleryproject.viewmodel.ThumbnailViewModel
import com.example.imagegalleryproject.widgets.FAB
import com.google.firebase.auth.FirebaseAuth
import com.google.relay.compose.RowScopeInstanceImpl.align
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GalleryPage(
    navController: NavController, scrollState: LazyStaggeredGridState
) {
    val mAuth = FirebaseAuth.getInstance()
    var movieTitleQuery by remember {
        mutableStateOf("")
    }

    var stateOfOnSearchClicked by remember {
        mutableStateOf(false)
    }

    val thumbnailViewModel = ThumbnailViewModel()

    val searchWidgetState by thumbnailViewModel.searchWidgetState
    val searchTextState by thumbnailViewModel.searchTextState

    val isContextualActionModeActive = remember { mutableStateOf(false) }
    val countOfSelectedItems = remember { mutableStateOf(0) }
    val selectedImages = ArrayList<FavoriteImage>()

    if (mAuth.currentUser != null) {
        androidx.compose.material.Scaffold(
            backgroundColor = Color(240, 244, 244),
            topBar = {
                MainAppBar(
                    navController = navController,
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onTextChange = { thumbnailViewModel.updateSearchTextState(newValue = it) },
                    onCloseClicked = {
                        thumbnailViewModel.updateSearchTextState(newValue = "")
                        thumbnailViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                    },
                    onSearchClicked = {
                        Log.d("Searched Text", it)
                        movieTitleQuery = it
                        stateOfOnSearchClicked = true
                    },
                    onSearchTriggered = {
                        thumbnailViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                    },
                    thumbnailViewModel = thumbnailViewModel,
                    mAuth = mAuth,
                    query = movieTitleQuery,
                    scrollState = scrollState
                )
            },
            content = {
                PopulateView(
                    stateOfOnSearchClicked = stateOfOnSearchClicked,
                    navController = navController,
                    thumbnailViewModel = thumbnailViewModel,
                    query = movieTitleQuery,
                    isContextualActionModeActive = isContextualActionModeActive,
                    countOfSelectedItems = countOfSelectedItems,
                    selectedImages = selectedImages,
                    scrollState = scrollState
                )
            }
        )
    } else {
        navController.navigate(Pages.SignIn.route)
    }
}

@Composable
fun MainAppBar(
    navController: NavController,
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    thumbnailViewModel: ThumbnailViewModel,
    mAuth: FirebaseAuth,
    query: String,
    scrollState: LazyStaggeredGridState
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                navController = navController,
                onSearchClicked = onSearchTriggered,
                mAuth = mAuth,
                thumbnailViewModel = thumbnailViewModel,
                movieTitleQuery = query,
                scrollState = scrollState
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked,
                navController = navController,
                mAuth = mAuth
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DefaultAppBar(
    navController: NavController,
    onSearchClicked: () -> Unit,
    mAuth: FirebaseAuth,
    thumbnailViewModel: ThumbnailViewModel,
    movieTitleQuery: String,
    scrollState: LazyStaggeredGridState
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isContextualActionModeActive = remember { mutableStateOf(false) }
    val countOfItemsSelected = remember { mutableStateOf(0) }

    val selectedImages = ArrayList<FavoriteImage>()

    val favoriteViewModel = FavoriteViewModel()

    var localMenu by remember { mutableStateOf(false) }

    val appBarOffset = animateFloatAsState(
        targetValue = if (scrollState.firstVisibleItemScrollOffset > 0) -56f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    DrawerHeader()
                    DrawerBody(
                        items = listOf(
                            Pages.Gallery, Pages.Favorites, Pages.ProfileManagement
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
                        ContextualTopBar(
                            countOfSelectedItems = countOfItemsSelected,
                            imageVector = Icons.Default.Favorite,
                            performAction = { favoriteViewModel.addFavorites(selectedImages) }
                        )
                    } else {
                        AnimatedVisibility(
                            visible = appBarOffset.value >= 0f,
                            enter = fadeIn(),
                            exit = fadeOut()) {
                            TopAppBar(
                                title = {  },
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .wrapContentWidth()
                                    .height(56.dp),
                                actions = {
                                    TextField(value = "", onValueChange = {

                                    },
                                        placeholder = {
                                            Text(
                                                text = "Click on the search bar to begin",
                                                modifier = Modifier.alpha(ContentAlpha.medium),
                                                color = Color.White,
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(horizontal = 0.dp)
                                            .fillMaxWidth()
                                            .onFocusChanged {
                                                if (it.isFocused) {
                                                    onSearchClicked()
                                                }
                                            },
                                        leadingIcon = {
                                            Row {
                                                IconButton(onClick = {
                                                    scope.launch {
                                                        drawerState.open()
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = "Toggle DrawerLayout",
                                                        tint = Color.White
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        onSearchClicked()
                                                    }, modifier = Modifier.alpha(ContentAlpha.medium)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = "Search for movies",
                                                        tint = Color.White
                                                    )
                                                }
                                            }
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                localMenu = !localMenu
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "More Options",
                                                    tint = Color.White
                                                )
                                            }
                                            DropdownMenu(expanded = localMenu,
                                                onDismissRequest = { localMenu = false }) {
                                                DropdownMenuItem(text = {
                                                    Text(text = "Sign Out", color = Color.White)
                                                }, onClick = {
                                                    if (mAuth.currentUser != null) {
                                                        mAuth.signOut()
                                                        navController.navigate(Pages.SignIn.route)
                                                    }
                                                })
                                            }
                                        })
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (scrollState.firstVisibleItemIndex == 0) {
                        FAB()
                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = {
                    if (scrollState.firstVisibleItemIndex == 0) {
                        BottomBar(navController)
                    }
                },
                content = {
                        PopulateView(
                            stateOfOnSearchClicked = false,
                            navController = navController,
                            thumbnailViewModel = thumbnailViewModel,
                            isContextualActionModeActive = isContextualActionModeActive,
                            countOfSelectedItems = countOfItemsSelected,
                            query = movieTitleQuery,
                            selectedImages = selectedImages,
                            scrollState = scrollState
                        )
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    navController: NavController,
    mAuth: FirebaseAuth
) {
    var localMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = {},
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .wrapContentWidth()
            .height(56.dp)
            .onKeyEvent {
                if (it.key == Key.Back) {
                    onCloseClicked()
                }
                true
            },
        actions = {
            TextField(value = text, onValueChange = {
                onTextChange(it)
            },
                modifier = Modifier.fillMaxWidth(), placeholder = {
                    Text(
                        text = "Search the movie title here",
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        color = Color.White
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()) {
                                onTextChange("")
                            } else {
                                onCloseClicked()
                            }
                        }, modifier = Modifier.alpha(ContentAlpha.medium)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Search for movies",
                            tint = Color.White
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        localMenu = !localMenu
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(expanded = localMenu, onDismissRequest = { localMenu = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "Sign Out", color = Color.White)
                        }, onClick = {
                            if (mAuth.currentUser != null) {
                                mAuth.signOut()
                                navController.navigate(Pages.SignIn.route)
                            }
                        }
                        )
                    }
                }, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ), keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClicked(text)
                    }
                )
            )
        }
    )
}

@Composable
fun PopulateView(
    stateOfOnSearchClicked: Boolean,
    navController: NavController,
    thumbnailViewModel: ThumbnailViewModel,
    query: String,
    isContextualActionModeActive: MutableState<Boolean>,
    countOfSelectedItems: MutableState<Int>,
    selectedImages: ArrayList<FavoriteImage>,
    scrollState: LazyStaggeredGridState
) {
    val selectedItems = remember { mutableStateListOf<Search>() }
    val isLongPressActive = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val backHandler = LocalOnBackPressedDispatcherOwner.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val activity = (LocalContext.current as? Activity)

    if (stateOfOnSearchClicked) {
        val locallyObservableData = thumbnailViewModel.apiResult.observeAsState()
        val message = thumbnailViewModel.message.observeAsState()

        DisposableEffect(Unit) {
            val callback = object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isContextualActionModeActive.value) {
                        isContextualActionModeActive.value = false
                        isLongPressActive.value = false
                        selectedItems.clear()
                    }
                    else {
                        scope.launch {
                            thumbnailViewModel.updateSearchTextState(newValue = "")
                            thumbnailViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
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
                selectedImages.add(FavoriteImage(it.poster))
            }
        }

        val checkList = ArrayList<Search>()

        val key = remember(query) { query }

        LaunchedEffect(key) {
            thumbnailViewModel.getImages(query)
        }
        locallyObservableData.value?.let {
            message.value?.let {
                it.getContentIfNotHandled()?.let {
                    Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
                }
            }
            when (it.status) {
                Status.LOADING -> {
                    LoadingUI()
                }
                Status.SUCCESS -> {
                    it.data?.let {
                        checkList.addAll(it.search)
                    }
                    if (checkList.isEmpty()) {
                        EmptyResultsUI()
                    } else {
                        LazyVerticalStaggeredGrid(
                            state = scrollState,
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier
                                .wrapContentSize()
                                .background(Color(240, 244, 244)),
                            contentPadding = PaddingValues(16.dp)

                        ) {
                            items(it.data!!.search) { listOfMovies ->
                                ListItem(
                                    navController, listOfMovies, selectedItems, isLongPressActive
                                ) { isSelected ->
                                    if (isSelected) {
                                        selectedItems.add(listOfMovies)
                                    } else {
                                        selectedItems.remove(listOfMovies)
                                    }
                                }
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .wrapContentSize()
                            .background(Color(240, 244, 244))
                    ) {
                        Text(
                            "Looks like you're offline",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    } else {
        val locallyObservableData = thumbnailViewModel.movieData.observeAsState()
        DisposableEffect(Unit) {
            val callback = object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    thumbnailViewModel.updateSearchTextState(newValue = "")
                    thumbnailViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                    if (isContextualActionModeActive.value) {
                        isContextualActionModeActive.value = false
                        isLongPressActive.value = false
                        selectedItems.clear()
                    } else {
                        scope.launch {
                            if(stateOfOnSearchClicked) {
                                thumbnailViewModel.updateSearchTextState(newValue = "")
                                thumbnailViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                            } else {
                                activity?.finish()
                                drawerState.close()
                            }
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
                selectedImages.add(FavoriteImage(it.poster))
            }
        }

        val checkList = ArrayList<Search>()
        locallyObservableData.value?.let { list ->
            list.data?.let { list ->
                checkList.addAll(list)
            }
        }
        if (checkList.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentSize()
                    .background(Color(240, 244, 244))
            ) {
                Text(
                    "Type in search bar to get movie result",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color(240, 244, 244)),
                contentPadding = PaddingValues(16.dp)
            ) {
                locallyObservableData.value?.let {
                    when (it.status) {
                        Status.LOADING -> {

                        }
                        Status.SUCCESS -> {
                            it.data?.let { moviesList ->
                                items(moviesList) { movie ->
                                    ListItem(
                                        navController,
                                        movie,
                                        selectedItems,
                                        isLongPressActive,
                                    ) { isSelected ->
                                        if (isSelected) {
                                            selectedItems.add(movie)
                                        } else {
                                            selectedItems.remove(movie)
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
        thumbnailViewModel.getDataFromCloud()
    }
}

@Composable
fun LoadingUI() {
    // Show loading UI
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .background(Color(240, 244, 244))
    ) {
        Text(
            "Movie results loading",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun EmptyResultsUI() {
    // Show empty results UI
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .background(Color(240, 244, 244))
    ) {
        Text(
            "Type in search bar to get movie result",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ListItem(
    navController: NavController,
    search: Search,
    selectedItems: MutableList<Search>,
    isLongPressActive: MutableState<Boolean>,
    onSelectionChange: (Boolean) -> Unit
) {
    val isSelected = selectedItems.contains(search)
    val isLongPressEnabled = remember { mutableStateOf(false) }

    val longPressDuration = 500 // Adjust the duration as needed
    val coroutineScope = rememberCoroutineScope()
    var longPressJob: Job? by remember { mutableStateOf(null) }
    Box(modifier = Modifier
        .padding(15.dp)
        .wrapContentSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (isLongPressActive.value) {
                    isLongPressActive.value = false
                } else {
                    val encodedUrl = URLEncoder.encode(
                        search.poster, StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate(
                        route = Pages.SingleImagePage.passImgId(encodedUrl)
                    )
                }
            }, onLongPress = {
                isLongPressEnabled.value = true
                longPressJob = coroutineScope.launch {
                    delay(longPressDuration.toLong())
                    if (isLongPressEnabled.value) {
                        isLongPressActive.value = true
                    }
                }
            })
        }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(search.poster).crossfade(true)
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
fun PreviewGalleryPage() {
    GalleryPage(rememberNavController(), rememberLazyStaggeredGridState())
}
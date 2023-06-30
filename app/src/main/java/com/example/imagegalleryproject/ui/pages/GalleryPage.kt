package com.example.imagegalleryproject.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.*
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
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
import com.example.imagegalleryproject.screens.PagesWithIconAndTitles
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
    navController: NavController,
    scrollState: LazyStaggeredGridState
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

    val context = LocalContext.current



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
            context = context,
            query = movieTitleQuery,
            scrollState = scrollState
        )
    }, content = {
        if (mAuth.currentUser != null) {
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
        } else {
            navController.navigate(Pages.SignIn.route)
        }
    })
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
    context: Context,
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
                context = context,
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
                thumbnailViewModel = thumbnailViewModel
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DefaultAppBar(
    navController: NavController,
    onSearchClicked: () -> Unit,
    mAuth: FirebaseAuth,
    thumbnailViewModel: ThumbnailViewModel,
    context: Context,
    movieTitleQuery: String,
    scrollState: LazyStaggeredGridState
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val isContextualActionModeActive = remember { mutableStateOf(false) }
    val countOfItemsSelected = remember { mutableStateOf(0) }

    val selectedImages = ArrayList<FavoriteImage>()


    val favoriteViewModel = FavoriteViewModel()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Column {
                DrawerHeader()
                DrawerBody(items = listOf(
                    PagesWithIconAndTitles.Gallery,
                    PagesWithIconAndTitles.Favorites,
                    PagesWithIconAndTitles.ProfileManagement
                ), onItemClick = {
                    scope.launch {
                        navController.navigate(it.route) {
                            popUpTo = navController.graph.getStartDestination()
                            launchSingleTop = true
                        }
                        drawerState.close()
                    }
                })
            }
        }
    }, content = {
        androidx.compose.material.Scaffold(
            topBar = {
            if (isContextualActionModeActive.value) {
                // Render contextual action mode topBar
                // Replace with your desired implementation
                ContextualTopBar(
                    countOfSelectedItems = countOfItemsSelected,
                    imageVector = Icons.Default.Favorite,
                    performAction = { favoriteViewModel.addFavorites(selectedImages) }
                )
            } else {
                androidx.compose.material.TopAppBar(
                    title = {
                    Text(
                        text = "GalleryFragment",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }, actions = {
                    IconButton(onClick = {
                        onSearchClicked()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        showMenu = !showMenu
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "Sign Out", color = Color.White)
                        }, onClick = {
                            if (mAuth.currentUser != null) {
                                mAuth.signOut()
                                navController.navigate(Pages.SignIn.route)
                            }
                        })
                    }
                }, navigationIcon = {
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
                })
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
            })
    })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    thumbnailViewModel: ThumbnailViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onKeyEvent {
                if (it.key == Key.Back) {
                    onCloseClicked()
                }
                true
            }, color = MaterialTheme.colorScheme.primary
    ) {
        TextField(value = text, onValueChange = {
            onTextChange(it)
        }, modifier = Modifier.fillMaxWidth(), placeholder = {
            Text(
                text = "Search the movie title here",
                modifier = Modifier.alpha(ContentAlpha.medium),
                color = Color.White
            )
        }, leadingIcon = {
            IconButton(
                onClick = {}, modifier = Modifier.alpha(ContentAlpha.medium)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search for movies",
                    tint = Color.White
                )
            }
        }, trailingIcon = {
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
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Search",
                    tint = Color.White
                )
            }
        }, keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ), keyboardActions = KeyboardActions(onSearch = {
            onSearchClicked(text)
        })
        )
    }
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

    val localCopyCount = countOfSelectedItems

  if(stateOfOnSearchClicked) {
      val locallyObservableData = thumbnailViewModel.apiResult.observeAsState()
      val message = thumbnailViewModel.message.observeAsState()

      DisposableEffect(Unit) {
          val callback = object : OnBackPressedCallback(true) {
              override fun handleOnBackPressed() {
                  if (isContextualActionModeActive.value) {
                      isContextualActionModeActive.value = false
                      isLongPressActive.value = false
                      selectedItems.clear()
                  } else {

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
              selectedImages.add(FavoriteImage(it.Poster))
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
                      checkList.addAll(it.Search)
                  }
                  if (checkList.isEmpty()) {
                      EmptyResultsUI()
                  } else {
                      LazyVerticalStaggeredGrid(
                          columns = StaggeredGridCells.Fixed(2),
                          modifier = Modifier
                              .fillMaxSize()
                              .background(Color(240, 244, 244)),
                          contentPadding = PaddingValues(16.dp)

                      ) {
                          items(it.data!!.Search) { listOfMovies ->
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
                          .fillMaxSize()
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
          localCopyCount.value = selectedItems.size
          selectedItems.forEach {
              selectedImages.add(FavoriteImage(it.Poster))
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
                  .fillMaxSize()
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
                  .fillMaxSize()
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
            .fillMaxSize()
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
            .fillMaxSize()
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
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (isLongPressActive.value) {
                    isLongPressActive.value = false
                } else {
                    val encodedUrl = URLEncoder.encode(
                        search.Poster, StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate(
                        route = PagesWithIconAndTitles.SingleImagePage.passImgId(encodedUrl)
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
            model = ImageRequest.Builder(LocalContext.current).data(search.Poster)
                .crossfade(true).build(),
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
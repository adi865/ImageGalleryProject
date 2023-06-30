package com.example.imagegalleryproject.ui.pages


import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.imagegalleryproject.BottomBar.BottomBar
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.TopBar
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.screens.PagesWithIconAndTitles
import com.example.imagegalleryproject.ui.MainActivity
import com.example.imagegalleryproject.ui.drawerlayout.DrawerBody
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.example.imagegalleryproject.viewmodel.UserInfoViewModel
import com.example.imagegalleryproject.widgets.FAB
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch
import org.checkerframework.common.subtyping.qual.Bottom


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileManagement(
    navController: NavController,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userInfoViewModel = UserInfoViewModel()
    userInfoViewModel.getUserInfo(navController)


    val userDataInfoInterface = userInfoViewModel.userInforDataObserver.observeAsState()

    val context = LocalContext.current

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
                    TopBar(title = "ProfileManagement", drawerState = drawerState, navController = navController)
                },
                floatingActionButton = {
                    FAB()
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = {
                    BottomBar(navController)
                }
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(240, 244, 244))) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .background(Color.White, shape = RoundedCornerShape(2.dp))
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Get the context and lifecycle owner
                        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                        var isUploading by remember { mutableStateOf(false) }

                        val context = LocalContext.current

                        val launcher =
                            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                                if (uri != null) {
                                    selectedImageUri = uri
                                    uploadImageToFirebase(uri)
                                }
                            }

                        val imageUrl = remember { mutableStateOf("") }
                        val mAuth = FirebaseAuth.getInstance()

                        fun loadImageFromFirebase() {
                            // Get a reference to the Firebase Storage
                            val storage = FirebaseStorage.getInstance()
                            val storageReference =
                                storage.reference.child("${mAuth.currentUser!!.uid}/images/")

                            // Download the image and get its download URL
                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                // Assign the download URL to the imageUrl state variable
                                imageUrl.value = uri.toString()
                            }.addOnFailureListener {
                                // Handle any errors that occurred while fetching the image
                                    Toast.makeText(
                                        context,
                                        "Failed to load image from Firebase Storage",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }
                        }
                        LaunchedEffect(Unit) {
                            loadImageFromFirebase()
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.48f)
                                .padding(start = 29.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .clickable {
                                    launcher.launch("image/*")
                                }
                        ) {
                            if (imageUrl.value.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(imageUrl.value),
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.images),
                                    contentDescription = "Add Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(0.52f)
                                .padding(start = 20.dp)
                        ) {
                            userDataInfoInterface.value?.let {
                                Text(
                                    text = it.result.get("name").toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.Black
                                )
                                Text(
                                    text = it.result.get("email").toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 14.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(2.dp)),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileItem(
                            text = "Personal Info",
                            icon = painterResource(R.drawable.img_user),
                            modifier = Modifier
                                .weight(0.96f)
                                .fillMaxWidth()
                        )

                        ProfileItem(
                            text = "Settings",
                            icon = painterResource(R.drawable.img_settings),
                            modifier = Modifier
                                .weight(0.95f)
                                .fillMaxWidth()
                        )

                        ProfileItem(
                            text = "Support",
                            icon = painterResource(R.drawable.img_checkmark),
                            modifier = Modifier
                                .weight(0.95f)
                                .fillMaxWidth()
                        )

                        ProfileItem(
                            text = "Privacy and Policy",
                            icon = painterResource(R.drawable.img_menu),
                            modifier = Modifier
                                .weight(0.97f)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun ProfileItem(
    text: String,
    icon: Painter,
    modifier: Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painter = icon, contentDescription = null, modifier = Modifier.weight(0.2f))
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp)
                .clickable { }
                .weight(0.7f),
            color = Color.Black
        )
        Icon(
            painter = painterResource(R.drawable.img_arrowright),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.weight(0.1f)
        )
    }
}

fun uploadImageToFirebase(uri: Uri) {
    var mAuth = FirebaseAuth.getInstance()
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage!!.reference
    val imageRef = storageRef.child("${mAuth.currentUser!!.uid}/images/")

    imageRef.putFile(uri)
        .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot?> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                imageRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                    override fun onSuccess(uri: Uri?) {

                    }
                })
            }
        }).addOnFailureListener {

        }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileManagement() {
    ProfileManagement(rememberNavController())
}




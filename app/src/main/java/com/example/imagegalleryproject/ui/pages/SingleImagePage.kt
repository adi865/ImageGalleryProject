package com.example.imagegalleryproject.ui.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.ui.drawerlayout.DrawerBody
import com.example.imagegalleryproject.ui.drawerlayout.DrawerHeader
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SingleImagePage(navController: NavController, imgId: String) {
    val modifiedImgId = imgId.removeSurrounding("{", "}")
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    val tempImageResArray = ArrayList<FavoriteImage>()
    val favoriteViewModel = FavoriteViewModel()

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
                            Pages.ProfileManagement,
                            Pages.SingleImagePage
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
            Scaffold {
                BottomSheetScaffold(
                    sheetElevation = 0.dp,
                    scaffoldState = bottomSheetScaffoldState,
                    content = {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 0.dp)
                                .fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_arrowleft),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 5.dp)
                                    .zIndex(2f)
                                    .size(26.dp)
                                    .align(Alignment.TopStart)
                                    .clickable {
                                        navController.popBackStack()
                                    }
                            )
                            Image(
                                painter = painterResource(id = R.drawable.img_vector),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 10.dp, top = 5.dp)
                                    .size(26.dp)
                                    .zIndex(2f)
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        tempImageResArray.add(FavoriteImage(modifiedImgId))
                                        favoriteViewModel.addSingleFavorite(
                                            tempImageResArray
                                        )
                                    }
                            )

                            CoilImage(
                                imageModel = modifiedImgId,
                                contentScale = ContentScale.Crop,
                                placeHolder = ImageBitmap.imageResource(R.drawable.img_cover),
                                error = ImageBitmap.imageResource(R.drawable.poster_placeholder),
                                modifier = Modifier.fillMaxSize()
                            )

                        }
                    },
                    sheetPeekHeight = 200.dp,
                    sheetBackgroundColor = Color.Transparent,
                    sheetContent = {
                        Button(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(
                                    253,
                                    129,
                                    74
                                ),
                            ), onClick = {},
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(0.dp)
                                .zIndex(2f)
                                .offset(y = 25.dp)
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Text("Overview", color = Color.White)
                        }
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                .height(650.dp)
                                .background(Color.White),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_group2),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dimensionResource(id = R.dimen._14pxh))
                                )
                                Text(
                                    text = "Digital Illustration",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .weight(0.89f),
                                    style = TextStyle(fontSize = 16.sp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Title",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(
                                            start = dimensionResource(id = R.dimen._81pxh),
                                            top = dimensionResource(id = R.dimen._24pxv),
                                            end = dimensionResource(id = R.dimen._81pxh)
                                        ),
                                    style = TextStyle(fontSize = 16.sp)
                                )
                                Text(
                                    text = "Title Here",
                                    color = Color.Black,
                                    modifier = Modifier.padding(
                                        start = dimensionResource(id = R.dimen._81pxh),
                                        end = dimensionResource(id = R.dimen._81pxh)
                                    ),
                                    style = TextStyle(fontSize = 16.sp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column {
                                Text(
                                    text = "Size",
                                    color = Color.Black,
                                    style = TextStyle(fontSize = 16.sp),
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier.padding(
                                        start = dimensionResource(id = R.dimen._81pxh),
                                        top = dimensionResource(id = R.dimen._24pxv),
                                        end = dimensionResource(id = R.dimen._81pxh)
                                    )
                                )
                                Text(
                                    text = "Dimensions here",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 16.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(
                                        start = dimensionResource(id = R.dimen._81pxh),
                                        end = dimensionResource(id = R.dimen._81pxh)
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = "Location",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(
                                            start = dimensionResource(id = R.dimen._81pxh),
                                            top = dimensionResource(id = R.dimen._24pxv),
                                            end = dimensionResource(id = R.dimen._81pxh)
                                        ),
                                    style = TextStyle(fontSize = 16.sp)
                                )
                                Text(
                                    text = "Location Here",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = dimensionResource(id = R.dimen._81pxh),
                                            end = dimensionResource(id = R.dimen._81pxh)
                                        ),
                                    style = TextStyle(fontSize = 16.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = dimensionResource(id = R.dimen._16pxh),
                                        top = dimensionResource(id = R.dimen._19pxv),
                                        end = dimensionResource(id = R.dimen._16pxh)
                                    )
                                    .height(dimensionResource(id = R.dimen._1pxv))
                            )
                            Text(
                                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris fermentum.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris fermentum.",
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = dimensionResource(id = R.dimen._33pxh),
                                        top = dimensionResource(id = R.dimen._10pxv),
                                        end = dimensionResource(id = R.dimen._33pxh)
                                    ),
                                style = TextStyle(fontSize = 16.sp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Lorem ipsum sub topic",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(
                                        start = dimensionResource(id = R.dimen._81pxh),
                                        top = dimensionResource(id = R.dimen._32pxv),
                                        end = dimensionResource(id = R.dimen._81pxh)
                                    )
                            )
                            Text(
                                text = "Podcast with lorem ipsum",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = dimensionResource(id = R.dimen._16pxh),
                                        end = dimensionResource(id = R.dimen._16pxh)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Image(
                                painter = painterResource(id = R.drawable.img_player),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dimensionResource(id = R.dimen._31pxh))
                                    .padding(
                                        start = dimensionResource(id = R.dimen._16pxh),
                                        top = dimensionResource(id = R.dimen._20pxv),
                                        end = dimensionResource(id = R.dimen._16pxh),
                                        bottom = dimensionResource(id = R.dimen._20pxv)
                                    ),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                )
            }
        }
    )
}
package com.example.imagegalleryproject.ui.drawerlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.viewmodel.UserInfoViewModel

@Composable
fun DrawerHeader() {
    val userInfoViewModel = UserInfoViewModel()
    userInfoViewModel.getUserInfo()
    var userDataInfoInterface = userInfoViewModel.userInforDataObserver.observeAsState()
    var getUserProfilePic = userInfoViewModel.userProfilePic.observeAsState()
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFFFAB91))
        .padding(top = 32.dp, bottom = 0.dp)
    ) {
      Column {
          getUserProfilePic.value?.let {
              AsyncImage(
                  model = ImageRequest.Builder(LocalContext.current)
                      .data(it)
                      .placeholder(R.drawable.man)
                      .crossfade(true)
                      .build(),
                  contentDescription = "barcode image",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                      .padding(start = 18.dp)
                      .size(64.dp)
                      .clip(CircleShape)
              )
          }
          Spacer(modifier = Modifier.height(41.dp))
          userDataInfoInterface.value?.let {
              Text("${it.result.get("name").toString()}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp, top = 0.dp))
              Text("${it.result.get("email").toString()}", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp, top = 0.dp))
          }

      }

    }
}

@Composable
fun DrawerBody(
    items: List<Pages>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (Pages) -> Unit
) {
    LazyColumn(modifier = Modifier.background(Color.White).fillMaxHeight()) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)
            ) {
                Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.DarkGray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = item.title, style = itemTextStyle, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDrawerHeader() {
    DrawerHeader()
    DrawerBody(items = listOf(
        Pages.Gallery,
        Pages.Favorites
    ), onItemClick = {
        println("Clicked")
    })
}
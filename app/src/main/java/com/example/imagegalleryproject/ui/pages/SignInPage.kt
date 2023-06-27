package com.example.imagegalleryproject.ui.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.util.mToast
import com.google.firebase.auth.FirebaseAuth
import com.google.relay.compose.RowScopeInstanceImpl.align

@Composable
fun SignInPage(navController: NavController) {
    val mAuth = FirebaseAuth.getInstance()
    var signInButtonClicked by remember {
        mutableStateOf(false)
    }
    val openDialog = remember { mutableStateOf(false) }

    var emailState by remember {
        mutableStateOf("")
    }
    var passwordState by remember {
        mutableStateOf("")
    }

    val mContext = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(Color(240, 244, 244))
    ) {
        if (openDialog.value) {
            ResetPassword()
        }
        Box(
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(10.dp)
          ) {
              Image(
                  painter = painterResource(id = R.drawable.img_image1),
                  contentDescription = "Gallery Project",
                  modifier = Modifier
                      .width(82.4.dp)
                      .height(80.2.dp),
                  contentScale = ContentScale.Fit
              )
              Text(
                  text = "Sign In",
                  fontWeight = FontWeight.Bold,
                  fontSize = 22.sp,
                  color = Color.Black
              )
          }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clip(RoundedCornerShape(32.dp)).background(Color.White).padding(35.dp).wrapContentHeight()
        ) {
            Text(text = "Email", color = Color.Black, textAlign = TextAlign.Left, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            TextField(
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF9F9FB), unfocusedContainerColor = Color(0xFFF9F9FB), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black),
                value = emailState, onValueChange = {
                    emailState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your email")
                },
                leadingIcon = {
                    Icon(imageVector = InputType.Name.icon, null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                visualTransformation = VisualTransformation.None
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(text = "Password", color = Color.Black, textAlign = TextAlign.Left, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            TextField(
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF9F9FB), unfocusedContainerColor = Color(0xFFF9F9FB), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black),
                value = passwordState, onValueChange = {
                    passwordState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your email")
                },
                leadingIcon = {
                    Icon(imageVector = InputType.Password.icon, null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                "Forgot your password?", modifier = Modifier
                    .align(Alignment.Start)
                    .clickable(
                        onClick = {
                            openDialog.value = !openDialog.value
                        }
                    ),
                color = Color(253, 129, 74)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = {
                    signInButtonClicked = true
                    if (emailState.isNotEmpty() && passwordState.isNotEmpty()) {
                        mAuth.signInWithEmailAndPassword(emailState, passwordState)
                            .addOnCompleteListener {

                                if (it.isSuccessful) {
                                    navController.navigate(route = Pages.Gallery.route)
                                } else {
                                    mToast(mContext, it.exception.toString())
                                }
                            }
                    } else {
                        mToast(mContext, "Fields can't be empty")
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(253, 129, 74)),
                modifier = Modifier
                    .width(176.6.dp)
                    .border(
                        width = 1.dp,
                        color = Color(253, 129, 74),
                        shape = RoundedCornerShape(60)
                    )
            ) {
                Text("Sign In", color = Color.White)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Don't have an account?",
                    color = Color(134, 134, 134)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Sign Up",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        navController.navigate(route = Pages.SignUp.route)
                    }
                )
            }
        }
    }
    }

sealed class InputType(
    val label: String,
    val icon: ImageVector,
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation
) {
    object Name : InputType(
        label = "Username",
        icon = Icons.Default.Person,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Password : InputType(
        label = "Password",
        icon = Icons.Default.Lock,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = PasswordVisualTransformation()
    )
}



@Preview(showBackground = true)
@Composable
fun PreviewSignInPage() {
    SignInPage(navController = rememberNavController())
}
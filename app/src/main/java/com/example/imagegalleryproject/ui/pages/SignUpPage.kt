package com.example.imagegalleryproject.ui.pages

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
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.imagegalleryproject.model.User
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.util.FirebaseUtils
import com.example.imagegalleryproject.util.isTextEmpty
import com.example.imagegalleryproject.util.isValidEmail
import com.example.imagegalleryproject.util.mToast
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpPage(navController: NavController) {
    val mAuth = FirebaseAuth.getInstance()
    var signUpButtonClicked by remember {
        mutableStateOf(false)
    }
    var userNameState by remember {
        mutableStateOf("")
    }
    var emailState by remember {
        mutableStateOf("")
    }
    var passwordState by remember {
        mutableStateOf("")
    }

    var confirmPasswordState by remember {
        mutableStateOf("")
    }

    val mContext = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(Color(240, 244, 244))
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }

        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(35.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = "Username",
                color = Color.Black,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9FB),
                    unfocusedContainerColor = Color(0xFFF9F9FB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                value = userNameState, onValueChange = {
                    userNameState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Your Username")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.VerifiedUser, null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                visualTransformation = VisualTransformation.None
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Email",
                color = Color.Black,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9FB),
                    unfocusedContainerColor = Color(0xFFF9F9FB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                value = emailState, onValueChange = {
                    emailState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your email")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                visualTransformation = VisualTransformation.None
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Password",
                color = Color.Black,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9FB),
                    unfocusedContainerColor = Color(0xFFF9F9FB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                value = passwordState, onValueChange = {
                    passwordState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your password")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, null)
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
                text = "Confirm Password",
                color = Color.Black,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9FB),
                    unfocusedContainerColor = Color(0xFFF9F9FB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                value = confirmPasswordState, onValueChange = {
                    confirmPasswordState = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Re-enter your password")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = {
                    signUpButtonClicked = true
                    if (!isTextEmpty(userNameState) && !isTextEmpty(emailState) && !isTextEmpty(
                            passwordState
                        ) && !isTextEmpty(confirmPasswordState)
                    ) {
                        if (isValidEmail(emailState)) {
                            if (passwordState == confirmPasswordState) {
                                mAuth.createUserWithEmailAndPassword(emailState, passwordState)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val userId = it.result.user!!.uid
                                            val account = { userId }
                                            val user =
                                                User(userNameState, emailState, passwordState)
                                            FirebaseUtils().firebaseStoreDatabase.collection("accounts")
                                                .document(userId).set(account)
                                            FirebaseUtils().firebaseStoreDatabase.collection("accounts")
                                                .document(userId).set(user)
                                            navController.navigate(Pages.SignIn.route)
                                        } else {
                                            mToast(mContext, it.exception.toString())
                                        }
                                    }
                            } else {
                                mToast(mContext, "Passwords don't match")
                            }
                        } else {
                            mToast(mContext, "Email entered in wrong format")
                        }
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
                Text("Sign Up", color = Color.White)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    fontSize = 12.sp,
                    color = Color(134, 134, 134)
                )
                Spacer(modifier = Modifier.width(2.dp))
                TextButton(
                    onClick = {
                        navController.navigate(route = Pages.SignIn.route)
                    }
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 12.sp,
                        color = Color.Blue
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignUpPage() {
    SignUpPage(rememberNavController())
}
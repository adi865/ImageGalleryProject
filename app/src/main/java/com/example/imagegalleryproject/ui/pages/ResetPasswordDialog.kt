package com.example.imagegalleryproject.ui.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.imagegalleryproject.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ResetPassword() {
    var shouldDismiss = rememberSaveable {
        mutableStateOf(false)
    }
    var emailSentClicked by rememberSaveable() {
        mutableStateOf(false)
    }

    var mAuth = FirebaseAuth.getInstance()

    if (shouldDismiss.value) return

    Dialog(
        onDismissRequest = {
            shouldDismiss.value = true
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(  horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close Fragment",
                        modifier = Modifier
                            .padding(end = 5.dp, top = 5.dp)
                            .size(18.dp)
                            .align(Alignment.TopEnd).clickable {
                                shouldDismiss.value = true
                            }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp),
                ) {
                    var emailState by rememberSaveable {
                        mutableStateOf("")
                    }

                    val mContext = LocalContext.current
                    Image(
                        painter = painterResource(id = R.drawable.img_image1),
                        contentDescription = "Gallery Project",
                        modifier = Modifier
                            .width(82.4.dp)
                            .height(80.2.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Reset Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Please enter your email so we can send you instructions to reset your password", textAlign = TextAlign.Center, color = Color(0xFF78829D), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(7.dp))
                    TextField(
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF9F9FB), unfocusedContainerColor = Color(0xFFF9F9FB), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black),
                        value = emailState, onValueChange = {
                            emailState = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Enter your email here", fontWeight = FontWeight.Bold)
                        },
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
                    Button(
                        onClick = {
                            emailSentClicked = true
                            mAuth.sendPasswordResetEmail(emailState).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // if isSuccessful then done message will be shown
                                    // and you can change the password
                                    Toast.makeText(mContext, "Email Sent", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(mContext, "Not a valid email", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(mContext, "Email doesn't exist", Toast.LENGTH_SHORT).show()
                            }
                            shouldDismiss.value = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(
                                253,
                                129,
                                74
                            )
                        ),
                        modifier = Modifier
                            .width(195.6.dp)
                            .border(
                                width = 1.dp,
                                color = Color(253, 129, 74),
                                shape = RoundedCornerShape(60)
                            )
                    ) {
                        Text("Reset Password", color = Color.White)
                    }
                }
            }
        }
    }
}
package com.example.imagegalleryproject.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.imagegalleryproject.ui.MainActivity


const val SUCCESS = 200
const val FAILED = 400
const val SERVER_ERROR = 500


fun View.isVisible(isShowingLoading: Boolean, container: View) {
    if(isShowingLoading) {
        this.visibility = View.VISIBLE
        container.visibility = View.GONE
    } else  {
        this.visibility = View.GONE
        container.visibility = View.VISIBLE
    }
}

fun mToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun isTextEmpty(input: String?): Boolean {
    return  input == null || input.isEmpty()
}

fun isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}
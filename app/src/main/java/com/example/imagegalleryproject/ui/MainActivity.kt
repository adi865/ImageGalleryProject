package com.example.imagegalleryproject.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.Coil
import com.example.imagegalleryproject.ImageGalleryProjectTheme
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.navgraphs.SetupNavGraph
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val CAPTURE_IMAGE = 0

    private var fileUri: Uri? = null
    private var uriImage: Uri? = null
    private var newFile: File? = null

    private lateinit var mAuth: FirebaseAuth

    private lateinit var storageRef: StorageReference

    private lateinit var favoriteViewModel: FavoriteViewModel

    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            ImageGalleryProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(240, 244, 244)
                ) {
                  context = LocalContext.current
                    navController = rememberNavController()
                    SetupNavGraph(navController = navController)
                    favoriteViewModel = FavoriteViewModel()
                }
            }
        }
    }

        fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
    }

        override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
              Toast.makeText(context, "Grant Application permissions to open and capture images", Toast.LENGTH_SHORT).show()
            }
        }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            println("From inside onActivityResult")
            processCapturedPhoto()
            uploadImageToFirebaseStorage()
        }
    }
        fun startCamera() {
        //specify name of the image and name's format
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)  //This line creates an intent to capture an image using the device's camera.
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"
        val imagePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_images") //This line creates a file path where the captured image will be stored.
        if (!imagePath.exists()) {
            imagePath.mkdirs()
        }
        newFile = File(imagePath, imageFileName) //This line creates a new File object with the specified file path and file name.
        fileUri = FileProvider.getUriForFile(this, "com.example.imagegalleryproject.fileprovider", newFile!!) //This line generates a content URI for the file using a FileProvider. This URI is used to pass the file's location to other components of the app.
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) //This line sets the output file URI for the camera intent. It specifies where the captured image should be saved.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // This line adds flags to the intent to grant read and write permissions to the URI.
        startActivityForResult(intent, CAPTURE_IMAGE)
        val columns = arrayOf(MediaStore.Images.ImageColumns.DATA)
        val cursor: Cursor? = contentResolver.query(Uri.parse(fileUri.toString()), columns, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val photoPath: String? = newFile!!.absolutePath
            if (photoPath != null) {
                val file = File(photoPath)
                uriImage = Uri.fromFile(file)
            }
            cursor.close()
        } else {
            Toast.makeText(context, "Image couldn't retrieved from app's sandbox", Toast.LENGTH_SHORT).show()
        }
    }

    //access app's sandbox to get the captured image
     fun processCapturedPhoto() {
        println("From inside processCapturedPhoto")
        val columns = arrayOf(MediaStore.Images.ImageColumns.DATA)
        val cursor: Cursor? = contentResolver.query(Uri.parse(fileUri.toString()), columns, null, null, null)
        cursor!!.moveToFirst()
        val photoPath: String? = newFile!!.absolutePath
        if (photoPath != null) {
            val file = File(photoPath)
            uriImage = Uri.fromFile(file)
        }
    }

        fun uploadImageToFirebaseStorage() {
            mAuth = FirebaseAuth.getInstance()
            var storage = FirebaseStorage.getInstance()
            storageRef = storage!!.reference
        var placeHolderArrayForCapturedImage = ArrayList<FavoriteImage>()
        if(uriImage != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading from camera")
            progressDialog.show()
            val ref: StorageReference = storageRef.child("${mAuth.currentUser!!.uid}/favorites/")
            ref.putFile(uriImage!!).addOnSuccessListener {taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    placeHolderArrayForCapturedImage.add(FavoriteImage(imageUrl))
                    favoriteViewModel.addSingleFavorite(placeHolderArrayForCapturedImage)
                }
                progressDialog.dismiss()
                Toast.makeText(this, "Image Uploaded from Camera", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            } .addOnProgressListener { taskSnapshot ->
                // Progress Listener for loading
                // percentage on the dialog box
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }
        }
        else {
            Toast.makeText(this, "Failed to fetch image from the camera into the firebase", Toast.LENGTH_SHORT).show()
        }
    }
}


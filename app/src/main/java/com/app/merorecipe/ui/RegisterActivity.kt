package com.app.merorecipe.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.databinding.ActivityRegisterBinding
import com.app.merorecipe.entity.User
import com.app.merorecipe.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {


    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.INTERNET
    )
    private lateinit var binding: ActivityRegisterBinding
    private var activityScope = CoroutineScope(Dispatchers.IO)

    private var imageName: String = "";
    private var requestGalleryCode = 0
    private var requestCameraCode = 1
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        binding.chkShowHide.setOnClickListener {
            doPasswordHideShow()
        }

        binding.btnRegister.setOnClickListener {
            if (checkEmptyFields()) {
                doRegister()
            }
        }

        binding.profileImage.setOnClickListener {
            if (!hasPermission()) {
                requestPermission()

            } else {
                loadPopUpMenu()
            }


        }
    }

    private fun doPasswordHideShow() {
        if (binding.chkShowHide.isChecked) {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.chkShowHide.text = "Hide"
        } else {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.chkShowHide.text = "Show"
        }

    }

    private fun doRegister() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val gender = getGender()
        val user = User(
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            password = password,
            gender = gender
        )
        CoroutineScope(Dispatchers.IO).launch {

            val userRepository = UserRepository()
            val response = userRepository.register(user)
            Log.d("User",user.toString())
            if (response.success == true) {
                withContext(Main) {
                    if (imageUrl != null) {
                        val userId = response?.data?._id.toString()
                        uploadImage(userId)
                    }
                    Toast.makeText(
                        this@RegisterActivity,
                        "Account Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToLogin()
                }
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun getGender(): String {
        var gender = ""
        when {
            binding.rdoMale.isChecked -> {
                gender = "Male"
            }
            binding.rdoFemale.isChecked -> {
                gender = "Female"
            }
            binding.rdoOthers.isChecked -> {
                gender = "Others"
            }
        }
        return gender
    }

    private fun checkEmptyFields(): Boolean {
        var flag = false
        when {
            TextUtils.isEmpty(binding.etFirstName.text.toString()) -> {
                binding.etFirstName.error = "Firstname is required"
                binding.etFirstName.requestFocus()
                return flag
            }
            TextUtils.isEmpty(binding.etLastName.text.toString()) -> {
                binding.etLastName.error = "Lastname is required"
                binding.etLastName.requestFocus()
                return flag
            }
            TextUtils.isEmpty(binding.etEmail.text.toString()) -> {
                binding.etEmail.error = "Email is required"
                binding.etEmail.requestFocus()
                return flag
            }
            TextUtils.isEmpty(binding.etUsername.text.toString()) -> {
                binding.etUsername.error = "Username is required"
                binding.etUsername.requestFocus()
                return flag
            }
            TextUtils.isEmpty(binding.etPassword.text.toString()) -> {
                binding.etPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                return flag
            }
            else -> {
                flag = true
                return flag
            }
        }
    }

    private fun uploadImage(id: String) {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageName = file.name;
            val body =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepo = UserRepository()
                    val response = userRepo.uploadImage(id, body)
                    if (response.success == true) {
                        withContext(Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Image Uploaded",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    withContext(Main) {
                        Log.d("Error Uploading Image ", ex.localizedMessage)
                        Toast.makeText(
                            this@RegisterActivity,
                            ex.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this@RegisterActivity, binding.profileImage)
        popupMenu.menuInflater.inflate(R.menu.gallery_camera, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera ->
                    openCamera()
                R.id.menuGallery ->
                    openGallery()
            }
            true
        }
        popupMenu.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestGalleryCode)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, requestCameraCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestGalleryCode && data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == requestCameraCode && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
            }
        }
    }

    private fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? {
        var file: File? = null
        return try {
            file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    //Request Permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@RegisterActivity,
            permissions, 1434
        )
    }


    //Check If Permission is given
    private fun hasPermission(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
            }
        }
        return hasPermission
    }


}
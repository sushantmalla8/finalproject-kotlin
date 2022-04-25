package com.app.merorecipe.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View

import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.databinding.ActivityNewRecipeBinding
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.repository.UserRepository
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRecipeBinding

    private var imageName: String = "";
    private var requestGalleryCode = 0
    private var requestCameraCode = 1
    private var imageUrl: String? = null
    private var recipeID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        supportActionBar?.hide()

        getIntentDataFromRecipeActivity()
        binding.btnAddRecipe.setOnClickListener {
            if (isNotEmpty()) {
                doAddRecipe()
            }
        }
        //While updateing recipe
        binding.btnUpdate.setOnClickListener {
            updateRecipe()
        }
        //While Adding New Recipe
        binding.imgNewRecipe.setOnClickListener {
            loadPopUpMenu()
        }
        //While Updating Image
        binding.tvUpdateImage.setOnClickListener {
            loadPopUpMenu()
        }
    }

    private fun getIntentDataFromRecipeActivity() {
        val intent = intent
        if (intent.extras != null) {
            val recipeName = intent.getStringExtra("RecipeName")
            val description = intent.getStringExtra("Description")
            val category = intent.getStringExtra("Category")
            val image = intent.getStringExtra("Image")
            recipeID = intent.getStringExtra("Id")
            val path = ServiceBuilder.loadFilePath() + image
            binding.etNewRecipe.setText(recipeName.toString())
            binding.etDescription.setText(description.toString())
            binding.etNewRecipeCategory.setText(category.toString())
            Glide.with(this).load(path).into(binding.imgNewRecipe)

            binding.tvUpdateImage.visibility = View.VISIBLE
            binding.btnAddRecipe.visibility = View.GONE
            binding.btnUpdate.visibility = View.VISIBLE

        }


    }

    private fun updateRecipe() {
        val recipe = binding.etNewRecipe.text.toString()
        val category = binding.etNewRecipeCategory.text.toString()
        val description = binding.etDescription.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getRecipeRepo = RecipeRepository()
                val getResponse = getRecipeRepo.updateRecipe(
                    recipeID = recipeID!!,
                    Recipe(
                        recipeName = recipe,
                        category = category,
                        recipeDescription = description
                    )
                )
                if (getResponse.success == true) {
                    withContext(Main) {
                        GlobalClass.globalToast(this@NewRecipeActivity, "Recipe Updated")
                    }
                }
            } catch (ex: IOException) {
                withContext(Main) {
                    GlobalClass.globalToast(this@NewRecipeActivity, "Error: ${ex.localizedMessage}")
                }
            }
        }
    }

    private fun doAddRecipe() {
        val recipe = binding.etNewRecipe.text.toString()
        val category = binding.etNewRecipeCategory.text.toString()
        val description = binding.etDescription.text.toString()
        val addedBy = LoginActivity.Username

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getRecipeRepo = RecipeRepository()
                val getResponse = getRecipeRepo.addNewRecipe(
                    Recipe(
                        recipeName = recipe,
                        addedBy = addedBy,
                        category = category,
                        recipeDescription = description,
                        isFav = false,
                        favBy = ""
                    )
                )
                if (getResponse.success == true) {
                    if (imageUrl != null) {
                        uploadImage(getResponse?.data?._id.toString())
                    }
                    withContext(Main) {
                        Log.d("ID", getResponse?.data?._id.toString())
                        GlobalClass.globalToast(this@NewRecipeActivity, "Recipe Added")
                        clearText()
                    }
                }
            } catch (ex: IOException) {
                withContext(Main) {
                    GlobalClass.globalToast(this@NewRecipeActivity, "Error: ${ex.localizedMessage}")
                }
            }
        }
    }

    private fun clearText() {
        binding.etDescription.setText("")
        binding.etNewRecipeCategory.setText("")
        binding.etNewRecipe.setText("")
        binding.imgNewRecipe.setImageResource(0)
    }


    private fun isNotEmpty(): Boolean {
        var flag = true
        when {
            TextUtils.isEmpty(binding.etNewRecipe.text.toString()) -> {
                binding.etNewRecipe.error = "Required"
                binding.etNewRecipe.requestFocus()
                flag = false
            }
            TextUtils.isEmpty(binding.etNewRecipeCategory.text.toString()) -> {
                binding.etNewRecipeCategory.error = "Required"
                binding.etNewRecipeCategory.requestFocus()
                flag = false
            }
            TextUtils.isEmpty(binding.etDescription.text.toString()) -> {
                binding.etDescription.error = "Required"
                binding.etDescription.requestFocus()
                flag = false
            }

        }
        return flag
    }

    private fun uploadImage(recipeId: String) {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageName = file.name;
            val body =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val getRecipeRepo = RecipeRepository()
                    val getResponse = getRecipeRepo.uploadImage(recipeId, body)
                    if (getResponse.success == true) {
                        withContext(Main) {
                            GlobalClass.globalToast(this@NewRecipeActivity, "Image Uploaded")
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    withContext(Main) {
                        Log.d("Error Uploading Image ", ex.localizedMessage)
                        GlobalClass.globalToast(this@NewRecipeActivity, "${ex.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun updateImage(recipeId: String) {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageName = file.name;
            val body =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val getRecipeRepo = RecipeRepository()
                    val getResponse = getRecipeRepo.updateImage(recipeId, body)
                    if (getResponse.success == true) {
                        withContext(Main) {
                            GlobalClass.globalToast(this@NewRecipeActivity, "Image Updated")
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    withContext(Main) {
                        Log.d("Error Updating Image ", ex.localizedMessage)
                        GlobalClass.globalToast(this@NewRecipeActivity, "${ex.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, binding.imgNewRecipe)
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
                binding.imgNewRecipe.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == requestCameraCode && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                binding.imgNewRecipe.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
            }
            updateImage(recipeID.toString())
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


}
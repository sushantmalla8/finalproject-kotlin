package com.app.merorecipe.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.merorecipe.R
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.databinding.ActivityRegisterBinding
import com.app.merorecipe.databinding.FragmentProfileBinding
import com.app.merorecipe.entity.User
import com.app.merorecipe.repository.UserRepository
import com.app.merorecipe.ui.LoginActivity
import com.app.merorecipe.ui.NewRecipeActivity
import com.app.merorecipe.ui.RecipeActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var activityScope = CoroutineScope(Dispatchers.IO)
    private var oldPasswordFromDb: String = ""


    private var imageName: String = "";
    private var requestGalleryCode = 0
    private var requestCameraCode = 1
    private var imageUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserData()

        binding.tvRecipe.setOnClickListener {
            context?.startActivity(Intent(context, RecipeActivity::class.java))
        }
        binding.tvEditFullName.setOnClickListener {
            GlobalClass.globalToast(view?.context!!, "You Can't Update FullName")
        }
        binding.tvEditUserName.setOnClickListener {
            binding.etProfileUsername.isEnabled = true
        }
        binding.tvEditEmail.setOnClickListener {
            binding.etProfileEmail.isEnabled = true
        }
        binding.btnUpdate.setOnClickListener {
            updateUserProfile()
        }
        binding.btnUpdatePassword.setOnClickListener {
            if (validatePasswordFields()) {
                updatePassword()
            }
        }
        binding.profileImage.setOnClickListener {
            loadPopUpMenu()
        }


    }

    private fun validatePasswordFields(): Boolean {
        var flag = true
        if (TextUtils.isEmpty(binding.etOldPassword.text.toString())) {
            binding.etOldPassword.error = "Required"
            binding.etOldPassword.requestFocus()
            flag = false
        } else if (TextUtils.isEmpty(binding.etNewPassword.text.toString())) {
            binding.etNewPassword.error = "Required"
            binding.etNewPassword.requestFocus()
            flag = false
        }
        return flag
    }


    private fun fetchUserData() {
        activityScope.launch {
            try {
                val getRepo = UserRepository()
                val getResponse = getRepo.profile(LoginActivity.UserId)
                if (getResponse.success == true) {
                    val presentUser = getResponse.data
                    oldPasswordFromDb = presentUser!!.password.toString()
                    if (presentUser != null) {
                        withContext(Main) {
                            loadUserDataToForm(presentUser)

                        }
                    }
                }
            } catch (ex: IOException) {
                withContext(Main) {
                    GlobalClass.globalToast(context, "${ex.localizedMessage}")
                }
            }
        }
    }

    private fun loadUserDataToForm(user: User) {
        binding.etProfileName.setText(user.firstName + " " + user.lastName)
        binding.etProfileEmail.setText(user.email)
        binding.etProfileUsername.setText(user.username)
        setGender(user)
        val photoURI = ServiceBuilder.loadFilePath() + user.photo
        activity?.let {
            Glide
                .with(it)
                .load(photoURI)
                .into(binding.profileImage)
        }

    }

    private fun setGender(user: User) {
        val rdoMale = binding.rdoMale
        val rdoFemale = binding.rdoFemale
        val rdoOthers = binding.rdoOther
        when (user.gender) {
            "Male" -> rdoMale.isChecked = true
            "Female" -> rdoFemale.isChecked = true
            "Others" -> rdoOthers.isChecked = true
        }
    }


    private fun updateUserProfile(username: String? = null, email: String? = null) {
        val username = binding.etProfileUsername.text.toString()
        val email = binding.etProfileEmail.text.toString()
        val userGender = getGender()
        //Update Properties ... Must See this portion in future
        val user =
            User(username = username, email = email, gender = userGender)
        activityScope.launch {
            val getRepo = UserRepository()
            val getResponse = getRepo.updateMe(user, LoginActivity.UserId)
            if (getResponse.success == true) {
                withContext(Main) {
                    dynamicToast("Profile Updated")
                }
            }
        }
    }

    private fun getGender(): String {
        var gender: String = ""
        when {
            binding.rdoMale.isChecked -> gender = "Male"
            binding.rdoFemale.isChecked -> gender = "Female"
            binding.rdoOther.isChecked -> gender = "Others"
        }
        return gender
    }

    private fun dynamicToast(strMessage: String) {
        GlobalClass.globalToast(context, "Profile Updated")
    }


    private fun updatePassword() {

        val newPassword = binding.etNewPassword.text.toString()
        val user = User(password = newPassword)

        activityScope.launch {
            try {
                val getRepo = UserRepository()
                val getResponse = getRepo.changePassword(LoginActivity.UserId, user)
                if (getResponse.success == true) {
                    withContext(Main) {
                        GlobalClass.globalToast(context, "Password Updated Successfully")
                    }
                }
            } catch (ex: IOException) {
                withContext(Main) {
                    GlobalClass.globalToast(context, "Error Updating Password")
                }
            }
        }

    }


    private fun updateImage(id: String) {
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
                                view?.context!!,
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
                            view?.context!!,
                            ex.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(view?.context!!, binding.profileImage)
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
                val contentResolver = view?.context!!.contentResolver
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
            updateImage(LoginActivity.UserId)
        }
    }

    private fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? {
        var file: File? = null
        return try {
            file = File(
                view?.context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

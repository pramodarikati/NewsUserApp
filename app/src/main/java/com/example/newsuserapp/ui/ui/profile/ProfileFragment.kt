package com.example.newsuserapp.ui.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsuserapp.R
import com.example.newsuserapp.databinding.FragmentProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val GALLERY_PERMISSION_REQUEST_CODE = 102
    private val CAMERA_REQUEST_CODE = 103
    private val GALLERY_REQUEST_CODE = 104

    private val user = FirebaseAuth.getInstance().currentUser
    private val sharedPref by lazy { requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        loadProfileData()

        binding.imageProfile.setOnClickListener {
//            chooseImageSource()
        }
        binding.btnChangePhoto.setOnClickListener {
            chooseImageSource()
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val locationName = "${address.locality}, ${address.adminArea}, ${address.countryName}"
                        binding.location.text = locationName
                    } else {
                        binding.location.text = "Location not found"
                    }
                } catch (e: IOException) {
                    binding.location.text = "Error getting location"
                    e.printStackTrace()
                }
            } else {
                binding.location.text = "Location not available"
            }
        }.addOnFailureListener {
            binding.location.text = "Failed to get location"
        }
    }



    private fun loadProfileData() {
        val imageUri = sharedPref.getString("profile_image", null)

        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .circleCrop()
                .into(binding.imageProfile)
        } else {
            val photoUrl = user?.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(binding.imageProfile)
            } else {
                Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.imageProfile)
            }
        }

        binding.textName.text = user?.displayName ?: "No Name"
        binding.textEmail.text = user?.email ?: "No Email"
    }


    private fun chooseImageSource() {
        val options = arrayOf("Camera", "Gallery")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image Source")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        if (hasCameraPermissions()) {
                            openCamera()
                        } else {
                            EasyPermissions.requestPermissions(
                                this,
                                "Camera permission is needed to take pictures",
                                CAMERA_PERMISSION_REQUEST_CODE,
                                android.Manifest.permission.CAMERA
                            )
                        }
                    }
                    1 -> pickImageGallery.launch("image/*")

                }
            }
            .create()
            .show()
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private val pickImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            saveImageUri(uri)
            loadProfileImage(uri)
        }
    }

    private fun loadProfileImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.imageProfile)
    }
    private fun hasCameraPermissions(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.CAMERA)
    }

    private fun hasGalleryPermissions(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        if (hasGalleryPermissions()) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Storage permission is needed to select images",
                GALLERY_PERMISSION_REQUEST_CODE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            GALLERY_PERMISSION_REQUEST_CODE -> openGallery()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        saveProfilePicture(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        saveProfilePicture(it)
                    }
                }
            }
        }
    }


    private fun saveProfilePicture(imageBitmap: Bitmap) {
        val tempUri = saveBitmapToFile(imageBitmap)
        tempUri?.let {
            saveImageUri(it)
            loadProfileImage(it)
        }
    }


    private fun saveProfilePicture(imageUri: Uri) {
        saveImageUri(imageUri)
        loadProfileData()
    }

    private fun saveImageUri(uri: Uri) {
        sharedPref.edit().putString("profile_image", uri.toString()).apply()
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        try {
            val file = File(requireContext().cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            return Uri.fromFile(file)
        } catch (e: IOException) {
            Log.e("ProfileFragment", "Error saving image: ${e.message}")
            return null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

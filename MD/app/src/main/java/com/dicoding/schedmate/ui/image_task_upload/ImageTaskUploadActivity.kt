package com.dicoding.schedmate.ui.image_task_upload

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.ActivityImageTaskUploadBinding
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity.Companion
import com.dicoding.schedmate.ui.detail_task.DetailTaskViewModel
import com.dicoding.schedmate.ui.detail_task.DetailTaskViewModelFactory
import getImageUri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import reduceFileImage
import uriToFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ImageTaskUploadActivity : AppCompatActivity() {

    companion object {
        const val ID_TASK = "id_task"
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA

    }

    private lateinit var binding: ActivityImageTaskUploadBinding
    private lateinit var viewModel: DetailTaskViewModel
    private lateinit var sessionManager: SessionManager

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityImageTaskUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val idTask = intent.getIntExtra(ID_TASK, -1)

        val userPreference = UserPreference.getInstance(dataStore)
        val factory = DetailTaskViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

//        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.ivBackButton.setOnClickListener { finish() }
        binding.btnSend.setOnClickListener {
            val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)

            val imageFile = currentImageUri?.let { it1 -> uriToFile(it1, this).reduceFileImage() }
            val requestImageFile = imageFile?.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = requestImageFile?.let { it1 ->
                MultipartBody.Part.createFormData(
                    "upload_file",
                    imageFile.name,
                    it1
                )
            }
            viewModel.updateTask(taskId = idTask, progressStatus = "2", endTime = formatted, file = multipartBody)
        }

        viewModel.updateTaskRes.observe(this) { updateTaskRes ->
            if (updateTaskRes != null) {
                Toast.makeText(this, updateTaskRes.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.isLoading.observe(this) {isLoading ->
            binding.btnSend.text = if (isLoading) "Loading..." else "Kirim"
        }

    }



    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivEvidence.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }
}
package com.photoeditor.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoeditor.app.filter.FilterListener
import com.photoeditor.app.filter.FilterViewAdapter
import com.photoeditor.app.weather.WeatherApi
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PhotoEditorMain : AppCompatActivity(), FilterListener {

    private lateinit var uri: Uri
    private lateinit var mPhotoEditorView: PhotoEditorView

    private var mPhotoEditor: PhotoEditor? = null
    private var recyclerView: RecyclerView? = null
    private var cameraImageButton: ImageButton? = null
    private var galleryImageButton: ImageButton? = null
    private var saveImageButton: ImageButton? = null
    private lateinit var addWeatherButton: ImageButton

    private val mFilterViewAdapter = FilterViewAdapter(this)
    private val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    private val path = Environment.getExternalStorageDirectory().absolutePath + "/PhotoEditor"
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.

            } else {
                showToast("Для хранения изображений терубется разрешение на запись")
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoeditor)
        initView()
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView).build()
    }

    private fun requireContext(): Context {
        return this.applicationContext
    }

    private fun initView() {
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mPhotoEditorView.source.setImageResource(R.drawable.sample_image)

        recyclerView = findViewById(R.id.recyclerViewFilters)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = llmFilters
        recyclerView?.adapter = mFilterViewAdapter

        cameraImageButton = findViewById(R.id.cameraImageButton)
        cameraImageButton?.setOnClickListener {
            val photoFile = File.createTempFile(
                "IMG_",
                ".jpg",
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )

            getPicture.launch(uri)
        }

        galleryImageButton = findViewById(R.id.galleryImageButton)
        galleryImageButton?.setOnClickListener {
            getContent.launch("image/*")
        }

        saveImageButton = findViewById(R.id.saveImageButton)
        saveImageButton?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()

            val filePath: String =
                path + "/IMG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
            createDirIfExists()

            mPhotoEditor?.saveAsFile(
                filePath,
                saveSettings,
                object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        showToast("Сохранено: ${filePath}")
                    }

                    override fun onFailure(exception: Exception) {
                        showToast("Ошибка сохранения")
                    }
                })

        }
        addWeatherButton = findViewById(R.id.addWeatherImageButton)
        addWeatherButton.setOnClickListener {
            WeatherApi(this).getWeather(mPhotoEditor)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter?) {
        mPhotoEditor?.setFilterEffect(photoFilter)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            mPhotoEditorView.source.setImageURI(uri)
        }
    private val getPicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
            if (isSaved) {
                mPhotoEditorView.source.setImageURI(uri)
            }
        }

    private fun createDirIfExists() {
        val dir = File(Environment.getExternalStorageDirectory().absolutePath + "/PhotoEditor")
        if (!dir.exists()) dir.mkdir()
    }

}
package com.molecalendar

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddMoleActivity : AppCompatActivity() {

    private lateinit var moleViewModel: MoleViewModel
    private lateinit var moleImageView: ImageView
    private lateinit var takePhotoButton: Button
    private lateinit var locationEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var sizeEditText: TextInputEditText
    private lateinit var colorEditText: TextInputEditText
    private lateinit var selectDateButton: Button
    private lateinit var selectedDateText: TextView
    private lateinit var reminderCheckBox: CheckBox
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private var currentPhotoPath: String? = null
    private var nextCheckDateMillis: Long = 0
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_IMAGE_CAPTURE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mole)

        // Initialize views
        moleImageView = findViewById(R.id.moleImageView)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        locationEditText = findViewById(R.id.locationEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        sizeEditText = findViewById(R.id.sizeEditText)
        colorEditText = findViewById(R.id.colorEditText)
        selectDateButton = findViewById(R.id.selectDateButton)
        selectedDateText = findViewById(R.id.selectedDateText)
        reminderCheckBox = findViewById(R.id.reminderCheckBox)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        moleViewModel = ViewModelProvider(this)[MoleViewModel::class.java]

        // Set default next check date to 3 months from now
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 3)
        nextCheckDateMillis = calendar.timeInMillis
        selectedDateText.text = "Selected: ${dateFormat.format(Date(nextCheckDateMillis))}"

        setupListeners()
    }

    private fun setupListeners() {
        takePhotoButton.setOnClickListener {
            checkCameraPermission()
        }

        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        saveButton.setOnClickListener {
            saveMole()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.molecalendar.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "MOLE_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = nextCheckDateMillis

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                nextCheckDateMillis = selectedCalendar.timeInMillis
                selectedDateText.text = "Selected: ${dateFormat.format(Date(nextCheckDateMillis))}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun saveMole() {
        val location = locationEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val sizeText = sizeEditText.text.toString().trim()
        val color = colorEditText.text.toString().trim()

        if (location.isEmpty()) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
            return
        }

        if (sizeText.isEmpty()) {
            Toast.makeText(this, "Please enter size", Toast.LENGTH_SHORT).show()
            return
        }

        val size = sizeText.toFloatOrNull()
        if (size == null || size <= 0) {
            Toast.makeText(this, "Please enter valid size", Toast.LENGTH_SHORT).show()
            return
        }

        if (color.isEmpty()) {
            Toast.makeText(this, "Please enter color", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTime = System.currentTimeMillis()
        val mole = Mole(
            location = location,
            description = description,
            size = size,
            color = color,
            addedDate = currentTime,
            lastCheckedDate = currentTime,
            nextCheckDate = nextCheckDateMillis,
            photoPath = currentPhotoPath,
            reminderEnabled = reminderCheckBox.isChecked
        )

        moleViewModel.insert(mole) {
            Toast.makeText(this, "Mole saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotoPath?.let { path ->
                Glide.with(this)
                    .load(path)
                    .centerCrop()
                    .into(moleImageView)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

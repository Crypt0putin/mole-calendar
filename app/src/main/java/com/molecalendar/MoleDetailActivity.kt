package com.molecalendar

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MoleDetailActivity : AppCompatActivity() {

    private lateinit var moleViewModel: MoleViewModel
    private lateinit var moleImageView: ImageView
    private lateinit var locationTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var sizeTextView: TextView
    private lateinit var colorTextView: TextView
    private lateinit var addedDateTextView: TextView
    private lateinit var lastCheckedTextView: TextView
    private lateinit var nextCheckDateTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    private var moleId: Long = -1
    private var currentMole: Mole? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mole_detail)

        // Get mole ID from intent
        moleId = intent.getLongExtra("MOLE_ID", -1)
        if (moleId == -1L) {
            Toast.makeText(this, "Error loading mole details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        moleImageView = findViewById(R.id.moleImageView)
        locationTextView = findViewById(R.id.locationTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        sizeTextView = findViewById(R.id.sizeTextView)
        colorTextView = findViewById(R.id.colorTextView)
        addedDateTextView = findViewById(R.id.addedDateTextView)
        lastCheckedTextView = findViewById(R.id.lastCheckedTextView)
        nextCheckDateTextView = findViewById(R.id.nextCheckDateTextView)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        moleViewModel = ViewModelProvider(this)[MoleViewModel::class.java]

        // Observe mole data
        moleViewModel.getMoleById(moleId).observe(this) { mole ->
            mole?.let {
                currentMole = it
                displayMoleDetails(it)
            }
        }

        setupListeners()
    }

    private fun displayMoleDetails(mole: Mole) {
        locationTextView.text = mole.location
        descriptionTextView.text = mole.description
        sizeTextView.text = "${mole.size} mm"
        colorTextView.text = mole.color
        addedDateTextView.text = dateFormat.format(Date(mole.addedDate))
        lastCheckedTextView.text = dateFormat.format(Date(mole.lastCheckedDate))
        nextCheckDateTextView.text = dateFormat.format(Date(mole.nextCheckDate))

        // Load image if available
        if (mole.photoPath != null && File(mole.photoPath).exists()) {
            Glide.with(this)
                .load(mole.photoPath)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_camera)
                .into(moleImageView)
        } else {
            moleImageView.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    private fun setupListeners() {
        editButton.setOnClickListener {
            // TODO: Implement edit functionality
            Toast.makeText(this, "Edit functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteMole()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteMole() {
        currentMole?.let { mole ->
            // Delete photo file if exists
            mole.photoPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }

            // Delete from database
            moleViewModel.delete(mole)
            Toast.makeText(this, "Mole deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

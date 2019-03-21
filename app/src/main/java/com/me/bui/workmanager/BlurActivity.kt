package com.me.bui.workmanager

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import com.bumptech.glide.Glide


class BlurActivity : AppCompatActivity() {

    private lateinit var viewModel: BlurViewModel
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var goButton: Button
    private lateinit var outputButton: Button
    private lateinit var cancelButton: Button
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)
        bindResources()

        // Get the ViewModel
        viewModel = ViewModelProviders.of(this).get(BlurViewModel::class.java)

        // Image uri should be stored in the ViewModel; put it there then display
        val imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI)
        viewModel.setImageUri(imageUriExtra)
        viewModel.imageUri?.let { imageUri ->
            Glide.with(this).load(imageUri).into(imageView)
        }

        // Setup blur image file button
        setOnClickListeners()

    }

    private fun bindResources() {
        imageView = findViewById(R.id.image_view)
        progressBar = findViewById(R.id.progress_bar)
        goButton = findViewById(R.id.go_button)
        outputButton = findViewById(R.id.see_file_button)
        cancelButton = findViewById(R.id.cancel_button)
        radioGroup = findViewById(R.id.radio_blur_group)
    }

    private fun setOnClickListeners() {
        goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        progressBar.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        goButton.visibility = View.GONE
        outputButton.visibility = View.GONE
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        progressBar.visibility = View.GONE
        cancelButton.visibility = View.GONE
        goButton.visibility = View.VISIBLE
    }

    private val blurLevel: Int
        get() =
            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
}
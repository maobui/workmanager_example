package com.me.bui.workmanager

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import androidx.work.WorkInfo
import android.content.Intent






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

        // Show work status, added in onCreate()
        viewModel.savedWorkInfo.observe(this, workInfosObserver())

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

        outputButton.setOnClickListener { view ->
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        // Hookup the Cancel button
        cancelButton.setOnClickListener { view -> viewModel.cancelWork() }
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

    private fun workInfosObserver() : Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfos ->

            // If there are no matching work info, do nothing
            if (listOfWorkInfos.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the first output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfos[0]

            val finished = workInfo.getState().isFinished()
            if (!finished) {
                showWorkInProgress()
            } else {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // If there is an output file show "See File" button
                if (!outputImageUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputImageUri as String)
                    outputButton.visibility = View.VISIBLE
                }
            }
        }
    }
}
package com.me.bui.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.me.bui.workmanager.KEY_IMAGE_URI


/**
 * Created by mao.bui on 3/21/2019.
 */
class BlurWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val TAG = BlurWorker::class.java.simpleName
    override fun doWork(): Result {
        val applicationContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        try {

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = applicationContext.contentResolver
            // Create a bitmap
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            // Blur the bitmap
            val output = blurBitmap(picture, applicationContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(applicationContext, output)

            makeStatusNotification("Output is $outputUri", applicationContext)

            val outputData = Data.Builder()
                .putString(KEY_IMAGE_URI, outputUri.toString())
                .build()
            // If there were no errors, return SUCCESS
            return ListenableWorker.Result.success(outputData)
        } catch (throwable: Throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error applying blur", throwable)
            return ListenableWorker.Result.failure()
        }
    }
}
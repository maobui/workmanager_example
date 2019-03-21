package com.me.bui.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.me.bui.workmanager.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mao.bui on 3/21/2019.
 */
class SaveImageToFileWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val TAG = SaveImageToFileWorker::class.java.simpleName

    private val TITLE = "Blurred Image"
    private val DATE_FORMATTER = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())
    override fun doWork(): Result {
        val applicationContext = applicationContext

        val resolver = applicationContext.getContentResolver()
        try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))
            val outputUri = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, TITLE, DATE_FORMATTER.format(Date()))
            if (TextUtils.isEmpty(outputUri)) {
                Log.e(TAG, "Writing to MediaStore failed");
                return Result.failure()
            }
            // TODO create and set the output Data object with the imageUri.
            return Result.success();
        } catch (exception : Exception) {
            Log.e(TAG, "Unable to save image to Gallery", exception)
            return Result.failure();
        }
    }
}
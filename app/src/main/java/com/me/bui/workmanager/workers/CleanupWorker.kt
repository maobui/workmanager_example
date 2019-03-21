package com.me.bui.workmanager.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.me.bui.workmanager.OUTPUT_PATH
import java.io.File


/**
 * Created by mao.bui on 3/21/2019.
 */
class CleanupWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val TAG = CleanupWorker::class.java.simpleName
    override fun doWork(): Result {
        val applicationContext = applicationContext
        try {
            val outputDirectory = File(
                applicationContext.filesDir, OUTPUT_PATH
            )
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries!!.size > 0) {
                    for (entry in entries!!) {
                        val name = entry.getName()
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.d(TAG, String.format("Deleted %s - %s", name, deleted))
                        }
                    }
                }
            }

            return Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error cleaning up", exception)
            return Result.failure()
        }

    }
}
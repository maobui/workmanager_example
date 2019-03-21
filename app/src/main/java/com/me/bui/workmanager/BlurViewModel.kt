package com.me.bui.workmanager

import android.arch.lifecycle.ViewModel
import android.net.Uri


class BlurViewModel : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    /**
     * Setters
     */
    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}

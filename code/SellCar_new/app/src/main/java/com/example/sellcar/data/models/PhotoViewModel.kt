package com.example.sellcar.data.models

import android.net.Uri
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    var selectedImages: List<Uri> = emptyList()


    fun updateSelectedImages(uris: List<Uri>) {
        selectedImages = uris
    }

}
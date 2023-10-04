package com.rafaelsilva81.mobiledevufc.providers

import android.graphics.Bitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class Storage {
    companion object {
        fun getInstance(): FirebaseStorage {
            return Firebase.storage
        }

        fun getReference(): StorageReference {
            return getInstance().reference
        }

        fun getImageData(bitmap: Bitmap): ByteArray {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            return data
        }

        fun getRandomImageName(): String {
            return "${UUID.randomUUID()}.png"
        }
    }
}
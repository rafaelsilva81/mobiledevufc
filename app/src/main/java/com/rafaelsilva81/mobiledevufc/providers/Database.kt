package com.rafaelsilva81.mobiledevufc.providers

import com.google.firebase.firestore.FirebaseFirestore

class Database {
    companion object {
        fun getInstance(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }
}
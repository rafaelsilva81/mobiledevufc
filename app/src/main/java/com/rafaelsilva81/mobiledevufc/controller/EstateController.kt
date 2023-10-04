package com.rafaelsilva81.mobiledevufc.controller

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rafaelsilva81.mobiledevufc.model.EstateModel
import com.rafaelsilva81.mobiledevufc.providers.Database
import kotlinx.coroutines.tasks.await

// Enum para tipo de negocio
enum class BusinessType {
    RENT,
    SALE
}

class EstateController {

    companion object {
        fun list(
            onSuccess: ((List<EstateModel>) -> Unit)? = null,
            onError: (() -> Unit)? = null,
            onComplete: (() -> Unit)? = null,
            orderPriceDirection: Query.Direction? = null,
            businessType: BusinessType? = null,
            showUnavailable: Boolean = false,
        ) {

            val db = Database.getInstance()
            val housesRef = db.collection("houses")
            var query: Query = housesRef

            if (!showUnavailable) {
                // vai mostrar só os disponiveis
                query = query.whereEqualTo("available", true)
            }

            // por padrao ambos estao true entao mostra tudo
            when (businessType) {
                null -> {}
                BusinessType.RENT -> {
                    query = query.whereEqualTo("rent", true)
                }

                BusinessType.SALE -> {
                    query = query.whereEqualTo("rent", false)
                }
            }

            if (orderPriceDirection != null) {
                query = query.orderBy("price", orderPriceDirection)
            }


            query.get().addOnSuccessListener { result ->
                val houses = mutableListOf<EstateModel>()
                for (document in result) {
                    val house = document.toObject(EstateModel::class.java)
                    house.setId(document.id)
                    houses.add(house)
                    //println("${document.id} => ${document.data}")
                }
                onSuccess?.invoke(houses)
            }.addOnFailureListener {
                onError?.invoke()
            }.addOnCompleteListener {
                onComplete?.invoke()
            }
        }

        fun add(
            estate: EstateModel,
            onSuccess: (() -> Unit)? = null,
            onError: (() -> Unit)? = null,
            onComplete: (() -> Unit)? = null
        ) {
            val db = Database.getInstance()
            val estateRef = db.collection("houses").document()

            estate.setId(estateRef.id)
            estateRef.set(
                estate,
                SetOptions.mergeFields(
                    "address",
                    "description",
                    "image",
                    "area",
                    "price",
                    "rent",
                    "available",
                )
            ).addOnSuccessListener {
                onSuccess?.invoke()
            }.addOnFailureListener {
                onError?.invoke()
            }.addOnCompleteListener {
                onComplete?.invoke()
            }
        }

        suspend fun get(
            id: String,
        ): EstateModel? {
            val db = Database.getInstance()
            val estateRef = db.collection("houses").document(id)
            val estateDocSnapshot = estateRef.get().await()

            return estateDocSnapshot.toObject(EstateModel::class.java)
        }

        fun delete(
            id: String,
            onSuccess: (() -> Unit)? = null,
            onError: (() -> Unit)? = null,
            onComplete: (() -> Unit)? = null
        ) {
            val db = Database.getInstance()
            val estateRef = db.collection("houses").document(id)

            estateRef.delete().addOnSuccessListener {
                onSuccess?.invoke()
            }.addOnFailureListener {
                onError?.invoke()
            }.addOnCompleteListener {
                onComplete?.invoke()
            }
        }

        fun edit(
            id: String,
            estate: EstateModel,
            onSuccess: (() -> Unit)? = null,
            onError: (() -> Unit)? = null,
            onComplete: (() -> Unit)? = null
        ) {
            val db = Database.getInstance()
            val estateRef = db.collection("houses").document(id)

            estateRef.set(
                estate,
                SetOptions.mergeFields(
                    "address",
                    "description",
                    "image",
                    "area",
                    "price",
                    "rent",
                    "available",
                )
            ).addOnSuccessListener {
                onSuccess?.invoke()
            }.addOnFailureListener {
                onError?.invoke()
            }.addOnCompleteListener {
                onComplete?.invoke()
            }
        }
    }
}
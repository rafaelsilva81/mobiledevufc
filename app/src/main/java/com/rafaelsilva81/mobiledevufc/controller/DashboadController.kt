package com.rafaelsilva81.mobiledevufc.controller

import com.google.firebase.firestore.AggregateSource
import com.rafaelsilva81.mobiledevufc.providers.Database
import kotlinx.coroutines.tasks.await

class DashboadController {
    companion object {
        suspend fun countTotalEstates(): Long {
            val snapshot = Database.getInstance().collection("houses")
                .count()
                .get(AggregateSource.SERVER)
                .await()

            return snapshot.count
        }

        suspend fun countTotalEstatesForRent(): Long {
            val snapshot = Database.getInstance().collection("houses")
                .whereEqualTo("rent", true)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            return snapshot.count
        }

        suspend fun countTotalEstatesForSale(): Long {
            val snapshot = Database.getInstance().collection("houses")
                .whereEqualTo("rent", false)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            return snapshot.count
        }

        suspend fun countTotalAvailableEstates(): Long {
            val snapshot = Database.getInstance().collection("houses")
                .whereEqualTo("available", true)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            return snapshot.count
        }

        suspend fun countTotalUnavailableEstates(): Long {
            val snapshot = Database.getInstance().collection("houses")
                .whereEqualTo("available", false)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            return snapshot.count
        }

    }
}
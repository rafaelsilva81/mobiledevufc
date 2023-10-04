package com.rafaelsilva81.mobiledevufc.helpers

import android.app.AlertDialog
import android.content.Context

class DialogHelper {
    companion object {
        fun showAlert(context: Context, title: String, message: String) {
            val alertDialogBuilder = AlertDialog.Builder(context)

            alertDialogBuilder.apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        fun getDialogBuilder(
            context: Context,
            title: String,
            message: String
        ): AlertDialog.Builder {
            val alertDialogBuilder = AlertDialog.Builder(context)

            alertDialogBuilder.apply {
                setTitle(title)
                setMessage(message)
            }

            return alertDialogBuilder
        }
    }
}
package com.example.petside

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class AlertFragment(private var message: String, private val onCancel: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (message.isEmpty()) {
            message = "Something went wrong, try again later"
        }
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("ERROR")
                .setMessage(message)
                .setPositiveButton("ОК") {
                        dialog, id ->
                    onCancel()
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
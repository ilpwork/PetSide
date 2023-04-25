package com.example.petside

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class AlertFragment(private var message: String, private val onCancel: () -> Unit) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (message.isEmpty()) {
            message = resources.getString(R.string.default_error)
        }
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(resources.getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, id ->
                    onCancel()
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
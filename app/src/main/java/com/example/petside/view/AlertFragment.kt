package com.example.petside.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.petside.R


class AlertFragment(private var message: String?, private val onCancel: () -> Unit) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (message === null || message!!.isEmpty()) {
            message = resources.getString(R.string.default_error)
        }
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(resources.getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                    onCancel()
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
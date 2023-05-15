package com.example.petside.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.petside.R


class AlertFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: AlertFragmentArgs by navArgs()
        var message = args.message
        if (message.isEmpty()) {
            message = resources.getString(R.string.default_error)
        }
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(resources.getString(R.string.error)).setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
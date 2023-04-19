package com.example.petside

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.example.petside.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.buttonNext.isEnabled = false

        binding.emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = binding.emailInput.text
                if (email === null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) {
                    binding.emailInputBox.error = "Incorrect E-mail"
                    binding.buttonNext.isEnabled = false
                }
            } else {
                binding.emailInputBox.error = null
                val description = binding.descriptionInput.text
                binding.buttonNext.isEnabled = description !== null && !description.isEmpty()
            }
        }

        binding.descriptionInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val description = binding.descriptionInput.text
                if (description === null || description.isEmpty()) {
                    binding.descriptionInputBox.error = "Description must not be empty"
                    binding.buttonNext.isEnabled = false
                }
            } else {
                binding.descriptionInputBox.error = null
                val email = binding.emailInput.text
                binding.buttonNext.isEnabled = email !== null && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            }
        }

        setContentView(binding.root)
    }


    fun onNextPress(view: View) {


    }
}
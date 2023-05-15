package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.petside.app.App
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getUser()

        setListeners()
        setObservers()

        return view
    }

    private fun setListeners() {
        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            viewModel.updateEmail(text.toString())
        }

        binding.descriptionInput.doOnTextChanged { text, _, _, _ ->
            viewModel.updateDescription(text.toString())
        }

        binding.buttonNext.setOnClickListener {
            viewModel.auth()
        }

        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
        }
    }

    private fun setObservers() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.emailInput.setText(it.email)
                binding.descriptionInput.setText(it.description)
                viewModel.newUser = it
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.buttonNext.isEnabled = uiState.buttonEnabled

                    if (uiState.isLoading) {
                        binding.buttonContainer.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.buttonContainer.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }

                    if (uiState.isUserLoggedIn) {
                        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
                    }

                    if (uiState.errorMessage !== null) {
                        findNavController().navigate(
                            AuthFragmentDirections.actionAuthFragmentToAlertFragment(
                                message = uiState.errorMessage
                            )
                        )
                        viewModel.clearError()
                    }
                }
            }
        }
    }

}
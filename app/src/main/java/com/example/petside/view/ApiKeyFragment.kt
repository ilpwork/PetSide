package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.petside.app.App
import com.example.petside.databinding.FragmentApiKeyBinding
import com.example.petside.viewmodel.ApiKeyViewModel
import kotlinx.coroutines.launch

class ApiKeyFragment : Fragment() {
    private lateinit var binding: FragmentApiKeyBinding

    private val viewModel: ApiKeyViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App)
            .appComponent
            .inject(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApiKeyBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.buttonNext.isEnabled = false

        viewModel.getUser()

        setListeners()
        setObservers()

        return view
    }

    private fun setObservers() {

        viewModel.user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.apiKeyInput.setText(it.api_key)
                viewModel.newUser = it
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {uiState ->
                    binding.buttonNext.isEnabled = uiState.buttonEnabled

                    if (uiState.isLoading) {
                        binding.buttonNext.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.buttonNext.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }

                    if (uiState.isUserLoggedIn) {
                        findNavController().navigate(ApiKeyFragmentDirections.actionApiKeyFragmentToTabBarFragment())
                    }

                    if (uiState.errorMessage !== null) {
                        val dialog = AlertFragment(uiState.errorMessage)
                        dialog.show(parentFragmentManager, "ApiKeyError")
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.apiKeyInput.doOnTextChanged { text, _, _, _ ->
            viewModel.updateKey(text.toString())
        }

        binding.buttonNext.setOnClickListener {
            viewModel.checkKey()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}
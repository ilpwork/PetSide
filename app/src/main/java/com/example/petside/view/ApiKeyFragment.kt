package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.petside.app.App
import com.example.petside.databinding.FragmentApiKeyBinding
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.RetrofitService
import com.example.petside.viewmodel.ApiKeyViewModel
import retrofit2.HttpException
import javax.inject.Inject

class ApiKeyFragment : Fragment() {
    private lateinit var binding: FragmentApiKeyBinding

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var user: LiveData<UserEntity>

    @Inject
    lateinit var dao: Dao

    private val viewModel: ApiKeyViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App)
            .appComponent
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApiKeyBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.buttonNext.isEnabled = false

        viewModel.retrofitService = retrofitService
        viewModel.dao = dao

        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.apiKeyInput.setText(it.api_key)
                viewModel.newUser = it
            }
        }

        setListeners()
        setObservers()

        return view
    }

    private fun setObservers() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.buttonNext.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.buttonNext.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setListeners() {
        binding.apiKeyInput.doOnTextChanged { text, _, _, _ ->
            binding.buttonNext.isEnabled = text != null && text.isNotEmpty()
        }

        binding.buttonNext.setOnClickListener {
            fun onSuccess() {
                findNavController().navigate(ApiKeyFragmentDirections.actionApiKeyFragmentToTabBarFragment())
            }

            fun onError(e: HttpException) {
                val dialog = AlertFragment(e.message())
                dialog.show(parentFragmentManager, "ApiKeyError")
            }

            viewModel.newUser.api_key = binding.apiKeyInput.text.toString()
            viewModel.checkKey(::onSuccess, ::onError)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}
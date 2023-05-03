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
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.RetrofitService
import com.example.petside.viewmodel.AuthViewModel
import retrofit2.HttpException
import javax.inject.Inject


class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    @Inject
    lateinit var user: LiveData<UserEntity>

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var dao: Dao

    private val viewModel: AuthViewModel by activityViewModels()

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
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.retrofitService = retrofitService
        viewModel.dao = dao

        setListeners()
        setObservers()

        return view
    }

    private fun setListeners() {
        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            viewModel.newUser.email = text.toString()
            viewModel.checkEmailAndDescription()
        }

        binding.descriptionInput.doOnTextChanged { text, _, _, _ ->
            viewModel.newUser.description = text.toString()
            viewModel.checkEmailAndDescription()
        }

        binding.buttonNext.setOnClickListener {
            auth()
        }

        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
        }
    }

    private fun setObservers() {
        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.emailInput.setText(it.email)
                binding.descriptionInput.setText(it.description)
                viewModel.newUser = it
            }
        }

        viewModel.buttonEnabled.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.buttonNext.isEnabled = it
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.buttonContainer.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.buttonContainer.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun auth() {
        fun onSuccess() {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
        }

        fun onError(e: HttpException) {
            val dialog = AlertFragment(e.message())
            dialog.show(parentFragmentManager, "AuthError")
        }

        viewModel.auth(::onSuccess, ::onError)
    }

}
package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.petside.app.App
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.AuthRequest
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        binding.buttonNext.isEnabled = false

        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.emailInput.setText(it.email)
                binding.descriptionInput.setText(it.description)
            }
        }

        binding.emailInput.doOnTextChanged { _, _, _, _ ->
            checkEmailAndDescription()
        }

        binding.descriptionInput.doOnTextChanged { _, _, _, _ ->
            checkEmailAndDescription()
        }

        binding.buttonNext.setOnClickListener {
            binding.buttonContainer.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            auth()
        }

        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
        }

        return view
    }

    private fun endLoading() {
        binding.buttonContainer.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        endLoading()
    }

    private fun checkEmailAndDescription() {
        val description = binding.descriptionInput.text
        val email = binding.emailInput.text
        binding.buttonNext.isEnabled =
            email !== null && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() && description !== null && description.isNotEmpty()
    }

    private fun auth() {
        val user = UserEntity(
            email = binding.emailInput.text.toString(),
            description = binding.descriptionInput.text.toString(),
            api_key = ""
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                retrofitService.auth(
                    AuthRequest(
                        user.email,
                        user.description
                    )
                )
                dao.insertUser(user)
            } catch (e: HttpException) {
                throw CancellationException(e.message())
            }
        }.invokeOnCompletion {
            if (it !== null) {
                val dialog = AlertFragment(it.message, ::endLoading)
                dialog.show(parentFragmentManager, "AuthError")
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
                }
            }
        }
    }

}
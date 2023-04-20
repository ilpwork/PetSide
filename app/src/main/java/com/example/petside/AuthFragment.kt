package com.example.petside

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.retrofit.AuthRequest
import com.example.petside.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.buttonNext.isEnabled = false

        binding.emailInput.doOnTextChanged { text, start, before, count ->
            checkEmailAndDescription()
        }

        binding.descriptionInput.doOnTextChanged { text, start, before, count ->
            checkEmailAndDescription()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mainApi = retrofit.create(MainApi::class.java)

        binding.buttonNext.setOnClickListener {
            binding.buttonNext.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    mainApi.auth(
                        AuthRequest(
                            binding.emailInput.text.toString(),
                            binding.descriptionInput.text.toString()
                        )
                    )
                    findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
                } catch (e: HttpException) {
                    val dialog = AlertFragment(e.message(), ::endLoading)
                    dialog.show(parentFragmentManager, "AuthError")
                }
            }
        }

        return view
    }

    private fun endLoading() {
        binding.buttonNext.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        endLoading()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkEmailAndDescription() {
        val description = binding.descriptionInput.text
        val email = binding.emailInput.text
        binding.buttonNext.isEnabled = email !== null && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches() && description !== null && description.isNotEmpty()
    }

}
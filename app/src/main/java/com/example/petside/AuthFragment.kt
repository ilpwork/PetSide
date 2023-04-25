package com.example.petside

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.db.MainDb
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.AuthRequest
import com.example.petside.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.buttonNext.isEnabled = false

        val mHandler = Handler(Looper.getMainLooper())
        val mainDb: MainDb = MainDb.getMainDb(requireContext())
        Thread {
            val user: UserEntity? = mainDb.getDao().getUser()
            if (user !== null) {
                mHandler.post {
                    binding.emailInput.setText(user.email)
                    binding.descriptionInput.setText(user.description)
                }
            }
        }.start()


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
            binding.buttonContainer.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    mainApi.auth(
                        AuthRequest(
                            binding.emailInput.text.toString(),
                            binding.descriptionInput.text.toString()
                        )
                    )

                    Thread {
                        val user = UserEntity(
                            email = binding.emailInput.text.toString(),
                            description = binding.descriptionInput.text.toString(),
                            api_key = ""
                        )
                        mainDb.getDao().insertUser(user)
                    }.start()

                    findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToApiKeyFragment())
                } catch (e: HttpException) {
                    val dialog = AlertFragment(e.message(), ::endLoading)
                    dialog.show(parentFragmentManager, "AuthError")
                }
            }
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

}
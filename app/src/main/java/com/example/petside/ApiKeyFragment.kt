package com.example.petside

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petside.databinding.FragmentApiKeyBinding
import com.example.petside.db.MainDb
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyFragment : Fragment() {
    private lateinit var binding: FragmentApiKeyBinding
    private var user: UserEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentApiKeyBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.buttonNext.isEnabled = false


        val mHandler = Handler(Looper.getMainLooper())
        val mainDb: MainDb = MainDb.getMainDb(requireContext())
        Thread {
            user = mainDb.getDao().getUser()
            if (user !== null) {
                mHandler.post {
                    binding.apiKeyInput.setText(user!!.api_key)
                }
            }
        }.start()

        binding.apiKeyInput.doOnTextChanged { text, start, before, count ->
            binding.buttonNext.isEnabled = text != null && text.isNotEmpty()
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
                val key = binding.apiKeyInput.text.toString()
                try {
                    mainApi.getFavourites(key)

                    Thread {
                        val newUser = UserEntity(
                            email = "",
                            description = "",
                            api_key = binding.apiKeyInput.text.toString()
                        )
                        if (user !== null) {
                            newUser.email = user!!.email
                            newUser.description = user!!.description
                        }
                        mainDb.getDao().insertUser(newUser)
                    }.start()

                    findNavController().navigate(ApiKeyFragmentDirections.actionApiKeyFragmentToTabBarFragment())
                } catch (e: HttpException) {
                    val dialog = AlertFragment(e.message(), ::endLoading)
                    dialog.show(parentFragmentManager, "ApiKeyError")
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
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
}
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
import com.example.petside.databinding.FragmentApiKeyBinding
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val newUser = UserEntity(
        email = "",
        description = "",
        api_key = ""
    )

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

        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                binding.apiKeyInput.setText(it.api_key)
                newUser.email = it.email
                newUser.description = it.description
            }
        }


        binding.apiKeyInput.doOnTextChanged { text, _, _, _ ->
            binding.buttonNext.isEnabled = text != null && text.isNotEmpty()
        }

        binding.buttonNext.setOnClickListener {
            binding.buttonNext.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            checkKey()
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

    private fun checkKey() {
        val key = binding.apiKeyInput.text.toString()
        newUser.api_key = binding.apiKeyInput.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                retrofitService.getFavourites(key)
                dao.insertUser(newUser)
            } catch (e: HttpException) {
                throw CancellationException(e.message())
            }
        }.invokeOnCompletion {
            if (it !== null) {
                val dialog = AlertFragment(it.message, ::endLoading)
                dialog.show(parentFragmentManager, "ApiKeyError")
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(ApiKeyFragmentDirections.actionApiKeyFragmentToTabBarFragment())
                }
            }
        }
    }
}
package com.example.petside

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.petside.databinding.FragmentAuthBinding
import com.example.petside.databinding.FragmentTabBarBinding

class TabBarFragment : Fragment() {
    private var _binding: FragmentTabBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTabBarBinding.inflate(inflater, container, false)
        val view = binding.root

        val feedFragment=FeedFragment()
        val addPostFragment=FeedFragment()
        val favouritesFragment=FavouritesFragment()
        val menuFragment=FeedFragment()

        setCurrentFragment(feedFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.feedTab->setCurrentFragment(feedFragment)
                R.id.addPostTab->setCurrentFragment(addPostFragment)
                R.id.favouritesTab->setCurrentFragment(favouritesFragment)
                R.id.menuTab->setCurrentFragment(menuFragment)
            }
            true
        }
        return view
    }

    private fun setCurrentFragment(fragment:Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
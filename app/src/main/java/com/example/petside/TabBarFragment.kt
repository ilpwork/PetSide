package com.example.petside

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.petside.databinding.FragmentTabBarBinding

class TabBarFragment : Fragment() {
    private lateinit var binding: FragmentTabBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTabBarBinding.inflate(inflater, container, false)
        val view = binding.root

        val feedFragment = FeedFragment()
        val addPostFragment = FeedFragment()
        val favouritesFragment = FavouritesFragment()
        val menuFragment = FeedFragment()

        setCurrentFragment(feedFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.feedTab -> setCurrentFragment(feedFragment)
                R.id.addPostTab -> setCurrentFragment(addPostFragment)
                R.id.favouritesTab -> setCurrentFragment(favouritesFragment)
                R.id.menuTab -> setCurrentFragment(menuFragment)
            }
            true
        }
        return view
    }

    private fun setCurrentFragment(fragment: Fragment) =
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}
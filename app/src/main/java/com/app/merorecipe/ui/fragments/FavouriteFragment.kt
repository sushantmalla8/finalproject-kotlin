package com.app.merorecipe.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.merorecipe.R
import com.app.merorecipe.adapter.FavouriteAdapter
import com.app.merorecipe.adapter.RecipeHomeAdapter
import com.app.merorecipe.databinding.FragmentFavouriteBinding
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private var listOfRecipe = arrayListOf<Recipe>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(view?.context)
        loadRecipe()
        refreshRecycler()
    }

    private fun loadRecipe() {
        CoroutineScope(Dispatchers.IO).launch {
            val getRecipeRepo = RecipeRepository()
            val getResponse = getRecipeRepo.getFavRecipe(LoginActivity.Username, true)
            listOfRecipe = getResponse.data
            listOfRecipe.reverse()
            withContext(Dispatchers.Main) {
                val adapter = FavouriteAdapter(listOfRecipe, view?.context!!)
                binding.recyclerView.adapter = adapter
            }
        }
    }

    private fun refreshRecycler() {
        binding.swipeRefresh.setOnRefreshListener {
            listOfRecipe.clear()
            loadRecipe()
            binding.swipeRefresh.isRefreshing = false
        }
    }
}


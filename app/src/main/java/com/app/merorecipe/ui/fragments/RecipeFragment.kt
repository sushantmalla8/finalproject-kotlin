package com.app.merorecipe.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.merorecipe.R
import com.app.merorecipe.adapter.RecipeHomeAdapter
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.databinding.FragmentProfileBinding
import com.app.merorecipe.databinding.FragmentRecipeBinding
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private var activityScope = CoroutineScope(Dispatchers.IO)

    private var listOfRecipe = arrayListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(view?.context)
        loadRecipe()
        refreshRecycler()
    }

    private fun loadRecipe() {
        activityScope.launch {
            val getRecipeRepo = RecipeRepository()
            val getResponse = getRecipeRepo.getAllRecipe()
            listOfRecipe = getResponse.data
            listOfRecipe.reverse()
            withContext(Main) {
                val adapter = RecipeHomeAdapter(listOfRecipe, view?.context!!)
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
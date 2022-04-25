package com.app.merorecipe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.merorecipe.adapter.MyRecipeAdapter
import com.app.merorecipe.databinding.ActivityRecipeBinding
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException

class RecipeActivity : AppCompatActivity() {

    private var _binding: ActivityRecipeBinding? = null
    private val binding get() = _binding!!
    private var arrListRecipe = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecipeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnAddRecipe.setOnClickListener {
            startActivity(Intent(this, NewRecipeActivity::class.java))
        }
        binding.recyclerViewRecipe.layoutManager = LinearLayoutManager(this)
        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val category = parent?.getItemAtPosition(position).toString()
                    loadRecycler(category)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        getCategory()
    }

    private fun loadRecycler(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getRecipeRepo = RecipeRepository()
                val getResponse =
                    getRecipeRepo.recipePerCategoryPerUser(LoginActivity.Username, "$category")
                if (getResponse.success == true) {
                    withContext(Main) {

                        if (getResponse?.data.count() <= 0) {
                            binding.tvRecipeCount.text =
                                "Recipe Count: 0"
                        }
                        binding.tvRecipeCount.text =
                            "Recipe Count: " + getResponse?.data.count().toString()

                        val adapter = MyRecipeAdapter(getResponse?.data, this@RecipeActivity)
                        binding.recyclerViewRecipe.adapter = adapter
                    }
                }
            } catch (ex: IOException) {
                withContext(Main) {
                    GlobalClass.globalToast(this@RecipeActivity, "Error: ${ex.localizedMessage}")
                }
            }
        }
    }

    private fun getCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val getRecipeRepo = RecipeRepository()
                val getResponse = getRecipeRepo.getDistinctCategory(LoginActivity.Username)
                if (getResponse.success == true) {
                    arrListRecipe = getResponse?.data!!
                    arrListRecipe.reverse()
                }
                withContext(Main) {
                    binding.spinnerCategory.adapter = ArrayAdapter(
                        this@RecipeActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        arrListRecipe
                    )
                }
            } catch (ex: IOException) {
                GlobalClass.globalToast(this@RecipeActivity, "Error : ${ex.localizedMessage}")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
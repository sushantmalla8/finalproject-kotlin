package com.app.merorecipe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.databinding.ActivityReipeDetailsBinding
import com.bumptech.glide.Glide

class RecipeDetailsActivity : AppCompatActivity() {
    private var _binding: ActivityReipeDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReipeDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadData()
    }

    private fun loadData() {
        val intent = intent
        if (intent.extras != null) {
            binding.tvInfo.text = intent.getStringExtra("RecipeName").toString()
            binding.tvDescription.text = intent.getStringExtra("Description").toString()
            binding.tvRecipeCategory.text ="Category: "+ intent.getStringExtra("Category").toString()
            val image = intent.getStringExtra("Image").toString()
            val path = ServiceBuilder.loadFilePath() + image.toString()
            Glide.with(this).load(path).into(binding.imgNewRecipe)
            val rawDate = intent.getStringExtra("Date").toString()
            val date = intent.getStringExtra("Date").toString().split("T")[0]
            val time = rawDate.split("T")?.get(1)?.split(".")?.get(0)
            binding.tvRecipeDate.text = "On: $date $time"
            binding.tvAddedBy.text = "By: "+intent.getStringExtra("addedBy").toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.app.merorecipe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.ui.NewRecipeActivity
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MyRecipeAdapter(
    private val listOfFavourite: ArrayList<Recipe>,
    private val context: Context

) : RecyclerView.Adapter<MyRecipeAdapter.MyRecipeViewHolder>() {

    private var recipeId: String = ""

    inner class MyRecipeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val favImage = itemView.findViewById<CircleImageView>(R.id.imgFav)
        val favRecipeName = itemView.findViewById<TextView>(R.id.tvRecipeName)
        val favRecipeCategory = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val icDelete = itemView.findViewById<TextView>(R.id.icDelete)
        val icEdit = itemView.findViewById<TextView>(R.id.icUpdate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipeViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.myrecipe_adapter_layout, parent, false)
        return MyRecipeViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyRecipeViewHolder, position: Int) {
        val favourite = listOfFavourite[position]
        holder.apply {
            recipeId = favourite._id.toString()
            favRecipeName.text = favourite.recipeName.toString()
            favRecipeCategory.text = favourite.category.toString()
            val photoPath = ServiceBuilder.loadFilePath() + favourite.photo
            Glide
                .with(context)
                .load(photoPath)
                .into(favImage)
            icDelete.setOnClickListener {
                deleteRecipe()

            }
            icEdit.setOnClickListener {
                context?.startActivity(Intent(context, NewRecipeActivity::class.java).apply {
                    putExtra("RecipeName", favourite.recipeName.toString())
                    putExtra("Description", favourite.recipeDescription.toString())
                    putExtra("Category", favourite.category.toString())
                    putExtra("Image", listOfFavourite[position].photo)
                    putExtra("Id", favourite._id.toString())
                })
            }

        }
    }

    private fun deleteRecipe() {
        CoroutineScope(Dispatchers.IO).launch {
            val getRecipeRepo = RecipeRepository()
            val getResponse =
                getRecipeRepo.deleteRecipe(recipeID = recipeId)
            if (getResponse.success == true) {
                withContext(Main) {
                    GlobalClass.globalToast(context, "Recipe Deleted")
                }
            }
        }
    }

    override fun getItemCount(): Int = listOfFavourite.size
}
package com.app.merorecipe.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.ui.LoginActivity
import com.app.merorecipe.ui.RecipeDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeHomeAdapter(
    private val listOfRecipe: ArrayList<Recipe>,
    private val context: Context
) : RecyclerView.Adapter<RecipeHomeAdapter.HomeViewHolder>(), Filterable {

    var isFav: Boolean = false

    private var arrListOfRecipe = arrayListOf<Recipe>()

    init {
        arrListOfRecipe = listOfRecipe
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.imgRecipeHome)
        val recipeName = itemView.findViewById<TextView>(R.id.tvRecipeNameHome)
        val recipeCategory = itemView.findViewById<TextView>(R.id.tvRecipeCategoryHome)
        val uploadedBy = itemView.findViewById<TextView>(R.id.tvUploadedBy)
        val uploadedOn = itemView.findViewById<TextView>(R.id.tvUploadedOn)
        val fav = itemView.findViewById<TextView>(R.id.tvFavHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.recipe_home_adpater_layout,
                    parent,
                    false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val recipe = listOfRecipe[position]
        holder.apply {
            recipeName.text = recipe.recipeName.toString()
            recipeCategory.text = recipe.category.toString()
            if (recipe.addedBy.toString() == LoginActivity.Username.toString()) {
                uploadedBy.text = "Uploaded By: You"
                fav.visibility = View.GONE
            } else {
                uploadedBy.text = "Uploaded By: ${recipe.addedBy.toString()}"
            }
            val date = recipe.createdAt?.split("T")?.get(0)
            val time = recipe!!.createdAt?.split("T")?.get(1)?.split(".")?.get(0)
            uploadedOn.text = date.toString() + " " + time.toString()
            val photoPath = ServiceBuilder.loadFilePath() + recipe.photo
            Glide
                .with(context)
                .load(photoPath)
                .into(image)
            if (recipe.isFav == true) {
                fav.setBackgroundResource(R.drawable.ic_red)
            } else {
                fav.setBackgroundResource(R.drawable.ic_fav_home)
            }
            var count = 0
            holder.fav.setOnClickListener {
                count++
                if (count % 2 == 0) {
                    isFav = true
                    updateToFav(recipe?._id!!, Recipe(isFav = recipe.isFav == isFav))

                } else {
                    isFav = false
                    updateToFav(recipe?._id!!, Recipe(isFav = recipe.isFav == isFav))
                }
                refresh()
            }
            holder.image.setOnClickListener {
                context?.startActivity(Intent(context, RecipeDetailsActivity::class.java).apply {
                    putExtra("RecipeName", recipe.recipeName.toString())
                    putExtra("Description", recipe.recipeDescription.toString())
                    putExtra("Category", recipe.category.toString())
                    putExtra("Image", listOfRecipe[position].photo)
                    putExtra("Date", recipe.createdAt.toString())
                    putExtra("addedBy", recipe.addedBy.toString())
                })
            }
        }

    }


    private fun updateToFav(recipeId: String, recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val getRecipeRepo = RecipeRepository()
            val getResponse = getRecipeRepo
                .updateToFav(
                    recipeId,
                    LoginActivity.Username,
                    recipe
                )

        }
    }

    private fun refresh() {
        notifyDataSetChanged()

    }


    override fun getItemCount(): Int = listOfRecipe.size

    //Filterable is used for search
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList = mutableListOf<Recipe>()
                if (constraint.toString().isEmpty()) {
                    filteredList.addAll(arrListOfRecipe);
                } else {
                    arrListOfRecipe.forEach { recipe ->
                        if (recipe.toString().toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                        ) {
                            filteredList.add(recipe);
                        }
                    }
                }
                var filterResult: FilterResults = FilterResults();
                filterResult.values = filteredList;
                return filterResult;
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listOfRecipe.clear();
                if (results != null) {
                    listOfRecipe.addAll(results.values as Collection<Recipe>)
                    notifyDataSetChanged();
                }
            }
        }
    }
}
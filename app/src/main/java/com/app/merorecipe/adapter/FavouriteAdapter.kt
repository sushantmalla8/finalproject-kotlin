package com.app.merorecipe.adapter

import android.content.Context
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
import com.app.merorecipe.ui.LoginActivity
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.expm1
import kotlin.math.sign

class FavouriteAdapter(
    private val listOfFavourite: ArrayList<Recipe>,
    private val context: Context

) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    private var favourite = Recipe()

    inner class FavouriteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val favImage = itemView.findViewById<CircleImageView>(R.id.imgFav)
        val favRecipeName = itemView.findViewById<TextView>(R.id.tvRecipeName)
        val favRecipeCategory = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val favButton = itemView.findViewById<TextView>(R.id.tvFav)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.favourite_adapter_layout, parent, false)
        return FavouriteViewHolder(view)

    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favourite = listOfFavourite[position]
        holder.apply {
            favRecipeName.text = favourite.recipeName.toString()
            favRecipeCategory.text = favourite.category.toString()
            favButton.setOnClickListener {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val getRecipeRepo = RecipeRepository()
                        val getResponse = getRecipeRepo
                            .updateToFav(
                                favourite._id.toString(),
                                LoginActivity.Username,
                                Recipe(isFav = false, favBy = "")
                            )
                        if (getResponse.success == true) {
                            withContext(Dispatchers.Main) {
                                GlobalClass.globalToast(
                                    context,
                                    "${favourite.recipeName} Removed From Favourite"
                                )
                            }

                        }
                    }
                } catch (ex: IOException) {
                    GlobalClass.globalToast(context, "Error: ${ex.localizedMessage}")
                }

            }
            val photoPath = ServiceBuilder.loadFilePath() + favourite.photo
            Glide
                .with(context)
                .load(photoPath)
                .into(favImage)
        }
    }


    override fun getItemCount(): Int = listOfFavourite.size
}
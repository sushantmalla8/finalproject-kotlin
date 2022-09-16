package com.app.merorecipe

import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.entity.User
import com.app.merorecipe.repository.RecipeRepository
import com.app.merorecipe.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class merorecipeUnitTest {

    //For Unit testing , person must change the base url to localhost:portNo in Service Builder Object
    //private const val BASE_URL = ("http://10.0.2.2:90/") to http://127.0.0.1:90/

    @Test
    fun registerTest() = runBlocking {
        val userRepository = UserRepository()
        val getResponse = userRepository.register(
            User(
                firstName = "Ram",
                lastName = "Shah",
                username = "ramshah",
                password = "ramshah",
                email = "ramshah@gmail.com",
                gender = "Male"
            )
        )
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun loginTestPass() = runBlocking {
        val userRepository = UserRepository()
        val getResponse = userRepository.login("ramshah", "ramshah")
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun loginTestFailed() = runBlocking {
        val userRepository = UserRepository()
        //Provided wrong username : actual Result must be false for the test to be failed.
        val getResponse = userRepository.login("ramshah", "ramshah")
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun showProfileTest() = runBlocking {
        val userRepository = UserRepository()
        //Only logged in users can generate token
        val getResponse = userRepository.login("ramshah", "ramshah")
        ServiceBuilder.token = "Bearer " + getResponse.token
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)

    }

    @Test
    fun addRecipeTest() = runBlocking {

        val userRepository = UserRepository()
        //Only logged in users can generate token
        val getUserResponse = userRepository.login("ramshah", "ramshah")
        ServiceBuilder.token = "Bearer " + getUserResponse.token
        val username = getUserResponse.data?.username


        val recipeRepository = RecipeRepository()
        val getResponse = recipeRepository.addNewRecipe(
            Recipe(
                recipeName = "Momos",
                recipeDescription = "One of the most sold fast food in Kathmandu",
                category = "Chinese Fast Food",
                addedBy = username
            )
        )
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getRecipePerCategoryPerUserTest() = runBlocking {

        val userRepository = UserRepository()
        //Only logged in users can generate token
        val getUserResponse = userRepository.login("ramshah", "ramshah")
        ServiceBuilder.token = "Bearer " + getUserResponse.token
        val username = getUserResponse.data?.username.toString()

        val recipeRepository = RecipeRepository()
        val getResponse = recipeRepository.recipePerCategoryPerUser(username, "Chinese Fast Food")
        val expectedResult: Boolean = true
        val actualResult = getResponse.success
        Assert.assertEquals(expectedResult, actualResult)

    }


}
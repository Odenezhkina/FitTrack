package ru.kpfu.itis.fittrack.data


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.fittrack.statsdata.EntireDatabase

class RecipeViewModel(application: Application): AndroidViewModel(application) {
    val getAllRecipes: LiveData<List<Recipe>>
    private val repository: RecipeRepository

    init {
//        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        val recipeDao = EntireDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        getAllRecipes = repository.getAllRecipes
    }

    fun addRecipe(recipe: Recipe){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecipe(recipe)
        }
    }


}

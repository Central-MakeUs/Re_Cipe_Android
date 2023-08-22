package com.cmc.recipe.presentation.ui.recipe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.cmc.recipe.R
import com.cmc.recipe.databinding.ActivityRecipeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navSetting()
    }

    private fun navSetting(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener {
                _, destination, _ ->
            binding.toolbarTitle.text = navController.currentDestination?.label.toString()
        }

        val graph = navController.navInflater.inflate(R.navigation.nav_recipe_graph)
        val args: RecipeActivityArgs = RecipeActivityArgs.fromBundle(intent.extras!!)

        if (args.theme?.isNotBlank() == true) {
            val bundle = Bundle()
            bundle.putString("theme", args.theme)

            graph.setStartDestination(R.id.recipeThemeFragment)
            navController.graph = graph
            navController.navigate(R.id.recipeThemeFragment,bundle)
        } else {
            graph.setStartDestination(R.id.recipeDetailFragment)
            navController.graph = graph
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun hideToolbar(state:Boolean){
        if(state){
            binding.toolbar.visibility = View.GONE
        }else{
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    fun setToolbarTitle(title:String){
        binding.toolbarTitle.text = title
    }
}
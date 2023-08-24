package com.cmc.recipe.domain.repository

import com.cmc.recipe.data.model.response.*
import com.cmc.recipe.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes() : Flow<NetworkState<RecipesResponse>>

    fun getRecipesDetail(id:Int) : Flow<NetworkState<RecipeDetailResponse>>

    fun postRecipesSave(id:Int) : Flow<NetworkState<BaseResponse>>

    fun postRecipesNotSave(id:Int) : Flow<NetworkState<BaseResponse>>

    fun getRecipesShortform() : Flow<NetworkState<ShortsResponse>>

    fun getRecipesShortformDetail(id:Int) : Flow<NetworkState<ShortsDetailResponse>>
}
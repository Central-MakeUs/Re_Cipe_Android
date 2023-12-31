package com.cmc.recipe.domain.usecase

import android.util.Log
import com.cmc.recipe.data.model.RecipeItem
import com.cmc.recipe.data.model.response.ShortsContent
import com.cmc.recipe.data.source.remote.request.RequestNickname
import com.cmc.recipe.data.source.remote.request.ReviewRequest
import com.cmc.recipe.domain.repository.AuthRepository
import com.cmc.recipe.domain.repository.RecipeRepository
import com.cmc.recipe.domain.repository.ShortsRepository
import com.cmc.recipe.domain.repository.UserRepository
import javax.inject.Inject

class ShortsUseCase @Inject constructor(
    private val repository: ShortsRepository
    ) {

    fun getRecipesShortform() = repository.getRecipesShortform()

    fun getRecipesShortformDetail(id:Int) = repository.getRecipesShortformDetail(id)

    fun postShortformLike(id:Int) = repository.postShortformLike(id)

    fun postShortformUnLike(id:Int) = repository.postShortformUnLike(id)

    fun postShortformSave(id:Int) = repository.postShortformSave(id)

    fun postShortformUnSave(id:Int) = repository.postShortformUnSave(id)

    fun reportShortform(id:Int) = repository.reportShortform(id)

    fun postReviewNoInterest(id:Int) = repository.postReviewNoInterest(id)

    //최근 본 레시피
    suspend fun insertRecentShorts(item: ShortsContent) = repository.insertRecentShorts(item)

    fun loadRecentShorts() = repository.loadRecentShorts()
}
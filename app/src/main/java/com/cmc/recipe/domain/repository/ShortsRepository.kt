package com.cmc.recipe.domain.repository

import com.cmc.recipe.data.model.response.*
import com.cmc.recipe.data.model.RecipeItem
import com.cmc.recipe.data.model.entity.RecipeEntity
import com.cmc.recipe.data.model.entity.ShortsEntity
import com.cmc.recipe.data.model.response.*
import com.cmc.recipe.data.source.remote.request.ReviewRequest
import com.cmc.recipe.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface ShortsRepository {

    fun getRecipesShortform() : Flow<NetworkState<ShortsResponse>>

    fun getRecipesShortformDetail(id:Int) : Flow<NetworkState<ShortsDetailResponse>>

    fun postShortformLike(id:Int) : Flow<NetworkState<BaseResponse>>

    fun postShortformUnLike(id:Int) : Flow<NetworkState<BaseResponse>>

    fun postShortformSave(id:Int) : Flow<NetworkState<BaseResponse>>

    fun postShortformUnSave(id:Int) : Flow<NetworkState<BaseResponse>>

    fun reportShortform(id:Int) : Flow<NetworkState<BaseResponse>>

    fun postReviewNoInterest(id:Int) : Flow<NetworkState<BaseResponse>>

    // 최근 본 레시피 저장
    suspend fun insertRecentShorts(item: ShortsContent): Boolean

    fun loadRecentShorts() : Flow<List<ShortsEntity>>

}
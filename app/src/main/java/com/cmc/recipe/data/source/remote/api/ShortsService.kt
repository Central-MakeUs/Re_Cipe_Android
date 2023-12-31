package com.cmc.recipe.data.source.remote.api

import com.cmc.recipe.data.model.response.*
import com.cmc.recipe.data.source.remote.request.ReviewRequest
import com.cmc.recipe.data.source.remote.request.UploadShortsRequest
import retrofit2.Response
import retrofit2.http.*

interface ShortsService {

    @GET("/api/v1/recipes/shortform")
    suspend fun getRecipesShortform() : Response<ShortsResponse>

    @GET("/api/v1/recipes/shortform/detail/{shortform-id}")
    suspend fun getRecipesShortformDetail(@Path("shortform-id")id:Int) : Response<ShortsDetailResponse>

    @POST("/api/v1/recipes/shortform/{shortform-recipe-id}/like")
    suspend fun postShortformLike(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

    @POST("/api/v1/recipes/shortform/{shortform-recipe-id}/unlike")
    suspend fun postShortformUnLike(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

    @POST("/api/v1/recipes/shortform/{shortform-recipe-id}/save")
    suspend fun postShortformSave(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

    @POST("/api/v1/recipes/shortform/{shortform-recipe-id}/unsave")
    suspend fun postShortformUnSave(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

    @GET("/api/v1/recipes/shortform/{shortform-recipe-id}/report")
    suspend fun reportShortform(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

    @GET("/api/v1/recipes/shortform/not-interest/{shortform-recipe-id}")
    suspend fun postReviewNoInterest(@Path("shortform-recipe-id")id:Int) : Response<BaseResponse>

}
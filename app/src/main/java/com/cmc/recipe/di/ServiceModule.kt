package com.cmc.recipe.di

import com.cmc.recipe.data.source.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun providesUserService(@Named("RecipeApi")retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun providesAuthService(@Named("RecipeApi")retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun providesRecipeService(@Named("RecipeApi")retrofit: Retrofit): RecipeService =
        retrofit.create(RecipeService::class.java)

    @Singleton
    @Provides
    fun providesSearchService(@Named("RecipeApi")retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)

    @Singleton
    @Provides
    fun providesUploadService(@Named("RecipeApi")retrofit: Retrofit): UploadService =
        retrofit.create(UploadService::class.java)

    @Singleton
    @Provides
    fun providesMyPageService(@Named("RecipeApi")retrofit: Retrofit): MyPageService =
        retrofit.create(MyPageService::class.java)

    @Singleton
    @Provides
    fun providesShortsService(@Named("RecipeApi")retrofit: Retrofit): ShortsService =
        retrofit.create(ShortsService::class.java)

    @Singleton
    @Provides
    fun providesCommentService(@Named("RecipeApi")retrofit: Retrofit): CommentService =
        retrofit.create(CommentService::class.java)

    @Singleton
    @Provides
    fun providesGoogleService(@Named("GoogleLogin") retrofit: Retrofit): GoogleService =
        retrofit.create(GoogleService::class.java)
}
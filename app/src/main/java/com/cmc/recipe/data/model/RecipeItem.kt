package com.cmc.recipe.data.model

data class RecipeItem(
    val comment_count: Int,
    val cook_time:Int,
    val created_date: String,
    var is_saved: Boolean,
    val nickname: String,
    val rating: Double,
    val recipe_id: Int,
    val recipe_name: String,
    val recipe_thumbnail_img: String,
)

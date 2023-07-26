package com.cmc.recipe.presentation.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cmc.recipe.data.model.RecipeReview
import com.cmc.recipe.databinding.ItemRecipeReviewBinding
import com.cmc.recipe.presentation.ui.base.BaseAdapter
import com.cmc.recipe.presentation.ui.base.BaseHolder
import com.cmc.recipe.presentation.ui.base.OnClickListener

class RecipeMenuReviewAdapter(private val upEventListener: OnClickListener,private val downEventListener: OnClickListener):
    BaseAdapter<RecipeReview, ItemRecipeReviewBinding, RecipeMenuReviewItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeMenuReviewItemHolder {
        return RecipeMenuReviewItemHolder(
            ItemRecipeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            upEventListener,
            downEventListener
        )
    }
}

class RecipeMenuReviewItemHolder(viewBinding: ItemRecipeReviewBinding,private val upEventListener: OnClickListener,private val downEventListener: OnClickListener):
    BaseHolder<RecipeReview, ItemRecipeReviewBinding>(viewBinding){
    override fun bind(binding: ItemRecipeReviewBinding, item: RecipeReview?) {
        binding.let { view ->
            item?.let { review ->
                view.tvReviewDate.text = review.date
                view.tvReviewNick.text = review.nick
                view.tvReviewThumb.text = review.content.substring(0,6)
                view.tvReview.text = review.content
                view.tvRatingbar.numStars = review.stars
                view.tvUpcount.text = "${review.thumb_up}"
                view.tvDowncount.text = "${review.thumb_down}"
            }
        }

    }
}
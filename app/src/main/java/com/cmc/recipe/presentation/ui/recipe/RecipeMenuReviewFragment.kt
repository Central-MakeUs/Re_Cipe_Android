package com.cmc.recipe.presentation.ui.recipe

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.recipe.R
import com.cmc.recipe.data.model.RecipeItem
import com.cmc.recipe.data.model.RecipeReview
import com.cmc.recipe.databinding.FragmentRecipeMenuBinding
import com.cmc.recipe.databinding.FragmentRecipeMenuReviewBinding
import com.cmc.recipe.presentation.ui.base.BaseFragment
import com.cmc.recipe.presentation.ui.base.OnClickListener

class RecipeMenuReviewFragment : BaseFragment<FragmentRecipeMenuReviewBinding>(FragmentRecipeMenuReviewBinding::inflate) {

    override fun initFragment() {
        val itemList = arrayListOf(
           RecipeReview(nick = "냉파 CMC", date = "2023.05.11", content = "기름이 너무 많아서 망했습니다. 맛은 있는데 약간 짜고 느끼한\n" +
                   "감이 좀 있어요. 그래도 다들 한번씩 해보길", stars = 3, thumb_up = 2, thumb_down = 2,image_list = null),
            RecipeReview(nick = "냉파 CMC", date = "2023.05.11", content = "기름이 너무 많아서 망했습니다. 맛은 있는데 약간 짜고 느끼한\n" +
                    "감이 좀 있어요. 그래도 다들 한번씩 해보길", stars = 3, thumb_up = 2, thumb_down = 2,image_list = null),
            RecipeReview(nick = "냉파 CMC", date = "2023.05.11", content = "기름이 너무 많아서 망했습니다. 맛은 있는데 약간 짜고 느끼한\n" +
                    "감이 좀 있어요. 그래도 다들 한번씩 해보길", stars = 3, thumb_up = 2, thumb_down = 2,image_list = null)
        )

        initRV(itemList)
    }

    private fun initRV(itemList:ArrayList<RecipeReview>){
        val clickListener = object : OnClickListener {
            override fun onMovePage(id: Int) {
            }
        }

        val adapter = RecipeMenuReviewAdapter(clickListener,clickListener)
        binding.rvReview.adapter = adapter
        binding.rvReview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvReview.addItemDecoration(dividerItemDecoration)
        adapter.replaceData(itemList)
    }
}
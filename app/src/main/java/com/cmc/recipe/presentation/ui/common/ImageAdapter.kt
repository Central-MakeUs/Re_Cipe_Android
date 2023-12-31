package com.cmc.recipe.presentation.ui.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cmc.recipe.databinding.ItemImageBinding
import com.cmc.recipe.presentation.ui.base.BaseAdapter
import com.cmc.recipe.presentation.ui.base.BaseHolder
import com.cmc.recipe.utils.dpToPx
import com.cmc.recipe.utils.loadImagesWithGlideRound

class ImageAdapter(val size : Int?):
    BaseAdapter<String, ItemImageBinding, ImageItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemHolder {
        return ImageItemHolder(
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            size
        )
    }
}

class ImageItemHolder(viewBinding: ItemImageBinding,val size : Int?):
    BaseHolder<String, ItemImageBinding>(viewBinding){
    override fun bind(binding: ItemImageBinding, item: String?) {
        if(size!=null){
            val pixel = dpToPx(viewDataBinding.root.context,size!!.toFloat())
            binding.imageView11.layoutParams.width = pixel
            binding.imageView11.layoutParams.height = pixel
        }else{
            val displayMetrics = viewDataBinding.root.resources.displayMetrics
            val pixel = dpToPx(viewDataBinding.root.context,5f)

            val width = displayMetrics.widthPixels / 3 - pixel

            binding.imageView11.layoutParams.width = width
            binding.imageView11.layoutParams.height = width
        }
        binding.imageView11.loadImagesWithGlideRound(item!!,10)

    }
}
package com.cmc.recipe.presentation.ui.upload

import com.cmc.recipe.databinding.DialogIngredientEtcBinding
import com.cmc.recipe.presentation.ui.base.BaseDialog
import com.cmc.recipe.utils.CommonTextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IngredientEtcDialog(val ingredient_name: String,val ingredient_unit: String) : BaseDialog<DialogIngredientEtcBinding>(DialogIngredientEtcBinding::inflate) {

    private lateinit var clickListener: onCountListener

    fun setListener(clickListener:onCountListener){
        this.clickListener = clickListener
    }

    override fun initDialog() {
        initView()

        binding.tvIngredientName.text = ingredient_name
        binding.tvIngredientUnit.text = ingredient_unit

        binding.etAmount.addTextChangedListener(CommonTextWatcher(
            onChanged = { text,_,_,_ ->
                binding.btnCheck.isEnabled = text!!.isNotEmpty()
            }
        ))

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnCheck.setOnClickListener {
            val amount = Integer.parseInt(binding.etAmount.text.toString())
            clickListener.getCount(amount)
            dismiss()
        }

    }

    private fun initView(){
        binding.tvIngredientName.text = ingredient_name

    }

    interface onCountListener{
        fun getCount(count:Int)
    }
}
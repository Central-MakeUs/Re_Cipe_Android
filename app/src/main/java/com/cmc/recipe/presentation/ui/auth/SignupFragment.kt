package com.cmc.recipe.presentation.ui.auth

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.cmc.recipe.MainApplication
import com.cmc.recipe.R
import com.cmc.recipe.databinding.FragmentSignupBinding
import com.cmc.recipe.presentation.ui.MainActivity
import com.cmc.recipe.presentation.ui.base.BaseFragment
import com.cmc.recipe.presentation.viewmodel.AuthViewModel
import com.cmc.recipe.presentation.viewmodel.UserViewModel
import com.cmc.recipe.utils.CommonTextWatcher
import com.cmc.recipe.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private val userViewModel : UserViewModel by viewModels()
    private val authViewModel : AuthViewModel by viewModels()

    private val args by navArgs<SignupFragmentArgs>()

    private var searchJob: Job? = null

    override fun initFragment() {
        onActiveButton()
        onClickSignup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }

    private fun onActiveButton(){
        binding.etNickName.addTextChangedListener(CommonTextWatcher(
            onChanged = { text,_,_,_ ->
                if(text.contentEquals(" ")){
                    showImage(false,"띄어쓰기를 제외한 한글 닉네임으로 작성해주세요")
                }else{
                    searchJob?.cancel()
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500L)
                        requestVerifyNickname(text.toString())
                    }
                }
            }
        ))
    }

    private fun showImage(flag:Boolean,msg:String){
        if(flag){
            binding.llValidateMsg.text = msg
            binding.ivEditIcon.setImageResource(R.drawable.ic_check)
            binding.btnNext.isEnabled = true
        }else{
            binding.llValidateMsg.text = msg
            binding.ivEditIcon.setImageResource(R.drawable.ic_validate_error)
            binding.btnNext.isEnabled = false
        }
    }

    private fun requestVerifyNickname(nickname:String){
        if(nickname.isNotBlank()){
            binding.llValidateMsg.visibility = View.VISIBLE
            binding.ivEditIcon.visibility = View.VISIBLE
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    userViewModel.verifyNickname(nickname)
                    userViewModel.verifyResult.collect{
                        when(it){
                            is NetworkState.Success -> {
                                it.data?.let {data ->
                                    if (data.code == "SUCCESS") {
                                        if(!data.data.isDuplicated){ // 중복 x
                                            showImage(true, "사용 가능한 닉네임입니다")
                                        }else{
                                            showImage(false, "중복된 닉네임입니다")
                                        }
                                    } else {
                                        showImage(false,"${data.data}")
                                    }
                                }
                                userViewModel._verifyResult.value = NetworkState.Loading
                            }
                            is NetworkState.Error ->{
                               // showToastMessage("${it.message}")
                                showImage(false, "${it.message}")
                                userViewModel._verifyResult.value = NetworkState.Loading
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // 회원가입 요청
    private fun requestSignup(nickname: String){
        authViewModel.signup(args.accessToken,nickname)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                authViewModel.signupResult.collect{
                    when(it){
                        is NetworkState.Success -> {
                            it.data?.let {data ->
                                if(data.code == "SUCCESS"){ // TODO : 변경
                                    saveTokens(data.data.accessToken,data.data.refreshToken)
                                    moveMainActivity()
                                }else{
                                    Log.d("data","${data.data}")
                                }
                            }
                            authViewModel._signupResult.value = NetworkState.Loading
                        }
                        is NetworkState.Error ->{
                            showToastMessage(it.message.toString())
                            authViewModel._signupResult.value = NetworkState.Loading
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun moveMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun onClickSignup(){
        binding.btnNext.setOnClickListener {
            val nickname = binding.etNickName.text.toString()
            requestSignup(nickname)
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        MainApplication.tokenManager.saveAccessToken(accessToken)
        MainApplication.tokenManager.saveRefreshToken(refreshToken)
    }

}
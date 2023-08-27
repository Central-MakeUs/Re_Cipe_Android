package com.cmc.recipe.presentation.ui.mypage

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cmc.recipe.MainApplication
import com.cmc.recipe.R
import com.cmc.recipe.databinding.FragmentSettingBinding
import com.cmc.recipe.presentation.ui.base.BaseFragment
import com.cmc.recipe.presentation.viewmodel.AuthViewModel
import com.cmc.recipe.presentation.viewmodel.UserViewModel
import com.cmc.recipe.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private val authViewModel : AuthViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    override fun initFragment() {

        initView()
        binding.let {
            it.menuNick.setOnClickListener {
                movePage(R.id.action_settingFragment_to_mypageNicknameFragment)
            }

            it.menuInfo.setOnClickListener {

            }

            it.menuTerms.setOnClickListener {
                movePage(R.id.action_settingFragment_to_mypageTermsFragment)
            }

            it.menuQna.setOnClickListener {
                movePage(R.id.action_settingFragment_to_mypageQnaFragment)
            }

            it.menuLogout.setOnClickListener {
                logout()
            }

            it.menuWithdrawal.setOnClickListener {
                withdrawal()
            }
        }
    }

    private fun initView() {
        userViewModel.getMyInfo()
        launchWithLifecycle(lifecycle) {
            userViewModel.myInfoResult.collect{
                when(it){
                    is NetworkState.Success -> {
                        it.data?.let {data ->
                            if(data.code == "SUCCESS"){
                                binding.tvNick.text = it.data.data.nickname
                            }
                        }
                        userViewModel._myInfoResult.value = NetworkState.Loading
                    }
                    is NetworkState.Error ->{
                        showToastMessage(it.message.toString())
                        userViewModel._myInfoResult.value = NetworkState.Loading
                    }
                    else -> {}
                }
            }
        }
    }

    private fun logout(){
        val refreshToken = MainApplication.tokenManager.getRefreshToken()
        val accessToken = MainApplication.tokenManager.getAccessToken()
        authViewModel.logout(accessToken,refreshToken)

        launchWithLifecycle(lifecycle) {
            authViewModel.logoutResult.collect{
                when(it){
                    is NetworkState.Success -> {
                        it.data?.let {data ->
                            if(data.code == "SUCCESS"){ // TODO : 변경
                                Log.d("data","${data.data}")
                                movePage(R.id.action_settingFragment_to_loginFragment)
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

    private fun withdrawal(){

    }
}
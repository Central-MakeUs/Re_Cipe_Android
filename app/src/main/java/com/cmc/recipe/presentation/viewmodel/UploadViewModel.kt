package com.cmc.recipe.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cmc.recipe.data.model.response.BaseResponse
import com.cmc.recipe.data.model.response.IngredientsResponse
import com.cmc.recipe.data.model.response.MyInfoResponse
import com.cmc.recipe.data.source.remote.request.RequestNickname
import com.cmc.recipe.data.source.remote.request.UploadRecipeRequest
import com.cmc.recipe.data.source.remote.request.UploadShortsRequest
import com.cmc.recipe.domain.usecase.UploadUseCase
import com.cmc.recipe.domain.usecase.UserUseCase
import com.cmc.recipe.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val uploadUseCase: UploadUseCase) : ViewModel() {

    var _uploadImageResult: MutableSharedFlow<NetworkState<BaseResponse>> = MutableSharedFlow()
    var uploadImageResult = _uploadImageResult.asSharedFlow()

    var _uploadVideoResult: MutableSharedFlow<NetworkState<BaseResponse>> = MutableSharedFlow()
    var uploadVideoResult = _uploadVideoResult.asSharedFlow()

    var _uploadRecipeResult: MutableSharedFlow<NetworkState<BaseResponse>> = MutableSharedFlow()
    var uploadRecipeResult: SharedFlow<NetworkState<BaseResponse>> = _uploadRecipeResult

    var _uploadShortsResult: MutableSharedFlow<NetworkState<BaseResponse>> = MutableSharedFlow()
    var uploadShortsResult: SharedFlow<NetworkState<BaseResponse>> = _uploadShortsResult

    var _ingredientsResult: MutableSharedFlow<NetworkState<IngredientsResponse>> = MutableSharedFlow()
    var ingredientsResult: SharedFlow<NetworkState<IngredientsResponse>> = _ingredientsResult

    fun uploadImage(file: MultipartBody.Part) = viewModelScope.launch {
        _uploadImageResult.emit(NetworkState.Loading)
        uploadUseCase.uploadImage(file)
            .catch { error ->
                _uploadImageResult.emit(NetworkState.Error(400, "${error.message}"))
            }
            .collect { values ->
                _uploadImageResult.emit(values)
            }
    }

    fun uploadVideo(file: MultipartBody.Part) = viewModelScope.launch {
        _uploadVideoResult.emit(NetworkState.Loading)
        uploadUseCase.uploadVideo(file)
            .catch { error ->
                _uploadVideoResult.emit(NetworkState.Error(400, "${error.message}"))
            }
            .collect { values ->
                _uploadVideoResult.emit(values)
            }
    }

    fun uploadShorts(request:UploadShortsRequest)= viewModelScope.launch {
        _uploadShortsResult.emit(NetworkState.Loading)
        uploadUseCase.uploadShorts(request)
            .catch { error ->
                _uploadShortsResult.emit(NetworkState.Error(400,"${error.message}"))
            }.collect { values ->
                _uploadShortsResult.emit(values)
            }
    }

    fun uploadRecipe(request:UploadRecipeRequest) = viewModelScope.launch {
        _uploadRecipeResult.emit(NetworkState.Loading)
        uploadUseCase.uploadRecipe(request)
            .catch { error ->
                _uploadRecipeResult.emit(NetworkState.Error(400,"${error.message}"))
            }.collect { values ->
                _uploadRecipeResult.emit(values)
            }
    }

    fun getIngredients() = viewModelScope.launch {
        _ingredientsResult.emit(NetworkState.Loading)
        uploadUseCase.getIngredients()
            .catch { error ->
                _ingredientsResult.emit(NetworkState.Error(400,"${error.message}"))
            }.collect { values ->
                _ingredientsResult.emit(values)
            }
    }

}
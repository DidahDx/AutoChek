package com.github.didahdx.autochek.ui.carDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.didahdx.autochek.data.remote.dto.CarMedia
import com.github.didahdx.autochek.data.remote.dto.carDetails.CarDetailsList
import com.github.didahdx.autochek.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    val carRepository: CarRepository
) : ViewModel() {

    val carMedia = MutableLiveData<List<CarMedia>>()
    val carDetails = MutableLiveData<CarDetailsList>()
    val selectedCarDetail = MutableLiveData<CarMedia>()
    val uiState = MutableLiveData<UiState>()

    fun fetchData(cardId: String) {
        viewModelScope.launch {
            try {
               uiState.postValue(UiState.Loading(true))
                carDetails.postValue(carRepository.getCarDetails(cardId))
                val mediaList = carRepository.getCarMedia(cardId).carMediaList
                carMedia.postValue(mediaList)
                update(mediaList[0])
                uiState.postValue(UiState.Success)
            } catch (e: Exception) {
                uiState.postValue(UiState.Error("\uD83D\uDE28 Wooops ${e.localizedMessage}"))
                Timber.e(e, e.localizedMessage)
            }
        }
    }

    fun update(carMedia: CarMedia) {
        viewModelScope.launch {
            selectedCarDetail.postValue(carMedia)
        }
    }
}
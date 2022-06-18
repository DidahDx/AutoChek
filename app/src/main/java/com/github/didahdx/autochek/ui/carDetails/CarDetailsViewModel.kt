package com.github.didahdx.autochek.ui.carDetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.data.model.Details
import com.github.didahdx.autochek.data.remote.dto.CarMedia
import com.github.didahdx.autochek.data.remote.dto.carDetails.CarDetailsList
import com.github.didahdx.autochek.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val state: SavedStateHandle,
    private val application: Application
) : ViewModel() {

    private val cardId = state.getLiveData(CarDetailsFragment.CARD_ID, "")
    val carMedia = MutableLiveData<List<CarMedia>>()
    val carDetails = MutableLiveData<CarDetailsList>()
    val selectedCarDetail = MutableLiveData<CarMedia>()
    val carDetailList = MutableLiveData<List<Details>>()
    val uiState = MutableLiveData<UiState>()
    val currentSeekTime = MutableLiveData<Long>()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                uiState.postValue(UiState.Loading)

                val carDetail = carRepository.getCarDetails(cardId.value!!)
                carDetails.postValue(carDetail)

                val mediaList = carRepository.getCarMedia(cardId.value!!).carMediaList
                carMedia.postValue(mediaList)
                update(mediaList[0])

                val detail = carRepository.getCarDetails(carDetail)
                carDetailList.postValue(detail)

                uiState.postValue(UiState.Success)
            } catch (e: Exception) {
                uiState.postValue(
                    UiState.Error(
                        application.getString(
                            R.string.error_message,
                            e.localizedMessage
                        )
                    )
                )
                Timber.e(e, e.localizedMessage)
            }
        }
    }

    fun update(carMedia: CarMedia) {
        viewModelScope.launch {
            selectedCarDetail.postValue(carMedia)
        }
    }

    fun updateSeekTime(seek:Long){
        viewModelScope.launch {
            currentSeekTime.postValue(seek)
        }
    }
}
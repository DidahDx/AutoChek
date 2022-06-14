package com.github.didahdx.autochek.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.didahdx.autochek.data.remote.dto.Make
import com.github.didahdx.autochek.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    val popularMake: MutableLiveData<List<Make>> = MutableLiveData<List<Make>>()
    val carFlowList = carRepository.getCarListPaging().cachedIn(viewModelScope)

    fun fetchCarData() {
        viewModelScope.launch {
            try {
                popularMake.postValue(carRepository.getPopularMake().makeList)
            } catch (e: Exception) {
                Timber.e(e, e.localizedMessage)
            }
        }
    }


}
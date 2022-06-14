package com.github.didahdx.autochek.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.data.remote.dto.Make
import com.github.didahdx.autochek.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val carRepository: CarRepository
) : ViewModel() {

    val popularMake: MutableLiveData<List<Make>> = MutableLiveData<List<Make>>()
    val carList: MutableLiveData<List<CarDetails>> = MutableLiveData<List<CarDetails>>()

    fun fetchCarData() {
        viewModelScope.launch {
            popularMake.postValue(carRepository.getPopularMake().makeList)
            carList.postValue(carRepository.getCarList().result)
        }
    }


}
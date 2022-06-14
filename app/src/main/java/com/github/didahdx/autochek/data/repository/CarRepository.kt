package com.github.didahdx.autochek.data.repository

import androidx.paging.PagingData
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.data.remote.dto.CarList
import com.github.didahdx.autochek.data.remote.dto.CarMediaList
import com.github.didahdx.autochek.data.remote.dto.PopularMakeList
import com.github.didahdx.autochek.data.remote.dto.carDetails.CarDetailsList
import kotlinx.coroutines.flow.Flow

/**
 * @author Daniel Didah on 6/13/22
 */
interface CarRepository {
    suspend fun getPopularMake(): PopularMakeList
    suspend fun getCarList(): CarList
    fun getCarListPaging(): Flow<PagingData<CarDetails>>
    suspend fun getCarDetails(cardId: String): CarDetailsList
    suspend fun getCarMedia(cardId: String): CarMediaList
}
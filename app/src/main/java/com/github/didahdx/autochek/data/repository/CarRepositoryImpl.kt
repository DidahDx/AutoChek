package com.github.didahdx.autochek.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.didahdx.autochek.data.remote.api.CarApiServices
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.data.remote.dto.CarList
import com.github.didahdx.autochek.data.remote.dto.CarMediaList
import com.github.didahdx.autochek.data.remote.dto.PopularMakeList
import com.github.didahdx.autochek.data.remote.dto.carDetails.CarDetailsList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Daniel Didah on 6/13/22
 */
@Singleton
class CarRepositoryImpl @Inject constructor(
    private val carApiServices: CarApiServices,
    private val carPagingSource: CarPagingSource
) : CarRepository {
    override suspend fun getPopularMake(): PopularMakeList {
        return carApiServices.getPopularMake()
    }

    override suspend fun getCarList(): CarList {
        return carApiServices.getCarList()
    }

    override fun getCarListPaging(): Flow<PagingData<CarDetails>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { carPagingSource }
        ).flow
    }

    override suspend fun getCarDetails(cardId: String): CarDetailsList {
        return carApiServices.getCarDetails(cardId)
    }

    override suspend fun getCarMedia(cardId: String): CarMediaList {
        return carApiServices.getCarMedia(cardId)
    }


}
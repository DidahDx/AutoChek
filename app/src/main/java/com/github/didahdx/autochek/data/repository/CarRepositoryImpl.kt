package com.github.didahdx.autochek.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.NumberFormat
import com.github.didahdx.autochek.data.CarPagingSource
import com.github.didahdx.autochek.data.NETWORK_PAGE_SIZE
import com.github.didahdx.autochek.data.model.Details
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
    private val carPagingSource: CarPagingSource,
    private val application: Application
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

    override suspend fun getCarDetails(carDetail: CarDetailsList): List<Details> {
        val detail = ArrayList<Details>()
        detail.add(Details(R.string.car_name, carDetail.carName))
        detail.add(Details(R.string.model, carDetail.model?.name))
        detail.add(Details(R.string.year, carDetail.year.toString()))
        detail.add(Details(R.string.vehicle_id, carDetail.id))
        detail.add(Details(R.string.engine_type, carDetail.engineType))

        val millage = if (carDetail.mileage != null) {
            NumberFormat.formatNumber(carDetail.mileage) + " " + carDetail.mileageUnit
        } else null
        detail.add(Details(R.string.mileage, millage))
        detail.add(Details(R.string.state, carDetail.state))
        detail.add(Details(R.string.city, carDetail.city))
        detail.add(Details(R.string.country, carDetail.country))

        val price = if (carDetail.marketplacePrice != null) {
            application.getString(
                R.string.price_amount,
                NumberFormat.formatNumber(carDetail.marketplacePrice)
            )
        } else null

        val sold = if (carDetail.sold != null && carDetail.sold) { application.getString(R.string.yes) } else { application.getString(R.string.no) }
        val hasFinancing = if (carDetail.hasFinancing != null && carDetail.hasFinancing) { application.getString(R.string.yes) } else { application.getString(R.string.no) }
        val hasWarranty = if (carDetail.hasWarranty != null && carDetail.hasWarranty) { application.getString(R.string.yes) } else { application.getString(R.string.no) }

        detail.add(Details(R.string.price, price))
        detail.add(Details(R.string.vin, carDetail.vin))
        detail.add(Details(R.string.has_financing, hasFinancing))
        detail.add(Details(R.string.has_warranty, hasWarranty))
        detail.add(Details(R.string.owner_type, carDetail.ownerType))
        detail.add(Details(R.string.sold, sold))
        detail.add(Details(R.string.transmission, carDetail.transmission))
        detail.add(Details(R.string.selling_condition, carDetail.sellingCondition))
        detail.add(Details(R.string.fuel_type, carDetail.fuelType))
        detail.add(Details(R.string.body_type, carDetail.bodyType?.name))
        detail.add(Details(R.string.interior_color, carDetail.interiorColor))
        detail.add(Details(R.string.exterior_color, carDetail.exteriorColor))


        return detail
    }

    override suspend fun getCarMedia(cardId: String): CarMediaList {
        return carApiServices.getCarMedia(cardId)
    }


}
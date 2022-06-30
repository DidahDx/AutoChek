package com.github.didahdx.autochek.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.Constants.MAX_PAGE_SIZE
import com.github.didahdx.autochek.common.Constants.NETWORK_PAGE_SIZE
import com.github.didahdx.autochek.common.NumberFormat
import com.github.didahdx.autochek.data.CarPagingSource
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
                enablePlaceholders = false,
                maxSize = MAX_PAGE_SIZE
            ),
            pagingSourceFactory = { carPagingSource }
        ).flow
    }

    override suspend fun getCarDetails(cardId: String): CarDetailsList {
        return carApiServices.getCarDetails(cardId)
    }

    override suspend fun getCarDetails(carDetailsList: CarDetailsList): List<Details> {
        val detail = ArrayList<Details>()
        detail.add(Details(R.string.car_name, carDetailsList.carName))
        detail.add(Details(R.string.model, carDetailsList.model?.name))
        detail.add(Details(R.string.year, carDetailsList.year.toString()))
        detail.add(Details(R.string.vehicle_id, carDetailsList.id))
        detail.add(Details(R.string.engine_type, carDetailsList.engineType))

        val millage = if (carDetailsList.mileage != null) {
            NumberFormat.formatNumber(carDetailsList.mileage) + " " + carDetailsList.mileageUnit
        } else null
        detail.add(Details(R.string.mileage, millage))
        detail.add(Details(R.string.state, carDetailsList.state))
        detail.add(Details(R.string.city, carDetailsList.city))
        detail.add(Details(R.string.country, carDetailsList.country))

        val price = if (carDetailsList.marketplacePrice != null) {
            application.getString(
                R.string.price_amount,
                NumberFormat.formatNumber(carDetailsList.marketplacePrice)
            )
        } else null

        val sold = if (carDetailsList.sold != null && carDetailsList.sold) {
            application.getString(R.string.yes)
        } else {
            application.getString(R.string.no)
        }
        val hasFinancing = if (carDetailsList.hasFinancing != null && carDetailsList.hasFinancing) {
            application.getString(R.string.yes)
        } else {
            application.getString(R.string.no)
        }
        val hasWarranty = if (carDetailsList.hasWarranty != null && carDetailsList.hasWarranty) {
            application.getString(R.string.yes)
        } else {
            application.getString(R.string.no)
        }

        detail.add(Details(R.string.price, price))
        detail.add(Details(R.string.vin, carDetailsList.vin))
        detail.add(Details(R.string.has_financing, hasFinancing))
        detail.add(Details(R.string.has_warranty, hasWarranty))
        detail.add(Details(R.string.owner_type, carDetailsList.ownerType))
        detail.add(Details(R.string.sold, sold))
        detail.add(Details(R.string.transmission, carDetailsList.transmission))
        detail.add(Details(R.string.selling_condition, carDetailsList.sellingCondition))
        detail.add(Details(R.string.fuel_type, carDetailsList.fuelType))
        detail.add(Details(R.string.body_type, carDetailsList.bodyType?.name))
        detail.add(Details(R.string.interior_color, carDetailsList.interiorColor))
        detail.add(Details(R.string.exterior_color, carDetailsList.exteriorColor))


        return detail
    }

    override suspend fun getCarMedia(cardId: String): CarMediaList {
        return carApiServices.getCarMedia(cardId)
    }


}
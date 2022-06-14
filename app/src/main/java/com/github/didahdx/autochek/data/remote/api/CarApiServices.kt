package com.github.didahdx.autochek.data.remote.api

import com.github.didahdx.autochek.data.remote.dto.CarList
import com.github.didahdx.autochek.data.remote.dto.CarMediaList
import com.github.didahdx.autochek.data.remote.dto.PopularMakeList
import com.github.didahdx.autochek.data.remote.dto.carDetails.CarDetailsList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Daniel Didah on 6/13/22
 */
interface CarApiServices {

    @GET("make")
    suspend fun getPopularMake(@Query("popular") popular: Boolean = true): PopularMakeList

    @GET("car/search")
    suspend fun getCarList(@Query("page_number") pageNumber: Int = 1): CarList

    @GET("car/{carId}")
    suspend fun getCarDetails(@Path("carId") cardId: String): CarDetailsList

    @GET("car_media")
    suspend fun getCarMedia(@Query("carId") cardId: String): CarMediaList

}
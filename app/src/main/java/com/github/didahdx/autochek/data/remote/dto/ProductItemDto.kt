package com.github.didahdx.autochek.data.remote.dto

import com.squareup.moshi.Json

/**
 * @author Daniel Didah on 6/13/22
 */
data class ProductItemDto(
    @Json(name = "image")
    var image: String,
    @Json(name = "title")
    var title: String,
    @Json(name = "brand_name")
    var brandName: String,
    @Json(name = "price")
    var price: Int,
    @Json(name = "rating")
    var rating: Int
)

package com.github.didahdx.autochek.data.remote.dto

data class CarDetails(
    val bodyTypeId: String?,
    val city: String?,
    val depositReceived: Boolean,
    val fuelType: String?,
    val gradeScore: Double?,
    val hasFinancing: Boolean,
    val hasThreeDImage: Boolean,
    val hasWarranty: Boolean,
    val id: String,
    val imageUrl: String,
    val installment: Double?,
    val licensePlate: String?,
    val loanValue: Double?,
    val marketplaceOldPrice: Int?,
    val marketplacePrice: Int?,
    val marketplaceVisibleDate: String?,
    val mileage: Int,
    val mileageUnit: String?,
    val sellingCondition: String?,
    val model: Model?,
    val sold: Boolean,
    val state: String?,
    val stats: Stats?,
    val title: String?,
    val transmission: String?,
    val websiteUrl: String?,
    val year: Int,
    val carName: String?,
)
package com.github.didahdx.autochek.data.remote.dto.carDetails

data class Ranges(
    val maxDownPayment: Double,
    val maxInterestRate: Double,
    val minDownPayment: Double,
    val minInterestRate: Double,
    val tenure: Int
)
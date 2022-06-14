package com.github.didahdx.autochek.data.remote.dto.carDetails

data class LoanCalculator(
    val defaultValues: DefaultValues,
    val loanPercentage: Double,
    val ranges: Ranges
)
package com.github.didahdx.autochek.data.remote.dto

data class CarList(
    val pagination: Pagination,
    val result: List<CarDetails>
)
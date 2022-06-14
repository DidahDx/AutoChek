package com.github.didahdx.autochek.data.remote.dto

data class Pagination(
    val currentPage: Int,
    val pageSize: Int,
    val total: Int
)
package com.github.didahdx.autochek.data.remote.dto

data class Model(
    val id: Int,
    val imageUrl: String,
    val make: Make,
    val name: String,
    val popular: Boolean,
    val wheelType: String
)
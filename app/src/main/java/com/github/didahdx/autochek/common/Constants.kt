package com.github.didahdx.autochek.common

/**
 * @author Daniel Didah on 6/13/22
 */
object Constants {
    const val END_POINT = "https://api.staging.myautochek.com/v1/inventory/"
    const val CAR_PAGE_INDEX = 1
    private const val PREFETCHED_PAGE_SIZE = 3
    const val NETWORK_PAGE_SIZE = 30
    const val MAX_PAGE_SIZE = NETWORK_PAGE_SIZE * PREFETCHED_PAGE_SIZE
}
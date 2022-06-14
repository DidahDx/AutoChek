package com.github.didahdx.autochek.common

import java.text.DecimalFormat

/**
 * @author Daniel Didah on 6/14/22
 */
object NumberFormat {
    private val twoDecimalPoint = DecimalFormat("###,###,###.##")

    fun formatNumber(number: Int): String {
        return twoDecimalPoint.format(number)
    }
}
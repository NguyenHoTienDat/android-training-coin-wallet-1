package com.framgia.bitcoinwallet.util

import java.util.*

fun Date.calculateTimeDuration(endDate: Date): Long {
    var mls: Long = endDate.time - time
    when (mls.toInt()) {
        0 -> mls = 0
        else -> mls /= 1000
    }
    return mls
}


package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.arch.lifecycle.MutableLiveData
import com.framgia.bitcoinwallet.data.model.ReceiveCoin
import java.text.SimpleDateFormat
import java.util.*

class ReceiveTransactionViewModel(item: ReceiveCoin) {
    val receiveTransaction: MutableLiveData<ReceiveCoin> = MutableLiveData()

    companion object {
        const val DATE_TIME_FORMAT = "HH:mm dd/MM/YYYY"
    }

    init {
        receiveTransaction.value = item
    }

    fun getConvertTimeStamp(): String {
        var timeStamp: Date =
                SimpleDateFormat(DATE_TIME_FORMAT).parse(receiveTransaction.value?.timestamp)

        return "${timeStamp.day}/${timeStamp.month}/${timeStamp.year}"
    }
}

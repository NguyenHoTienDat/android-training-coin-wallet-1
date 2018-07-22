package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.arch.lifecycle.MutableLiveData
import com.framgia.bitcoinwallet.data.model.SendCoin
import java.text.SimpleDateFormat
import java.util.*

class SendTransactionViewModel(item: SendCoin) {
    val sendTransaction: MutableLiveData<SendCoin> = MutableLiveData()

    companion object {
        const val DATE_TIME_FORMAT = "HH:mm dd/MM/YYYY"
    }

    init {
        sendTransaction.value = item
    }

    fun getConvertTimeStamp(): String {
        var timeStamp: Date =
                SimpleDateFormat(DATE_TIME_FORMAT).parse(sendTransaction.value?.timestamp)

        return "$timeStamp"
    }
}

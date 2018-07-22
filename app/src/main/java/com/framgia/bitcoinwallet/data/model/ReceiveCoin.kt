package com.framgia.bitcoinwallet.data.model

data class ReceiveCoin(var senderAddress: String = "",
                       var amount: Float = 0.0f,
                       var note: String = "",
                       var timestamp: String = "")


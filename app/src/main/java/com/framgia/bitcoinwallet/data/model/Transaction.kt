package com.framgia.bitcoinwallet.data.model

data class Transaction(var interactAddress: String = "",
                       var amount: Float = 0.0f,
                       var note: String = "",
                       var timestamp: String = "")
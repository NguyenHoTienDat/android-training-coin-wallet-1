package com.framgia.bitcoinwallet.data.model

data class SendCoin(var receiverAddress: String = "",
                    var amount: Float = 0.0f,
                    var note: String = "",
                    var timestamp: String = "")

package com.framgia.bitcoinwallet.data.model

class Wallet(var coin: Float, var createAt: String, var name: String) {
    constructor() : this(0f,"","Personal Wallet")
    var id: String = ""
    var isChoosed: Boolean = false
}

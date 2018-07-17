package com.framgia.bitcoinwallet.ui.screen.wallet

import android.arch.lifecycle.MutableLiveData
import com.framgia.bitcoinwallet.data.model.Wallet

class ItemWalletViewModel(item: Wallet) {

    val wallet: MutableLiveData<Wallet> = MutableLiveData()
    val isShowCheckUi: MutableLiveData<Boolean> = MutableLiveData()

    init {
        wallet.value = item
    }

    fun coinToString() = wallet.value?.coin.toString()
}

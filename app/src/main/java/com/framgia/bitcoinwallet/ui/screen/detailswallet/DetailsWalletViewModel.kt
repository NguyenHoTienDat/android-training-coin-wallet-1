package com.framgia.bitcoinwallet.ui.screen.detailswallet

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleObserver
import com.framgia.bitcoinwallet.data.source.repository.UserRepository

class DetailsWalletViewModel(private val context: Application,
                             private val userRepository: UserRepository)
    : AndroidViewModel(context), LifecycleObserver {
}
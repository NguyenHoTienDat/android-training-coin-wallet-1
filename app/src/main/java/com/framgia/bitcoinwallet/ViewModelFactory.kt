package com.framgia.bitcoinwallet

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.framgia.bitcoinwallet.data.source.local.UserLocalDatasource
import com.framgia.bitcoinwallet.data.source.remote.UserRemoteDatasource
import com.framgia.bitcoinwallet.data.source.repository.BitCoinRepository
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.ui.screen.changepassws.ChangePassWdViewModel
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinPriceViewModel
import com.framgia.bitcoinwallet.ui.screen.detailswallet.DetailsWalletViewModel
import com.framgia.bitcoinwallet.ui.screen.forgotpasswd.ForgotPassWdViewModel
import com.framgia.bitcoinwallet.ui.screen.login.LoginViewModel
import com.framgia.bitcoinwallet.ui.screen.main.MainViewModel
import com.framgia.bitcoinwallet.ui.screen.main.receivecointab.ReceiveViewModel
import com.framgia.bitcoinwallet.ui.screen.main.sendcointab.SendCoinViewModel
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionViewModel
import com.framgia.bitcoinwallet.ui.screen.signup.SignUpViewModel
import com.framgia.bitcoinwallet.ui.screen.wallet.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ViewModelFactory private constructor(
        private val application: Application,
        private val bitcointRepository: BitCoinRepository,
        private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(ForgotPassWdViewModel::class.java) ->
                        ForgotPassWdViewModel(application, userRepository)

                    isAssignableFrom(LoginViewModel::class.java) ->
                        LoginViewModel(application, userRepository)

                    isAssignableFrom(SendCoinViewModel::class.java) ->
                        SendCoinViewModel(application, userRepository)

                    isAssignableFrom(ReceiveViewModel::class.java) ->
                        ReceiveViewModel(application, userRepository)

                    isAssignableFrom(MainViewModel::class.java) ->
                        MainViewModel(application, userRepository)

                    isAssignableFrom(WalletViewModel::class.java) ->
                        WalletViewModel(application, userRepository)

                    isAssignableFrom(SignUpViewModel::class.java) ->
                        SignUpViewModel(application, userRepository)

                    isAssignableFrom(DetailsWalletViewModel::class.java) ->
                        DetailsWalletViewModel(application, userRepository)

                    isAssignableFrom(TransactionViewModel::class.java) ->
                        TransactionViewModel(application, userRepository)

                    isAssignableFrom(CoinPriceViewModel::class.java) ->
                        CoinPriceViewModel(application, userRepository)

                    isAssignableFrom(ChangePassWdViewModel::class.java) ->
                        ChangePassWdViewModel(application, userRepository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T


    companion object {
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) = INSTANCE ?: ViewModelFactory(application,
                BitCoinRepository.getInstance(),
                UserRepository.getInstance(
                        UserRemoteDatasource.getInstance(FirebaseAuth.getInstance(),
                                FirebaseDatabase.getInstance()),
                        UserLocalDatasource.getInstance()))

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

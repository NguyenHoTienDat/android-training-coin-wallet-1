package com.framgia.bitcoinwallet.ui.screen.wallet

import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.Wallet
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.util.SharedPreUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.reflect.Array.set
import java.util.ArrayList

class WalletViewModel(private val context: Application,
                      private val userRepository: UserRepository)
    : AndroidViewModel(context), LifecycleObserver {

    val currentWallet: MutableLiveData<Wallet> = MutableLiveData()
    val notifyMessage: MutableLiveData<String> = MutableLiveData()
    val wallets: MutableLiveData<MutableList<Wallet>> = MutableLiveData()
    val newWalletAdded: MutableLiveData<Wallet> = MutableLiveData()
    val isChangeWalletClick: MutableLiveData<Boolean> = MutableLiveData()

    var walletsTmp = mutableListOf<Wallet>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startViewModel() {
        getWalletsOfCurrentUser(getCurrentUserId())
        getCurrentWalletInfor(getCurrentUserId(), getCurrentWalletAddress())
    }

    fun changeCurrentWallet() {
        if (isChangeWalletClick.value == true) { //case item click to change wallet

        } else { //case item click to show details wallet screen

        }
    }

    fun addWallet(name: String) {
        wallets.value?.let {
            when (it.find { it.name == name }) {
                null -> {
                    userRepository.addWallet(getCurrentUserId(), name).subscribe(
                            {
                                wallets.value?.add(it)
                                newWalletAdded.value = it
                            },
                            {

                            }
                    )
                }
                else -> {
                    notifyMessage.value = context.getString(R.string.wallet_name_exist_title)
                }
            }
        }
    }

    fun changeWallet(currentChoosed: Int) {
        val walletTmp = currentWallet.value
 /*       currentWallet.value = walletsTmp[currentChoosed]
        SharedPreUtils.saveWalletAddress(context, currentWallet.value!!.id)*/
        Log.e("xxx", "be1 size: "+ walletsTmp.size )
        with(walletsTmp) {
            removeAt(currentChoosed)
            walletTmp?.let { add(it) }
        }
        Log.e("xxx", "be2 size: "+ walletsTmp.size )
        wallets.value = walletsTmp
    }

    private fun getWalletsOfCurrentUser(userId: String) {
        userRepository.getUserWallets(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                {
                    filterWallet(it)?.let { it1 -> walletsTmp.addAll(it1) }
                    wallets.value = walletsTmp
                },
                {

                }
        )
    }

    /**
     * Filter current wallet out of list other wallet
     */
    private fun filterWallet(wallets: MutableList<Wallet>?) = wallets?.apply {
        remove(wallets.find {
            it.id == getCurrentWalletAddress()
        })
    }

    /**
     * Get current balance base on user id and wallet id
     */
    private fun getCurrentWalletInfor(idUser: String, idWallet: String) {
        // fix login state is true, need to be check loginState
        // and get current User id, current wallet id later
        if (true) {
            userRepository
                    .getWalletInfor(idUser, idWallet)
                    .subscribe(
                            {
                                currentWallet.value = it
                            },
                            {
                                notifyMessage.value = context
                                        .getString(R.string.error_message)
                                Log.e(TAG, it.toString())
                            }
                    )
        }
    }

    private fun getCurrentUserId() = SharedPreUtils.getUserId(context)

    private fun getCurrentWalletAddress() = SharedPreUtils.getCurrentWalletAddress(context)

    companion object {
        const val TAG = "WalletViewModel"
    }
}

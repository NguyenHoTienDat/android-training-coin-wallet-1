package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.ReceiveCoin
import com.framgia.bitcoinwallet.data.model.SendCoin
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.ui.screen.main.sendcointab.SendCoinViewModel
import com.framgia.bitcoinwallet.util.SharedPreUtils

class TransactionViewModel(private val context: Application,
                           private val userRepository: UserRepository)
    : AndroidViewModel(context), LifecycleObserver {

    companion object {
        const val TAG = "TransactionViewModel"
    }

    val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()
    val isSendTransactionShowed: MutableLiveData<Boolean> = MutableLiveData()
    val sendTransaction: MutableLiveData<List<SendCoin>> = MutableLiveData()
    val receiveTransaction: MutableLiveData<List<ReceiveCoin>> = MutableLiveData()

    //Don't get balance at this frg, because it is loaded in sendcoin frg and save in MainViewModel
    //So we only obsever that var in MainViewModel
    val curentBalance: MutableLiveData<String> = MutableLiveData()

    init {
        isLoadingData.value = true
        isSendTransactionShowed.value = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startViewModel() {
        getSendTransaction()
        getReceiveTransaction()
    }

    private fun getSendTransaction() {
        userRepository.getSendTransaction(SharedPreUtils.getUserId(context)
                , SharedPreUtils.getCurrentWalletAddress(context)).subscribe(
                {
                    it?.let {
                        Log.d(TAG, it.toString())
                        when (it.size) {
                            0 -> {
                            }
                            else -> {
                                sendTransaction.value = it
                                Log.d(TAG,"send "+it.toString())
                            }
                        }
                    }
                    isLoadingData.value = false
                },
                {
                    Log.e(TAG, it.toString())
                }
        )
    }

    private fun getReceiveTransaction() {
        userRepository.getReceiveTransaction(SharedPreUtils.getUserId(context)
                , SharedPreUtils.getCurrentWalletAddress(context)).subscribe(
                {
                    it?.let {
                        when (it.size) {
                            0 -> {
                            }
                            else -> {
                                receiveTransaction.value = it
                                Log.d(TAG,"receive "+it.toString())
                            }
                        }
                    }
                    isLoadingData.value = false
                },
                {
                    Log.e(TAG, it.toString())
                }
        )
    }
}
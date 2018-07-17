package com.framgia.bitcoinwallet.ui.screen.main.sendcointab

import android.app.Application
import android.arch.lifecycle.*
import android.text.TextUtils
import android.util.Log
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.QrCode
import com.framgia.bitcoinwallet.data.model.Receiver
import com.framgia.bitcoinwallet.data.model.SendCoin
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.ui.screen.main.MainViewModel
import com.framgia.bitcoinwallet.util.Constant
import com.framgia.bitcoinwallet.util.SharedPreUtils
import com.google.gson.Gson
import java.util.*

class SendCoinViewModel(private val context: Application,
                        private val userRepository: UserRepository)
    : AndroidViewModel(context), LifecycleObserver {

    var loginState: Boolean = false
    var sendPermission = true
    var balance = 0F
    lateinit var receiverInfor: Receiver
    val curentBalance: MutableLiveData<String> = MutableLiveData()
    val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()
    val notifyMessage: MutableLiveData<String> = MutableLiveData()
    val coinAddressValid: MutableLiveData<Boolean> = MutableLiveData()
    val amounValid: MutableLiveData<Boolean> = MutableLiveData()
    val showAlertDialog: MutableLiveData<Boolean> = MutableLiveData()
    val sendCoinState: MutableLiveData<Boolean> = MutableLiveData()
    val amountValue: MutableLiveData<String> = MutableLiveData()
    val addressCoinValue: MutableLiveData<String> = MutableLiveData()

    init {
        coinAddressValid.value = true
        amounValid.value = true
        isLoadingData.value = false
        loginState = SharedPreUtils.getLoginState(context)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startViewModel() {
        getCurrentBalance(getCurrentUserId(), getCurrentWalletAddress())
    }

    /**
     * Verify send information input
     */
    fun verifySendCoin(coinAddress: String, amount: String) {
        checkAddressCoinExist(coinAddress)
        checkSendAmount(amount)
        if (amounValid.value!! && coinAddressValid.value!! && sendPermission) {
            showAlertDialog.value = true
        }
    }

    /**
     * Send amount of coins to address receiver
     * No need to validate input here besause this fun is only called after validate successfully
     */
    fun sendCoin(coinAddress: String, amount: String, note: String) {
        var sendCoin = SendCoin(coinAddress)
        sendCoin.amount = amount.toFloat()
        sendCoin.note = note
        sendCoin.timestamp = Date().toString()

        val senderStringRef = "${Constant.FIREBASE_USER_REF_KEY}/${getCurrentUserId()}" +
                "/${Constant.FIREBASE_WALLET_REF_KEY}" +
                "/${getCurrentWalletAddress()}" +
                "/${Constant.FIREBASE_WALLET_COIN_KEY}"

        val receiverStringRef = "${Constant.FIREBASE_USER_REF_KEY}/${getReceiverUserId()}" +
                "/${Constant.FIREBASE_WALLET_REF_KEY}" +
                "/$coinAddress" +
                "/${Constant.FIREBASE_WALLET_COIN_KEY}"

        userRepository.sendCoin(sendCoin, receiverInfor, receiverStringRef
                , senderStringRef, balance).subscribe(
                {
                    sendCoinState.value = it
                    balance -= amount.toFloat()
                    curentBalance.value = balance.toString()
                },
                {
                    Log.e(TAG, it.toString())
                }
        )
    }

    fun handleScanResults(result: String) {
        val qrCode = Gson().fromJson(result, QrCode::class.java)
        qrCode?.let {
            addressCoinValue.value = it.address
            if (it.amount != null) amountValue.value = it.amount.toString()
        }
    }

    fun resetVariableState() {
        showAlertDialog.value = false
        sendPermission = false
    }

    /**
     * Check input receiver coin address is exist or not or duplicate with current address
     */
    private fun checkAddressCoinExist(addressCoin: String) {
        //check empty and duplicate address
        if (addressCoin != getCurrentWalletAddress() && addressCoin.isNotEmpty()) {
            userRepository.checkCoinAddressExist(addressCoin).subscribe(
                    {
                        coinAddressValid.value = !it.userReceiverId.isEmpty()
                        receiverInfor = it
                        sendPermission = true
                    },
                    {
                        Log.e(TAG, it.toString())
                    }
            )
        } else {
            coinAddressValid.value = false
        }
    }

    /**
     * Check amount of coin need to be send
     * False if larger than current balance
     */
    private fun checkSendAmount(amount: String) {
        curentBalance?.value?.let {
            amounValid.value = !(TextUtils.isEmpty(amount)
                    || amount.toFloat() == 0F
                    || amount.toFloat() > balance)
        }
    }

    /**
     * Get current balance base on user id and wallet id
     */
    private fun getCurrentBalance(idUser: String, idWallet: String) {
        // fix login state is true, need to be check loginState
        // and get current User id, current wallet id later
        if (true) {
            userRepository
                    .getCurrentBalance(idUser, idWallet)
                    .subscribe(
                            {
                                curentBalance.value = it.toString()
                                balance = it
                                isLoadingData.value = false
                            },
                            {
                                notifyMessage.value = context
                                        .getString(R.string.error_message)
                                Log.e(TAG, it.toString())
                            }
                    )
        }
    }

    private fun getCurrentUserId() =  SharedPreUtils.getUserId(context)

    private fun getCurrentWalletAddress() = SharedPreUtils.getCurrentWalletAddress(context)

    private fun getReceiverUserId() = receiverInfor.userReceiverId

    companion object {
        const val TAG = "SendCoinViewModel"
    }
}

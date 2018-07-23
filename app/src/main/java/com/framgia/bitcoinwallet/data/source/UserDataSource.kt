package com.framgia.bitcoinwallet.data.source

import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.data.network.message.CryptoResponse
import com.google.firebase.auth.AuthCredential
import io.reactivex.Observable
import io.reactivex.Single
import java.util.ArrayList

interface UserDataSource {
    fun singIn(email: String, password: String): Single<User>
    fun signUp(user: User?): Single<User>
    fun forgotPassword(email: String): Single<Boolean>
    fun getInforUser(idUser: String): Single<User>
    fun getCurrentUserId(): Single<String>
    fun getCurrentBalance(idUser: String, idWallet: String): Observable<Float>
    fun sendCoin(sendCoin: SendCoin, receiver: Receiver, receiverStringRef: String
                 , senderStringRef: String, currentBalance: Float): Single<Boolean>

    fun updateUserTransaction(stringRef: String)
    fun checkCoinAddressExist(addressCoin: String): Single<Receiver>
    fun getInforTransactionUser(idUser: String): Single<Transaction>
    fun getUserWallets(idUser: String): Single<MutableList<Wallet>>
    fun addWallet(idUser: String, walletName: String): Observable<Wallet>
    fun getWalletInfor(idUser: String, idWallet: String): Observable<Wallet>
    fun getCryptoPrice(fsyms: String, tsyms: String): Observable<MutableList<BitCoin>>
    fun changePassWord(newPassWd: String): Single<String>
    fun reAuth(authCredential: AuthCredential): Single<Boolean>
    fun getSendTransaction(idUser: String, walletId: String): Observable<List<Transaction>>
    fun getReceiveTransaction(idUser: String, walletId: String): Observable<List<Transaction>>
    fun findUserWithWalletAddress(walletId: String): Single<User>
    interface local {
        fun saveUser(user: User)
        fun updateUser(user: User)
    }
}

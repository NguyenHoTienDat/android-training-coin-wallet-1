package com.framgia.bitcoinwallet.data.source.repository

import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.data.network.message.CryptoResponse
import com.framgia.bitcoinwallet.data.source.UserDataSource
import com.framgia.bitcoinwallet.data.source.local.UserLocalDatasource
import com.framgia.bitcoinwallet.data.source.remote.UserRemoteDatasource
import com.google.firebase.auth.AuthCredential
import io.reactivex.Observable
import io.reactivex.Single
import java.util.ArrayList

class UserRepository(private val userRemoteDatasource: UserRemoteDatasource,
                     private val userLocalDatasource: UserLocalDatasource?) : UserDataSource {

    companion object {
        private var INSTANCE: UserRepository? = null
        @JvmStatic
        fun getInstance(userRemoteDatasource: UserRemoteDatasource,
                        userLocalDatasource: UserLocalDatasource?): UserRepository {
            if (INSTANCE == null) {
                INSTANCE = UserRepository(userRemoteDatasource, userLocalDatasource)
            }
            return INSTANCE!!
        }
    }

    override fun singIn(email: String, password: String): Single<User> {
        return userRemoteDatasource.singIn(email, password)
    }

    override fun signUp(user: User?): Single<User> {
        return userRemoteDatasource.signUp(user)
    }

    override fun forgotPassword(email: String): Single<Boolean> {
        return userRemoteDatasource.forgotPassword(email)
    }

    override fun getInforUser(idUser: String): Single<User> {
        return userRemoteDatasource.getInforUser(idUser)
    }

    override fun getInforTransactionUser(idUser: String): Single<Transaction> {
        return userRemoteDatasource.getInforTransactionUser(idUser)
    }

    override fun getCurrentUserId(): Single<String> {
        return userRemoteDatasource.getCurrentUserId()
    }

    override fun getCurrentBalance(idUser: String, idWallet: String): Observable<Float> {
        return userRemoteDatasource.getCurrentBalance(idUser, idWallet)
    }

    override fun checkCoinAddressExist(addressCoin: String): Single<Receiver> {
        return userRemoteDatasource.checkCoinAddressExist(addressCoin)
    }

    override fun sendCoin(sendCoin: SendCoin, receiver: Receiver, receiverStringRef: String,
                          senderStringRef: String, currentBalance: Float): Single<Boolean> {
        return userRemoteDatasource.sendCoin(sendCoin, receiver, receiverStringRef
                , senderStringRef, currentBalance)
    }

    override fun getUserWallets(idUser: String): Single<MutableList<Wallet>> {
        return userRemoteDatasource.getUserWallets(idUser)
    }

    override fun addWallet(idUser: String, walletName: String): Observable<Wallet> {
        return userRemoteDatasource.addWallet(idUser, walletName)
    }

    override fun getWalletInfor(idUser: String, idWallet: String): Observable<Wallet> {
        return userRemoteDatasource.getWalletInfor(idUser, idWallet)
    }

    override fun updateUserTransaction(stringRef: String) {

    }

    override fun getCryptoPrice(fsyms: String, tsyms: String): Observable<MutableList<BitCoin>> {
        return userRemoteDatasource.getCryptoPrice(fsyms, tsyms)
    }

    override fun changePassWord(newPassWd: String): Single<String> {
        return userRemoteDatasource.changePassWord(newPassWd)
    }

    override fun reAuth(authCredential: AuthCredential): Single<Boolean> {
        return userRemoteDatasource.reAuth(authCredential)
    }

    override fun getSendTransaction(idUser: String, walletId: String): Observable<List<SendCoin>> {
        return userRemoteDatasource.getSendTransaction(idUser, walletId)
    }

    override fun getReceiveTransaction(idUser: String, walletId: String): Observable<List<ReceiveCoin>> {
        return userRemoteDatasource.getReceiveTransaction(idUser, walletId)
    }
}

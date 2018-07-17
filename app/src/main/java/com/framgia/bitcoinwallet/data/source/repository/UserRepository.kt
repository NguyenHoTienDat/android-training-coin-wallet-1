package com.framgia.bitcoinwallet.data.source.repository

import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.data.source.UserDataSource
import com.framgia.bitcoinwallet.data.source.local.UserLocalDatasource
import com.framgia.bitcoinwallet.data.source.remote.UserRemoteDatasource
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

    override fun signUp(email: String, password: String): Single<User> {
        return userRemoteDatasource.signUp(email, password)
    }

    override fun forgotPassword(email: String): Single<Boolean> {
        return userRemoteDatasource.forgotPassword(email)
    }

    override fun getInforUser(idUser: String): Single<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInforTransactionUser(idUser: String): Single<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}

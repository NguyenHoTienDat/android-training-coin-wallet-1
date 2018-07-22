package com.framgia.bitcoinwallet.data.source.remote

import android.util.Log
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.data.model.Transaction
import com.framgia.bitcoinwallet.data.network.api.BitcoinApi
import com.framgia.bitcoinwallet.data.network.message.CryptoResponse
import com.framgia.bitcoinwallet.data.network.service.BitcoinService
import com.framgia.bitcoinwallet.data.source.UserDataSource
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinPriceViewModel.Companion.EUR_TYPE
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinPriceViewModel.Companion.JPY_TYPE
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinPriceViewModel.Companion.USD_TYPE
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinPriceViewModel.Companion.VND_TYPE
import com.framgia.bitcoinwallet.util.Constant
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserRemoteDatasource : UserDataSource {

    private var mAuth: FirebaseAuth
    private var mFireDatabase: FirebaseDatabase
    private var bitcoinApi: BitcoinApi

    private constructor(auth: FirebaseAuth, dataBase: FirebaseDatabase) {
        mAuth = auth
        mFireDatabase = dataBase
        bitcoinApi = BitcoinService.getBitcoinApiInstance()
    }

    companion object {
        private var INSTANCE: UserRemoteDatasource? = null
        @JvmStatic
        fun getInstance(mAuth: FirebaseAuth, dataBase: FirebaseDatabase): UserRemoteDatasource {
            if (INSTANCE == null) {
                INSTANCE = UserRemoteDatasource(mAuth, dataBase)
            }
            return INSTANCE!!
        }
    }

    override fun singIn(email: String, password: String): Single<User> {
        return Single.create<User> { e ->
            try {
                var user: User
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var userFirebase = mAuth.currentUser
                        user = User(userFirebase!!.uid, userFirebase.email!!)
                        e.onSuccess(user)
                    } else {
                        e.onSuccess(User())
                    }
                }
            } catch (ex: Exception) {
                e.onError(ex)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun forgotPassword(email: String): Single<Boolean> {
        return Single.create<Boolean>({ e ->
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                e.onSuccess(it.isSuccessful)
            }
                    .addOnFailureListener {

                    }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getInforUser(idUser: String): Single<User> {
        return Single.create<User> { emitter ->
            var userRef = mFireDatabase.getReference("${Constant.FIREBASE_USER_REF_KEY}/$idUser")
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    emitter.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.getValue(User::class.java)?.apply {
                        emitter.onSuccess(this)
                    }
                }

            })
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getInforTransactionUser(idUser: String): Single<Transaction> {
        return Single.create<Transaction>({}).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCurrentUserId(): Single<String> {
        return Single.create<String> { emitter ->
            mAuth?.currentUser?.uid?.let { emitter.onSuccess("32984732324324") }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCurrentBalance(idUser: String, idWallet: String): Observable<Float> {
        return Observable.create<Float> { emitter ->
            var ref: DatabaseReference =
                    mFireDatabase.getReference("${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                            "/${Constant.FIREBASE_WALLET_REF_KEY}/$idWallet")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

                override fun onDataChange(wallet: DataSnapshot) {
                    val walletReponse: Wallet? = wallet.getValue(Wallet::class.java)
                    walletReponse?.coin?.let { emitter.onNext(it) }
                }
            })
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * return UserId if has a addressCoin
     */
    override fun checkCoinAddressExist(addressCoin: String): Single<Receiver> {
        return Single.create<Receiver> { emitter ->
            var ref: DatabaseReference = mFireDatabase.reference.child(Constant.FIREBASE_USER_REF_KEY)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

                override fun onDataChange(userContain: DataSnapshot) {
                    var isRunContinue = true
                    for (user: DataSnapshot in userContain.children) {
                        if (!isRunContinue) break
                        for (wallet: DataSnapshot
                        in user.child(Constant.FIREBASE_WALLET_REF_KEY).children) {
                            if (!isRunContinue) break
                            if (wallet.key == addressCoin) {
                                isRunContinue = false
                                wallet?.getValue(Wallet::class.java)?.let {
                                    emitter.onSuccess(Receiver(user.key.toString()
                                            , it.coin))
                                }
                            }
                        }
                    }
                    if (isRunContinue) {
                        emitter.onSuccess(
                                Receiver("", 0F))
                    }
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun sendCoin(sendCoin: SendCoin, receiver: Receiver, receiverStringRef: String
                          , senderStringRef: String, currentBalance: Float): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            try {
                //update sender wallet
                var senderRef: DatabaseReference = mFireDatabase
                        .getReference(senderStringRef)
                senderRef.setValue(currentBalance - sendCoin.amount)

                //update receiver
                var receiverRef: DatabaseReference = mFireDatabase
                        .getReference(receiverStringRef)

                receiverRef.setValue(receiver.currentBalance + sendCoin.amount)
                emitter.onSuccess(true)

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUserWallets(idUser: String): Single<MutableList<Wallet>> {
        return Single.create<MutableList<Wallet>> {
            var walletRef = mFireDatabase.getReference(
                    "${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                            "/${Constant.FIREBASE_WALLET_REF_KEY}")
            walletRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(wallets: DataSnapshot) {
                    when (wallets.hasChildren()) {
                        true -> {
                            var walletRes = ArrayList<Wallet>()
                            for (wallet: DataSnapshot in wallets.children) {
                                walletRes.add(wallet.getValue(Wallet::class.java)!!.apply {
                                    id = wallet.key.toString()
                                })
                            }
                            it.onSuccess(walletRes)
                        }

                        else -> it.onSuccess(ArrayList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                }

            })
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addWallet(idUser: String, walletName: String): Observable<Wallet> {
        return Observable.create<Wallet> {
            try {
                var walletRef = mFireDatabase.getReference(
                        "${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                                "/${Constant.FIREBASE_WALLET_REF_KEY}")
                val newKey = walletRef.push()
                Wallet(newKey.toString(), 0F, Date().toString(), walletName).apply {
                    newKey.setValue(this)
                    it.onNext(this)
                }

            } catch (e: Exception) {
                it.onError(e)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getWalletInfor(idUser: String, idWallet: String): Observable<Wallet> {
        return Observable.create<Wallet> { emitter ->
            try {
                var walletRef = mFireDatabase.getReference(
                        "${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                                "/${Constant.FIREBASE_WALLET_REF_KEY}/$idWallet")
                walletRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.getValue(Wallet::class.java)?.apply {
                            id = p0.key.toString()
                            emitter.onNext(this)
                        }
                    }

                })

            } catch (e: Exception) {
                emitter.onError(e)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateUserTransaction(stringRef: String) {

    }

    override fun signUp(user: User?): Single<User> {
        return Single.create<User> { e ->
            mAuth.createUserWithEmailAndPassword(user?.email.toString(), user?.password.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    val userFirebase = mAuth.currentUser
                    user?.id = userFirebase?.uid
                    user?.let { it1 -> e.onSuccess(it1) }
                } else {
                    e.onError(it.exception!!)
                }
            }
        }.flatMap { user ->
            return@flatMap Single.create<User> { e ->
                mFireDatabase.reference.child("user").child(user.id.toString()).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        e.onSuccess(user)
                    } else {
                        e.onError(it.exception!!)
                    }
                }
            }
        }.flatMap { user ->
            return@flatMap Single.create<User> {
                val defaultWallet = mFireDatabase.reference.child("user").child(user.id.toString())
                        .child(Constant.FIREBASE_WALLET_REF_KEY).push()

                Wallet(defaultWallet.key.toString(), 50F, Date().toString(), "Default Wallet").apply {
                    defaultWallet.setValue(this)
                    it.onSuccess(user)
                }

            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCryptoPrice(fsyms: String, tsyms: String): Observable<MutableList<BitCoin>> {
        return bitcoinApi.getCryptoPrices(fsyms, tsyms)
                .flatMap { crypto ->
                    return@flatMap Observable.create<MutableList<BitCoin>> { emitter ->
                        emitter.onNext(getBitcoinPrices(crypto, tsyms))
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getBitcoinPrices(crypto: CryptoResponse, tsyms: String): MutableList<BitCoin> {
        var bitcoins: MutableList<BitCoin> = mutableListOf()
        when (tsyms) {
            USD_TYPE -> {
                bitcoins.apply {
                    add(BitCoin("", R.drawable.ic_bch, "BCH", "$ " + crypto.bCH.uSD))
                    add(BitCoin("", R.drawable.ic_btc, "BTC", "$ " + crypto.bTC.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_dash, "DASH", "$ " + crypto.dASH.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_etc, "ETC", "$ " + crypto.eTC.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_etn, "ETN", "$ " + crypto.eTN.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_ltc, "LTC", "$ " + crypto.lTC.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_xmr, "XMR", "$ " + crypto.xMR.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_xrp, "XRP", "$ " + crypto.xRP.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_zec, "ZEC", "$ " + crypto.zEC.uSD.toString()))
                    add(BitCoin("", R.drawable.ic_eth, "ETH", "$ " + crypto.eTH.uSD.toString()))
                }
            }
            VND_TYPE -> {
                bitcoins.apply {
                    add(BitCoin("", R.drawable.ic_bch, "BCH", "VND " + crypto.bCH.vND))
                    add(BitCoin("", R.drawable.ic_btc, "BTC", "VND " + crypto.bTC.vND.toString()))
                    add(BitCoin("", R.drawable.ic_dash, "DASH", "VND " + crypto.dASH.vND.toString()))
                    add(BitCoin("", R.drawable.ic_etc, "ETC", "VND " + crypto.eTC.vND.toString()))
                    add(BitCoin("", R.drawable.ic_etn, "ETN", "VND " + crypto.eTN.vND.toString()))
                    add(BitCoin("", R.drawable.ic_ltc, "LTC", "VND " + crypto.lTC.vND.toString()))
                    add(BitCoin("", R.drawable.ic_xmr, "XMR", "VND " + crypto.xMR.vND.toString()))
                    add(BitCoin("", R.drawable.ic_xrp, "XRP", "VND " + crypto.xRP.vND.toString()))
                    add(BitCoin("", R.drawable.ic_zec, "ZEC", "VND " + crypto.zEC.vND.toString()))
                    add(BitCoin("", R.drawable.ic_eth, "ETH", "VND " + crypto.eTH.vND.toString()))
                }
            }
            EUR_TYPE -> {
                bitcoins.apply {
                    add(BitCoin("", R.drawable.ic_bch, "BCH", "EUR " + crypto.bCH.eUR))
                    add(BitCoin("", R.drawable.ic_btc, "BTC", "EUR " + crypto.bTC.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_dash, "DASH", "EUR " + crypto.dASH.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_etc, "ETC", "EUR " + crypto.eTC.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_etn, "ETN", "EUR " + crypto.eTN.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_ltc, "LTC", "EUR " + crypto.lTC.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_xmr, "XMR", "EUR " + crypto.xMR.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_xrp, "XRP", "EUR " + crypto.xRP.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_zec, "ZEC", "EUR " + crypto.zEC.eUR.toString()))
                    add(BitCoin("", R.drawable.ic_eth, "ETH", "EUR " + crypto.eTH.eUR.toString()))
                }
            }
            JPY_TYPE -> {
                bitcoins.apply {
                    add(BitCoin("", R.drawable.ic_bch, "BCH", "JPY " + crypto.bCH.jPY))
                    add(BitCoin("", R.drawable.ic_btc, "BTC", "JPY " + crypto.bTC.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_dash, "DASH", "JPY " + crypto.dASH.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_etc, "ETC", "JPY " + crypto.eTC.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_etn, "ETN", "JPY " + crypto.eTN.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_ltc, "LTC", "JPY " + crypto.lTC.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_xmr, "XMR", "JPY " + crypto.xMR.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_xrp, "XRP", "JPY " + crypto.xRP.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_zec, "ZEC", "JPY " + crypto.zEC.jPY.toString()))
                    add(BitCoin("", R.drawable.ic_eth, "ETH", "JPY " + crypto.eTH.jPY.toString()))
                }
            }
        }

        return bitcoins
    }

    override fun changePassWord(newPassWd: String): Single<String> {
        return Single.create<String> { emitter ->
            mAuth.currentUser?.updatePassword(newPassWd)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onSuccess(newPassWd)
                        } else {
                            emitter.onSuccess("")
                        }
                    }
        }
    }

    override fun reAuth(authCredential: AuthCredential): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            mAuth.currentUser?.reauthenticate(authCredential)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onSuccess(true)
                        } else {
                            emitter.onSuccess(false)
                        }
                    }
        }
    }

    override fun getSendTransaction(idUser: String, idWallet: String): Observable<List<SendCoin>> {
        return Observable.create<List<SendCoin>> { emitter ->
            var transactionRef = mFireDatabase.getReference(
                    "${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                            "/${Constant.FIREBASE_WALLET_REF_KEY}/$idWallet" +
                            "/${Constant.FIREBASE_TRANSACTION_REF_KEY}")
            transactionRef.child(Constant.FIREBASE_SEND_REF_KEY)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            emitter.onError(error.toException())
                        }

                        override fun onDataChange(sendSnap: DataSnapshot) {
                            when (sendSnap.hasChildren()) {
                                true -> {
                                    var sendCoins = mutableListOf<SendCoin>()
                                    for (sendItem: DataSnapshot in sendSnap.children) {
                                        sendItem.getValue(SendCoin::class.java)?.let {
                                            sendCoins.add(it)
                                        }
                                    }
                                    emitter.onNext(sendCoins)
                                }
                                else -> {
                                    emitter.onNext(mutableListOf())
                                }
                            }
                        }
                    })
        }
    }

    override fun getReceiveTransaction(idUser: String, idWallet: String): Observable<List<ReceiveCoin>> {
        return Observable.create<List<ReceiveCoin>> { emitter ->
            var transactionRef = mFireDatabase.getReference(
                    "${Constant.FIREBASE_USER_REF_KEY}/$idUser" +
                            "/${Constant.FIREBASE_WALLET_REF_KEY}/$idWallet" +
                            "/${Constant.FIREBASE_TRANSACTION_REF_KEY}")
            transactionRef.child(Constant.FIREBASE_RECEIVE_REF_KEY)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            emitter.onError(error.toException())
                        }

                        override fun onDataChange(receiveSnap: DataSnapshot) {
                            when (receiveSnap.hasChildren()) {
                                true -> {
                                    var receiveCoins = mutableListOf<ReceiveCoin>()
                                    for (receiveItem: DataSnapshot in receiveSnap.children) {
                                        receiveItem.getValue(ReceiveCoin::class.java)?.let {
                                            receiveCoins.add(it)
                                        }
                                    }
                                    emitter.onNext(receiveCoins)
                                }
                                else -> {
                                    emitter.onNext( mutableListOf())
                                }
                            }
                        }
                    })
        }
    }
}

package com.framgia.bitcoinwallet.data.source.remote

import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.data.model.Transaction
import com.framgia.bitcoinwallet.data.source.UserDataSource
import com.framgia.bitcoinwallet.util.Constant
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

    private constructor(auth: FirebaseAuth, dataBase: FirebaseDatabase) {
        mAuth = auth
        mFireDatabase = dataBase
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInforTransactionUser(idUser: String): Single<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                Wallet(0F, Date().toString(), walletName).apply {
                    newKey.setValue(this)
                    id = newKey.toString()
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
                emitter
                        .onError(e)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateUserTransaction(stringRef: String) {

    }

    override fun signUp(email: String, password: String): Single<User> {
        return Single.create<User> { e ->
            var user: User
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val userFirebase = mAuth.currentUser
                    user = User(userFirebase?.uid, userFirebase?.email)
                    e.onSuccess(user)
                } else {
                    e.onError(it.exception!!)
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}

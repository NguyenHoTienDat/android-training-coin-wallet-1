package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.Transaction
import com.framgia.bitcoinwallet.data.model.User
import com.framgia.bitcoinwallet.data.source.local.UserLocalDatasource
import com.framgia.bitcoinwallet.data.source.remote.UserRemoteDatasource
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionAdapter.Companion.TRANSACTION_RECEIVE_TYPE
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionAdapter.Companion.TRANSACTION_SEND_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ItemTransactionViewModel(item: Transaction, transactionType: Int) {
    val transaction: MutableLiveData<Transaction> = MutableLiveData()
    val user: ObservableField<User> = ObservableField()
    val transactionImageRes: ObservableField<Int> = ObservableField()
    val isShowDetails: ObservableField<Boolean> = ObservableField()
    val isLoadDetails: ObservableField<Boolean> = ObservableField()
    lateinit var userRepository: UserRepository

    init {
        transaction.value = item
        userRepository = UserRepository.getInstance(
                UserRemoteDatasource.getInstance(FirebaseAuth.getInstance(),
                        FirebaseDatabase.getInstance()),
                UserLocalDatasource.getInstance())

        when (transactionType) {
            TRANSACTION_SEND_TYPE -> transactionImageRes.set(R.drawable.ic_send_purple)
            TRANSACTION_RECEIVE_TYPE -> transactionImageRes.set(R.drawable.ic_receive_purple)
        }
        getUserInfor()
    }

    fun getUserInfor() {
        transaction.value?.interactAddress?.let { walletId ->
            userRepository.findUserWithWalletAddress(walletId).subscribe(
                    {
                        it?.let {
                            user.set(it)
                        }
                    },
                    {

                    }
            )
        }
    }

    fun showDetails() {
       if (isShowDetails.get() == true) {
           isShowDetails.set(false)
       } else isShowDetails.set(true)
    }
}

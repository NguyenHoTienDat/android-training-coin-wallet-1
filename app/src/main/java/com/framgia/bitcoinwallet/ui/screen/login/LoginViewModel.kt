package com.framgia.bitcoinwallet.ui.screen.login

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.source.repository.UserRepository
import com.framgia.bitcoinwallet.ui.screen.main.MainActivity
import com.framgia.bitcoinwallet.util.SharedPreUtils

class LoginViewModel(private val context: Application, private val userRepository: UserRepository)
    : AndroidViewModel(context) {

    val notifyMessage: MutableLiveData<String> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    fun clickSignIn(email: String, passWd: String) {
        loading.value = true
        userRepository.singIn(email, passWd).subscribe({
            loading.value = false
            when (it.id) {
                null -> {
                    notifyMessage.value = context.getString(R.string.login_failed_title)
                }
                else -> {
                    SharedPreUtils.changeLoginState(context, true)
                    SharedPreUtils.saveUserId(context, it.id)
                    context.startActivity(MainActivity.getMainIntent(context))
                }
            }
        }) {
            loading.value = false
            notifyMessage.value = it.message
        }
    }
}
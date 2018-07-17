package com.framgia.bitcoinwallet.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object SharedPreUtils {

    fun getLoginState(context: Context): Boolean {
        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE)
        return sharedPref?.getBoolean(Constant.PREF_LOGIN_STATE_KEY, false)
    }

    fun changeLoginState(context: Context, state: Boolean) {
        var editor: SharedPreferences.Editor =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE).edit()

        with(editor) {
            putBoolean(Constant.PREF_LOGIN_STATE_KEY, state)
            commit()
        }
    }

    fun saveUserId(context: Context, id: String) {
        var editor: SharedPreferences.Editor =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE).edit()

        with(editor) {
            putString(Constant.PREF_USER_ID_KEY, id)
            commit()
        }
    }

    fun getUserId(context: Context): String {
        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE)
        return sharedPref?.getString(Constant.PREF_USER_ID_KEY, "")
    }

    fun saveWalletAddress(context: Context, address: String) {
        var editor: SharedPreferences.Editor =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE).edit()

        with(editor) {
            putString(Constant.PREF_WALLET_ADDRESS_KEY, address)
            commit()
        }
    }

    fun getCurrentWalletAddress(context: Context): String {
        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constant.PREF_AUTHENCIATION, MODE_PRIVATE)
        return sharedPref?.getString(Constant.PREF_WALLET_ADDRESS_KEY, "")
    }
}

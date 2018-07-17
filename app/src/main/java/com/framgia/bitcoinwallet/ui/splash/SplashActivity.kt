package com.framgia.bitcoinwallet.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.ui.BaseActivity
import com.framgia.bitcoinwallet.ui.screen.login.LoginActivity
import com.framgia.bitcoinwallet.ui.screen.main.MainActivity
import com.framgia.bitcoinwallet.util.SharedPreUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setEvents()
    }

    private fun setEvents() {
        Handler().postDelayed(Runnable {
            when (checkLoginState()) {
                true -> startMainScreen()
                else -> startLoginScreen()
            }
        }, WAITING_TIME)
    }

    /**
     * Check login state in SharedPreferences
     */
    private fun checkLoginState() = SharedPreUtils.getLoginState(this)

    /**
     * Get Intent from MainActivity and navigate to Home Screen
     */
    private fun startMainScreen() {
        startActivity(MainActivity.getMainIntent(this))
    }

    /**
     * Get Intent from LoginActivity and navigate to Login Screen
     */
    private fun startLoginScreen() {
        startActivity(LoginActivity.getLoginIntent(this))
    }

    companion object {
        private const val TAG = "SplashActivity"
        private const val WAITING_TIME: Long = 3000
    }
}

package com.framgia.bitcoinwallet.ui.screen.detailswallet

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.Wallet
import com.framgia.bitcoinwallet.databinding.ActivityDetailsWalletBinding
import com.framgia.bitcoinwallet.ui.BaseActivity
import com.framgia.bitcoinwallet.util.obtainViewModel

class DetailsWalletActivity : BaseActivity<ActivityDetailsWalletBinding>() {

    private lateinit var currentWallet: Wallet

    companion object {
        private const val TAG = "DetailsWalletActivity"
        private const val EXTRA_WALLET = "com.framgia.bitcoinwallet.ui.screen.detailswallet.EXTRA_WALLET"
        fun getDetailsWallet(context: Context, wallet: Wallet): Intent =
                Intent(context, DetailsWalletActivity::class.java).apply {
                    putExtra(EXTRA_WALLET, wallet)
                }
    }

    override fun navigateLayout(): Boolean {
        return false
    }

    override fun getLayoutRes() = R.layout.activity_details_wallet

    override fun initComponents() {
        getIntentData()
        initViewModel()
    }

    override fun setEvents() {

    }

    override fun observeViewModel() {

    }

    private fun getIntentData() {
        currentWallet = intent.getParcelableExtra(EXTRA_WALLET)
        notifyMessage(currentWallet.name)
    }

    private fun initViewModel() {
        binding.viewModel = this@DetailsWalletActivity.obtainViewModel(DetailsWalletViewModel::class.java).apply {
            lifecycle.addObserver(this)
        }
    }

    private fun notifyMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
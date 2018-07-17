package com.framgia.bitcoinwallet.ui.screen.login

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.databinding.ActivityLoginBinding
import com.framgia.bitcoinwallet.ui.BaseActivity
import com.framgia.bitcoinwallet.util.obtainViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun navigateLayout(): Boolean = false

    override fun getLayoutRes(): Int = R.layout.activity_login

    override fun initComponents() {
        binding.viewModel = this@LoginActivity.obtainViewModel(LoginViewModel::class.java)
    }

    override fun observeViewModel() {
        binding.viewModel?.notifyMessage?.observe(this, Observer {
            it?.let { it1 -> notify(it1) }
        })
    }

    private fun notify(mess: String) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }

    override fun setEvents() {
        btn_login.setOnClickListener {
            binding.viewModel?.clickSignIn(
                    edt_email.getText().toString(), edt_password.getText().toString()) }
    }

    companion object {
        fun getLoginIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}

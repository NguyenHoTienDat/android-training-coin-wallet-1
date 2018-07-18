package com.framgia.bitcoinwallet.ui.screen.login

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.databinding.ActivityLoginBinding
import com.framgia.bitcoinwallet.ui.BaseActivity
import com.framgia.bitcoinwallet.ui.screen.signup.SignUpActivity
import com.framgia.bitcoinwallet.util.obtainViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<ActivityLoginBinding>(), LoginActionListener {
    override fun onClickSignUp(view: View) {
        startActivity(SignUpActivity.getSignUpIntent(this))
    }

    override fun onClickLogin(view: View) {
        binding.viewModel?.email?.value = edt_email.getText().toString()
        binding.viewModel?.password?.value = edt_password.getText().toString()

        binding?.viewModel?.clickSignIn()
    }

    override fun navigateLayout(): Boolean = false

    override fun getLayoutRes(): Int = R.layout.activity_login

    override fun initComponents() {
//        binding.viewModel = this@LoginActivity.obtainViewModel(LoginViewModel::class.java)
        binding?.apply {
            viewModel = this@LoginActivity.obtainViewModel(LoginViewModel::class.java)
            listener = this@LoginActivity
        }
        binding?.viewModel?.notifyMessage?.observe(this, Observer {
            it?.let { it1 -> notify(it1) }
        })
    }

    override fun setEvents() {
    }

    override fun observeViewModel() {
        binding.viewModel?.notifyMessage?.observe(this, Observer {
            it?.let { it1 -> notify(it1) }
        })
    }

    private fun notify(mess: String) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getLoginIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}

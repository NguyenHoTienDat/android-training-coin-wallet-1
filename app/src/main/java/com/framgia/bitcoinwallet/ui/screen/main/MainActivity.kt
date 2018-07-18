package com.framgia.bitcoinwallet.ui.screen.main

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.databinding.ActivityMainBinding
import com.framgia.bitcoinwallet.ui.BaseActivity
import com.framgia.bitcoinwallet.ui.screen.login.LoginActivity
import com.framgia.bitcoinwallet.ui.screen.main.receivecointab.ReceiveFragment
import com.framgia.bitcoinwallet.ui.screen.main.sendcointab.SendCoinFragment
import com.framgia.bitcoinwallet.ui.screen.wallet.WalletActivity
import com.framgia.bitcoinwallet.util.SharedPreUtils
import com.framgia.bitcoinwallet.util.obtainViewModel
import com.framgia.bitcoinwallet.util.setUpActionBar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_drawer.view.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolBar: Toolbar
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onResume() {
        super.onResume()
        when (pager_main.currentItem) {
            TAB_TRANSACTION_POSITION -> navigationView.setCheckedItem(R.id.action_transaction)
            TAB_SEND_POSITION -> navigationView.setCheckedItem(R.id.action_send)
            TAB_RECEIVE_POSITION -> navigationView.setCheckedItem(R.id.action_receive)
        }
    }

    override fun navigateLayout(): Boolean {
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initComponents() {
        setUpToolbar()
        setUpNavigationDrawer()
        initMainViewModel()

        when (SharedPreUtils.getCurrentWalletAddress(this)) {
            EMPTY_STRING -> {
                binding.viewModel?.getDefaultWallet()
            }
            else -> {
                binding.viewModel?.dataLoading?.value = false
            }
        }
    }

    override fun setEvents() {
        drawerLayout.addDrawerListener(drawerToggle)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_receive -> pager_main.currentItem = TAB_RECEIVE_POSITION
                R.id.action_transaction -> pager_main.currentItem = TAB_TRANSACTION_POSITION
                R.id.action_send -> pager_main.currentItem = TAB_SEND_POSITION
                R.id.action_bitcoin_wallet -> startWalletActivity()
                R.id.action_log_out -> logOut()
            }
            item.isChecked = true
            drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

        pager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    TAB_TRANSACTION_POSITION -> navigationView.setCheckedItem(R.id.action_transaction)
                    TAB_SEND_POSITION -> navigationView.setCheckedItem(R.id.action_send)
                    TAB_RECEIVE_POSITION -> navigationView.setCheckedItem(R.id.action_receive)
                }
            }

        })
    }

    override fun observeViewModel() {
        binding.viewModel?.dataLoading?.observe(this, Observer {
            when (it) {
                false -> setUpViewPager()
            }
        })

        binding.viewModel?.user?.observe(this, Observer {
            when (it) {
                null -> {}
                else -> { it?.let {
                    drawerLayout.text_email.text = it.email
                    drawerLayout.text_name.text = it.fullName
                }}
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_pr_code ->
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        result?.contents?.let {
            binding.viewModel?.addressCoinScanned?.value = it
        }
    }

    private fun setUpViewPager() {
        findViewById<ViewPager>(R.id.pager_main).run {
            adapter = MainTabAdapter(supportFragmentManager, createTabFragment())
            offscreenPageLimit = TAB_OFF_SCREEN_AMOUNT
            currentItem = TAB_TRANSACTION_POSITION
        }
        tab_main.setupWithViewPager(pager_main)
    }

    /**
     * Create fragments for 3 tabs
     */
    private fun createTabFragment(): HashMap<String, Fragment> {
        return HashMap<String, Fragment>().apply {
            put(getString(R.string.title_transaction), Fragment())
            put(getString(R.string.title_receive), ReceiveFragment.newInstance())
            put(getString(R.string.title_send), SendCoinFragment.newInstance())
        }
    }

    private fun setUpToolbar() {
        toolBar = findViewById(R.id.toolbar_main)
        setUpActionBar(toolBar) {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpNavigationDrawer() {
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_main).apply {
            setStatusBarBackground(R.color.color_deep_purple_50_700)
        }
        drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolBar,
                R.string.open_drawer,
                R.string.close_drawer
        )
        navigationView = findViewById(R.id.navigation_view_main)
        drawerToggle.syncState()
        navigationView.setCheckedItem(R.id.action_transaction)
    }

    private fun initMainViewModel() {
        binding.viewModel = obtainViewModel(MainViewModel::class.java).apply {
            lifecycle.addObserver(this)
            dataLoading.value = true
        }
    }

    private fun startWalletActivity() {
        startActivity(WalletActivity.getWalletActivityIntent(this))
    }

    private fun startLoginActivity() {
        startActivity(LoginActivity.getLoginIntent(this))
    }

    private fun logOut() {
        if (SharedPreUtils.getLoginState(this)) {
            SharedPreUtils.changeLoginState(this, false)
            SharedPreUtils.saveUserId(this, EMPTY_STRING)
            SharedPreUtils.saveWalletAddress(this, EMPTY_STRING)
            startLoginActivity()
            finish()
        }
    }

    companion object {
        private const val EMPTY_STRING = ""
        fun getMainIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
        private const val TAB_OFF_SCREEN_AMOUNT = 3
        private const val TAB_RECEIVE_POSITION = 0
        private const val TAB_TRANSACTION_POSITION = 1
        private const val TAB_SEND_POSITION = 2
    }
}

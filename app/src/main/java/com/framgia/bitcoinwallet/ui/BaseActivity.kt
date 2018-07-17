package com.framgia.bitcoinwallet.ui

import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

/**
 * Created: 05/07/2018
 * By: Sang
 * Description:
 */
abstract class BaseActivity<ViewBinding : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (navigateLayout()) {
            return
        }
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
        binding.setLifecycleOwner(this@BaseActivity)
        initComponents()
        observeViewModel()
        setEvents()
    }

    abstract fun navigateLayout(): Boolean

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun initComponents()

    abstract fun setEvents()

    abstract fun observeViewModel()
}

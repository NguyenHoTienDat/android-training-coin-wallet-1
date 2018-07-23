package com.framgia.bitcoinwallet.util

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.*
import com.framgia.bitcoinwallet.ui.screen.coinprice.CoinAdapter
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionAdapter
import com.framgia.bitcoinwallet.ui.screen.wallet.WalletAdapter

/**
 * Created: 06/07/2018
 * By: Sang
 * Description:
 */

@BindingAdapter("imageResource")
fun setImageResource(view: ImageView, res: Int) {
    Glide.with(view.context).load(res).apply(RequestOptions()
            .placeholder(R.drawable.ic_error_gray)).into(view)
}

@BindingAdapter("imageBm")
fun setImageBm(view: ImageView, bitmap: Bitmap) {
    Glide.with(view.context).load(bitmap).into(view)
}

@BindingAdapter("walletItems")
fun setWalletItems(view: RecyclerView, item: MutableList<Wallet>?) {
    when (view.adapter) {
        is WalletAdapter -> item?.let {
            (view.adapter as WalletAdapter).updateData(it)
        }
    }
}

@BindingAdapter("coinItems")
fun setCoinItems(view: RecyclerView, item: MutableList<BitCoin>?) {
    when (view.adapter) {
        is CoinAdapter -> item?.let {
            (view.adapter as CoinAdapter).updateData(it)
        }
    }
}

@BindingAdapter("itemDivider")
fun setItemDivider(view: RecyclerView, state: Boolean) {
    when (state) {
        true -> {
            view.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        }
    }
}

@BindingAdapter("transactionItems")
fun setTransactionItems(view: RecyclerView, transaction: MutableList<Transaction>?) {
    when (view.adapter) {
        is TransactionAdapter -> transaction?.let {
            (view.adapter as TransactionAdapter).updateData(it)
        }
    }
}
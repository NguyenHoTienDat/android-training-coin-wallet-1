package com.framgia.bitcoinwallet.ui.screen.wallet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.framgia.bitcoinwallet.data.model.Wallet
import com.framgia.bitcoinwallet.databinding.ItemWalletBinding
import com.framgia.bitcoinwallet.ui.BaseRecyclerView
import com.framgia.bitcoinwallet.ui.BaseRecyclerViewHolder

class WalletAdapter(var wallets: MutableList<Wallet>,
                    val listener: BaseRecyclerViewHolder.OnItemClickListener<Wallet>)
    : BaseRecyclerView<WalletAdapter.WalletHolder, ItemWalletBinding, Wallet>(wallets) {

    private var previousChoosed: Int = -1
    private var isShowCheckUi: Boolean = false

    override fun getViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        return WalletHolder(ItemWalletBinding.inflate(LayoutInflater.from(parent.context)
                , parent, false))
    }

    fun notifyPreviousItemCheck(currentPosistion: Int) {
        if (previousChoosed != -1) {
            notifyItemChanged(previousChoosed)
        }
        previousChoosed = currentPosistion
    }

    fun showCheckBoxChoose(isShow: Boolean) {
        isShowCheckUi = isShow
        notifyDataSetChanged()
    }

    fun updateData(newWallets: MutableList<Wallet>) {
        Log.e("xxx","before: "+newWallets.size)
        wallets.clear()
        wallets.addAll(newWallets)
        Log.e("xxx","after: "+newWallets.size)
        isShowCheckUi = false
        notifyDataSetChanged()
    }

    inner class WalletHolder(binding: ItemWalletBinding)
        : BaseRecyclerViewHolder<ItemWalletBinding, Wallet>(binding) {
        override fun bindData(item: Wallet) {
            binding.apply {
                viewModel = ItemWalletViewModel(item)
                viewModel?.isShowCheckUi?.value = isShowCheckUi
                executePendingBindings()
                root.setOnClickListener {
                    listener.onItemClick(adapterPosition, item)
                }
                checkboxItemWallet.setOnClickListener {
                    listener.onItemClick(adapterPosition, item)
                }
                if (previousChoosed != -1 && adapterPosition == previousChoosed) {
                    item.isChoosed = false
                }
            }

        }
    }
}

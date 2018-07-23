package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.framgia.bitcoinwallet.data.model.ReceiveCoin
import com.framgia.bitcoinwallet.databinding.ItemReceiveTransactionBinding
import com.framgia.bitcoinwallet.ui.BaseRecyclerView
import com.framgia.bitcoinwallet.ui.BaseRecyclerViewHolder

class ReceiveTransactionAdapter(var receiveTransaction: MutableList<ReceiveCoin>,
                                val listener: BaseRecyclerViewHolder.OnItemClickListener<ReceiveCoin>)
    : BaseRecyclerView<ReceiveTransactionAdapter.ReceiveTransactionHolder, ItemReceiveTransactionBinding, ReceiveCoin>(receiveTransaction) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ReceiveTransactionHolder {
        return ReceiveTransactionHolder(ItemReceiveTransactionBinding.inflate(LayoutInflater.from(parent.context)
                , parent, false))
    }

    fun updateData(newReceiveTransactions: MutableList<ReceiveCoin>) {
        receiveTransaction.apply {
            clear()
            addAll(newReceiveTransactions)
        }
        notifyDataSetChanged()
    }

    inner class ReceiveTransactionHolder(binding: ItemReceiveTransactionBinding)
        : BaseRecyclerViewHolder<ItemReceiveTransactionBinding, ReceiveCoin>(binding) {
        override fun bindData(item: ReceiveCoin) {
            binding.apply {
                viewModel = ReceiveTransactionViewModel(item)
                executePendingBindings()
                root.setOnClickListener {
                    listener.onItemClick(adapterPosition, item)
                }
            }
        }

    }
}

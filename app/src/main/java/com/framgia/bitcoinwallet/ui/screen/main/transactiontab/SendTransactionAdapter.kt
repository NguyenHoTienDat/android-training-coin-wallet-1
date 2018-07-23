package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.framgia.bitcoinwallet.data.model.SendCoin
import com.framgia.bitcoinwallet.databinding.ItemSendTransactionBinding
import com.framgia.bitcoinwallet.ui.BaseRecyclerView
import com.framgia.bitcoinwallet.ui.BaseRecyclerViewHolder

class SendTransactionAdapter(var sendTransaction: MutableList<SendCoin>,
                             val listener: BaseRecyclerViewHolder.OnItemClickListener<SendCoin>)
    : BaseRecyclerView<SendTransactionAdapter.SendTransactionHolder, ItemSendTransactionBinding, SendCoin>(sendTransaction) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): SendTransactionHolder {
        return SendTransactionHolder(ItemSendTransactionBinding.inflate(LayoutInflater.from(parent.context)
                , parent, false))
    }

    fun updateData(newSendTransactions: MutableList<SendCoin>) {
        sendTransaction.apply {
            clear()
            addAll(newSendTransactions)
        }
        notifyDataSetChanged()
    }

    inner class SendTransactionHolder(binding: ItemSendTransactionBinding)
        : BaseRecyclerViewHolder<ItemSendTransactionBinding, SendCoin>(binding) {
        override fun bindData(item: SendCoin) {
            binding.apply {
                viewModel = SendTransactionViewModel(item)
                executePendingBindings()
                root.setOnClickListener {
                    listener.onItemClick(adapterPosition, item)
                }
            }
        }

    }
}

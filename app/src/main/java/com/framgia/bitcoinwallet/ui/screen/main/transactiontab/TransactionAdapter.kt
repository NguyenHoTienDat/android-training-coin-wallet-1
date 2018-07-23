package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.view.LayoutInflater
import android.view.ViewGroup
import com.framgia.bitcoinwallet.R
import com.framgia.bitcoinwallet.data.model.Transaction
import com.framgia.bitcoinwallet.databinding.ItemTransactionBinding
import com.framgia.bitcoinwallet.ui.BaseRecyclerView
import com.framgia.bitcoinwallet.ui.BaseRecyclerViewHolder

class TransactionAdapter(var transaction: MutableList<Transaction>, val transactionType: Int,
                         val listener: BaseRecyclerViewHolder.OnItemClickListener<Transaction>)
    : BaseRecyclerView<TransactionAdapter.TransactionHolder, ItemTransactionBinding, Transaction>(transaction) {

    companion object {
        const val TRANSACTION_SEND_TYPE = 0
        const val TRANSACTION_RECEIVE_TYPE = 1
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        return TransactionHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context)
                , parent, false))
    }

    fun updateData(newTransactions: MutableList<Transaction>) {
        transaction.apply {
            clear()
            addAll(newTransactions)
        }
        notifyDataSetChanged()
    }

    inner class TransactionHolder(binding: ItemTransactionBinding)
        : BaseRecyclerViewHolder<ItemTransactionBinding, Transaction>(binding) {
        override fun bindData(item: Transaction) {
            binding.apply {
                viewModel = ItemTransactionViewModel(item, transactionType)
                executePendingBindings()
                root.setOnClickListener {
                    viewModel?.let { it.showDetails() }
                }
                imageCopy.setOnClickListener { listener.onItemClick(adapterPosition, item) }
            }
        }

    }
}

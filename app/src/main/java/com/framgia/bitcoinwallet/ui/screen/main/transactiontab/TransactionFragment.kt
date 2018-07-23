package com.framgia.bitcoinwallet.ui.screen.main.transactiontab

import android.arch.lifecycle.Observer
import android.view.View
import android.widget.Toast

import com.framgia.bitcoinwallet.data.model.Transaction
import com.framgia.bitcoinwallet.databinding.FragmentTransactionBinding
import com.framgia.bitcoinwallet.ui.BaseFragment
import com.framgia.bitcoinwallet.ui.BaseRecyclerViewHolder
import com.framgia.bitcoinwallet.ui.screen.main.MainActivity
import com.framgia.bitcoinwallet.ui.screen.main.MainViewModel
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionAdapter.Companion.TRANSACTION_RECEIVE_TYPE
import com.framgia.bitcoinwallet.ui.screen.main.transactiontab.TransactionAdapter.Companion.TRANSACTION_SEND_TYPE
import com.framgia.bitcoinwallet.util.obtainViewModel
import kotlinx.android.synthetic.main.fragment_transaction.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.framgia.bitcoinwallet.R

class TransactionFragment : BaseFragment<FragmentTransactionBinding>() {

    private lateinit var mainViewModel: MainViewModel

    companion object {
        fun newInstance() = TransactionFragment()
        private const val TAG = "TransactionFragment"
        private const val CLIPBOARD_LABLE = "CLIPBOARD_LABLE"
    }

    override fun getLayoutRes() = R.layout.fragment_transaction

    override fun initData() {
        viewDataBinding.apply {
            viewModel = (activity as MainActivity).obtainViewModel(TransactionViewModel::class.java)
            viewModel?.let { lifecycle.addObserver(it) }
        }

        mainViewModel = (activity as MainActivity).obtainViewModel(MainViewModel::class.java)

        recycler_send_transaction.adapter =
                TransactionAdapter(mutableListOf(), TRANSACTION_SEND_TYPE,
                        object : BaseRecyclerViewHolder.OnItemClickListener<Transaction> {
                            override fun onItemClick(position: Int, data: Transaction) {
                                copyQrCodeToClipBoar(data.interactAddress)
                            }

                        })
        recycler_receive_transaction.adapter =
                TransactionAdapter(mutableListOf(), TRANSACTION_RECEIVE_TYPE,
                        object : BaseRecyclerViewHolder.OnItemClickListener<Transaction> {
                            override fun onItemClick(position: Int, data: Transaction) {
                                copyQrCodeToClipBoar(data.interactAddress)
                            }

                        })
    }

    private fun copyQrCodeToClipBoar(interactAddress: String) {
        val clipboard = (activity as MainActivity).getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager
        val clip = ClipData.newPlainText(CLIPBOARD_LABLE, interactAddress)
        clipboard.primaryClip = clip
        notifyMessage(resources.getString(R.string.copy_qr_successed_title))
    }

    override fun observeModelData(view: View) {
        //Listen balance in MainViewModel ( which is set value by SendCoinFrg )
        //So we do not load this value again
        mainViewModel.currentBalance.observe(this, Observer {
            viewDataBinding.viewModel?.curentBalance?.value = it
        })

        viewDataBinding.viewModel?.isSendTransactionShowed?.observe(this, Observer {
            it?.let {
                when (it) {
                    true -> {
                        showSendTransaction()
                    }
                    else -> {
                        showReceiveTransaction()
                    }
                }
            }
        })
    }

    override fun setEvents(view: View) {
        text_received_title.setOnClickListener {
            viewDataBinding.viewModel?.showReceiveTransaction()
        }
        text_sended_title.setOnClickListener {
            viewDataBinding.viewModel?.showSendTransaction()
        }
    }

    private fun notifyMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSendTransaction() {
        text_sended_title.setTextColor(resources.getColor(R.color.color_deep_purple_50_700))
        view_under_sened_title.setBackgroundColor(resources.getColor(R.color.color_deep_purple_50_700))
        text_received_title.setTextColor(resources.getColor(android.R.color.darker_gray))
        view_under_received_title.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }

    private fun showReceiveTransaction() {
        text_received_title.setTextColor(resources.getColor(R.color.color_deep_purple_50_700))
        view_under_received_title.setBackgroundColor(resources.getColor(R.color.color_deep_purple_50_700))
        text_sended_title.setTextColor(resources.getColor(android.R.color.darker_gray))
        view_under_sened_title.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }
}
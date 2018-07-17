package com.framgia.bitcoinwallet.data.model

data class User(val id: String? = null, var email: String? = "", var fullName: String? = "",
                var phoneNumber: String? = "",
                var wallet: List<Wallet>? = null,
                var transaction: Transaction? = null)
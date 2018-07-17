package com.framgia.bitcoinwallet.data.model

import android.os.Parcel
import android.os.Parcelable

class Wallet(var coin: Float, var createAt: String, var name: String): Parcelable {

    constructor() : this(0f,"","Personal Wallet")
    var id: String = ""
    var isChoosed: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readString(),
            parcel.readString()) {
        id = parcel.readString()
        isChoosed = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.run {
            writeFloat(coin)
            writeString(createAt)
            writeString(name)
            writeString(id)
            writeByte(if (isChoosed) 1 else 0)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Wallet> {
        override fun createFromParcel(parcel: Parcel): Wallet {
            return Wallet(parcel)
        }

        override fun newArray(size: Int): Array<Wallet?> {
            return arrayOfNulls(size)
        }
    }
}

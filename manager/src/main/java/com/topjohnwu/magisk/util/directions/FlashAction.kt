package com.topjohnwu.magisk.util.directions

import android.os.Parcel
import android.os.Parcelable


enum class FlashAction : Parcelable {

    FLASH_ZIP, FLASH_MAGISK, FLASH_INACTIVE_SLOT,
    PATCH_BOOT,
    UNINSTALL;

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlashAction> {
        override fun createFromParcel(parcel: Parcel): FlashAction {
            return FlashAction.valueOf(parcel.readString())
        }

        override fun newArray(size: Int): Array<FlashAction?> {
            return arrayOfNulls(size)
        }
    }
}

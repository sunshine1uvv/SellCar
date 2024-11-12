package com.example.sellcar.data

import android.os.Parcel
import android.os.Parcelable

class NativeLib {
    companion object {
        private var isLibraryLoaded = false

        init {
            try {
                System.loadLibrary("native-lib")
                isLibraryLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                isLibraryLoaded = false
            }
        }
    }

    private external fun functionC(input: String): String

    private fun functionK(input: String): String {
        val result = StringBuilder()
        result.append("I'm from Kotlin)\n ")
        result.append(input)
        return result.toString()
    }

    fun getMessage(message: String): String {
        return if (isLibraryLoaded) {
            functionC(message)
        } else {
            functionK(message)
        }
    }
}

data class Auto(
    var id: String = "",
    var userId: String = "",
    var brand: String = "",
    var model: String = "",
    var generation: String = "",
    var year: Int = 0,
    var carBody: String = "",
    var transmission: String = "",
    var state: String = "",
    var price: Float = 0.0f,
    var imageUrls: ArrayList<String> = arrayListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(brand)
        parcel.writeString(model)
        parcel.writeString(generation)
        parcel.writeInt(year)
        parcel.writeString(carBody)
        parcel.writeString(transmission)
        parcel.writeString(state)
        parcel.writeFloat(price)
        parcel.writeStringList(imageUrls)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Auto> {
        override fun createFromParcel(parcel: Parcel): Auto {
            return Auto(parcel)
        }

        override fun newArray(size: Int): Array<Auto?> {
            return arrayOfNulls(size)
        }
    }

    fun isEmpty(): Boolean {
        return id.isEmpty() && brand.isEmpty() && model.isEmpty() && generation.isEmpty() &&
                year == 0 && carBody.isEmpty() &&
                transmission.isEmpty() && state.isEmpty() && price == 0.0f && imageUrls.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return  brand.isNotEmpty() && model.isNotEmpty() && generation.isNotEmpty() &&
                year != 0 && carBody.isNotEmpty() &&
                transmission.isNotEmpty() && state.isNotEmpty() && price != 0.0f
    }
}
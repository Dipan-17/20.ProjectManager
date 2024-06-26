package dipan.ProjectManagement.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title:String="",
    val createdBy:String="",
    var card:ArrayList<Card> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    ) {
    }

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(card)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
package dipan.ProjectManagement.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    val name: String,
    val createdBy: String,
    val assignedTo: ArrayList<String>, //user id only
    var labelColor: String = "#FFFFFF",
    var dueDate: Long = 0
): Parcelable {
    constructor() : this("", "", ArrayList())
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(labelColor)
        parcel.writeLong(dueDate)
    }

    override fun describeContents()=0

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
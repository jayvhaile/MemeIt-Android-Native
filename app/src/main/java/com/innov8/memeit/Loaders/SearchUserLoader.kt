package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.User

class SearchUserLoader() : UserListLoader {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        username = parcel.readString()
    }

    var name: String? = null
    var username: String? = null

    var joinByAnd = false


    override fun load(limit: Int, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.searchUser(name, username, joinByAnd, skip, limit).call(onSuccess, onError)

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowerLoader> {
        override fun createFromParcel(parcel: Parcel): FollowerLoader {
            return FollowerLoader(parcel)
        }

        override fun newArray(size: Int): Array<FollowerLoader?> {
            return arrayOfNulls(size)
        }
    }
}
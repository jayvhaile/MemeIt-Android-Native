package com.innov8.memeit.CustomClasses

import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.User
import com.memeit.backend.utilis.OnCompleteListener

interface UserListLoader {
    companion object {
        const val FOLLOWER_LOADER: Byte = 1
        const val FOLLOWING_LOADER: Byte = 2
        fun create(type: Byte, uid: String? = null):UserListLoader? =
                when (type) {
                    FOLLOWER_LOADER ->FollowerLoader(uid)
                    FOLLOWING_LOADER -> FollowingLoader(uid)
                    else -> null
                }
    }

    var listener: OnCompleteListener<List<User>>?

    fun load(skip: Int, limit: Int)
    fun reset() {}
}

class FollowerLoader(val uid: String?, override var listener: OnCompleteListener<List<User>>? = null) : UserListLoader {
    override fun load(skip: Int, limit: Int) {
        if (uid == null)
            MemeItUsers.getInstance().getMyFollowerList(skip, limit, listener)
        else
            MemeItUsers.getInstance().getFollowerListFor(uid, skip, limit, listener)
    }
}

class FollowingLoader(val uid: String?, override var listener: OnCompleteListener<List<User>>? = null) : UserListLoader {
    override fun load(skip: Int, limit: Int) {
        if (uid == null)
            MemeItUsers.getInstance().getMyFollowingList(skip, limit, listener)
        else
            MemeItUsers.getInstance().getFollowingListFor(uid, skip, limit, listener)
    }
}


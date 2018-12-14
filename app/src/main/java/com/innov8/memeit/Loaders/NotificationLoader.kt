package com.innov8.memeit.Loaders

import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Notification

class MyNotificationLoader() : Loader<Notification> {
    override var skip: Int = 0


    override fun load(limit: Int, onSuccess: (List<Notification>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getMyNotifications(skip, limit).call({
            onSuccess(it.map { n -> Notification.parseNotif(n) })
        }, onError)
    }




}
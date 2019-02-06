package com.innov8.memeit.loaders

import com.innov8.memeit.commons.Loader
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Reply

class ReplyLoader(val commentID: String) : Loader<Reply> {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Reply>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getRepliesForComment(commentID, skip, limit).call(onSuccess, onError)
    }
}
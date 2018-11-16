package com.innov8.memeit.Loaders

import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Comment

class CommentLoader(var memeId: String) : Loader<Comment> {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Comment>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getCommentForMeme(memeId, skip, limit).call(onSuccess, onError)
    }
}
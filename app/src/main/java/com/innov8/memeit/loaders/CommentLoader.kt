package com.innov8.memeit.loaders

import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Comment

class CommentLoader(var memeId: String) : Loader<Comment> {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Comment>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getCommentForMeme(memeId, skip, limit).call(onSuccess, onError)
    }
}

class TestCommentLoader(var memeId: String) : Loader<Comment> {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Comment>) -> Unit, onError: (String) -> Unit) {
        onSuccess(listOf())
    }
}

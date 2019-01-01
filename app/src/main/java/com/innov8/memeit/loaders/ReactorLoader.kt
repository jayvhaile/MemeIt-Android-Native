package com.innov8.memeit.loaders

import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Reaction

class ReactorLoader(val mid: String) : Loader<Reaction> {


    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Reaction>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getReactorsForMeme(mid, skip, limit).call(onSuccess, onError)
    }
}
package com.innov8.memeit.Fragments


import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memeit.Activities.AuthActivity

import androidx.fragment.app.Fragment

open class AuthFragment : Fragment() {
    val onError={message:String->
        setLoading(false)
        authActivity.showError(message)
    }
    protected lateinit var actionButton: CircularProgressButton

    protected val authActivity: AuthActivity
        get() = activity as AuthActivity
    protected var mLoading: Boolean = false
    protected fun setLoading(loading: Boolean) {
        mLoading = loading
        if (loading) {
            actionButton!!.startAnimation()
        } else {
            actionButton!!.revertAnimation()

        }
    }
}

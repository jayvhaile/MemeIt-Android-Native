package com.innov8.memeit.commons.views

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memeit.commons.BuildConfig
import com.innov8.memeit.commons.R
import com.innov8.memeit.commons.dp
import java.time.LocalDateTime

class ProfileDraweeView : SimpleDraweeView {
    constructor(context: Context, text: String = "", color: Int = Color.RED) : super(context) {
        this.text = text
        this.color = color
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileDraweeView, 0, 0)
        try {
            hasBorder = a.getBoolean(R.styleable.ProfileDraweeView_hasBorder, true)
        } finally {
            a.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }


    private lateinit var textDrawable: TextDrawable
    private var text: String = ""
    private var color: Int = Color.RED

    fun setText(t: String) {
        this.text = t
        init()
    }

    var hasBorder = true

    fun init() {
        hierarchy.roundingParams = RoundingParams.asCircle().apply {
            if (hasBorder) setBorder(Color.WHITE, 3f.dp(context))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            textDrawable = TextDrawable
                    .builder()
                    .beginConfig()
                    .bold()
                    .endConfig()
                    .buildRound(text, color)
            hierarchy.setPlaceholderImage(textDrawable)
        } else {
            hierarchy.setPlaceholderImage(R.drawable.circle_user_color)
        }

    }
}
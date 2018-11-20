package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.animation.drawable.AnimatedDrawable2
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memeit.*
import com.innov8.memeit.Activities.SettingsActivity
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.loadBitmap
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Meme.MemeType
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

private const val STATE_NONE = -1
private const val STATE_LOADING = 0
private const val STATE_FAILED = 1
private const val STATE_LOADED = 2
private const val STATE_GIF_THUMB_LOADING = 3
private const val STATE_GIF_THUMB_LOADED = 4
private const val STATE_GIF_PAUSED = 6

class MemeDraweeView : SimpleDraweeView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var state: Int = STATE_LOADING
        set(value) {
            field = value
            invalidate()
        }
    var onClick: (() -> Unit)? = null
    private val level by lazy { SettingsActivity.getImageQualityLevel(context) }
    private val quality by lazy { SettingsActivity.quality[level] }
    private val fac by lazy { SettingsActivity.factor[level] }
    private val autoLoadGif by lazy { SettingsActivity.autoLoadGifs(context) }
    private val lowSize = 80
    private val lowQuality = 10
    private val blurSize = 60

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 0, 0, 0)
    }
    private val dsize = 50f.dp(context)
    private val playDrawable by lazy { context.loadBitmap(R.drawable.ic_play, 1f) }
    private val downloadDrawable by lazy { context.loadBitmap(R.drawable.ic_download, 1f) }

    private val rectF = RectF()

    var autoPlayGif = true

    init {
        val textDrawable = TextDrawable
                .builder()
                .beginConfig()
                .bold()
                .textColor(Color.GRAY)
                .fontSize(14.sp)
                .endConfig()
        val r = "Loading Meme Failed. Tap to Retry"
        val f = "Loading Meme Failed"
        val retry = textDrawable.buildRect(r, Color.TRANSPARENT)
        val failed = textDrawable.buildRect(f, Color.TRANSPARENT)


        controller = Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .build()
        hierarchy.setRetryImage(retry)
        hierarchy.setFailureImage(failed)
        hierarchy.setProgressBarImage(LoadingDrawable(context))
        setOnClickListener {
            when (state) {
                STATE_LOADED -> {
                    if (type == MemeType.IMAGE) onClick?.invoke()
                    else pauseGif()
                }
                STATE_GIF_THUMB_LOADED -> loadGifWithThumb()
                STATE_GIF_PAUSED -> playGif()
                STATE_FAILED -> load()
            }
        }
    }

    private fun pauseGif() {
        anim?.stop()
        state = STATE_GIF_PAUSED
    }

    private fun playGif() {
        anim?.start()
        state = STATE_LOADED
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.left = width / 2 - dsize / 2
        rectF.top = height / 2 - dsize / 2
        rectF.right = rectF.left + dsize
        rectF.bottom = rectF.top + dsize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (state) {
            STATE_GIF_THUMB_LOADING, STATE_GIF_THUMB_LOADED -> {
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                canvas.drawBitmap(downloadDrawable, null, rectF, null)
            }
            STATE_GIF_PAUSED -> {
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                canvas.drawBitmap(playDrawable, null, rectF, null)
            }
        }
    }

    private var mid: String = ""
    private var imageId: String = ""
    private var ratio: Float = 1f
    private var type: MemeType = MemeType.IMAGE
    private var resizeWidth = 0
    private var resizeHeight = 0

    private var anim: AnimatedDrawable2? = null

    inner class CL(val iid: String) : BaseControllerListener<ImageInfo>() {
        override fun onFailure(id: String?, throwable: Throwable?) {
            if (imageId == iid) state = STATE_FAILED
        }

        override fun onRelease(id: String?) {
            if (imageId == iid) state = STATE_NONE
            anim = null
        }

        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            if (imageId == iid) {
                state = if (state == STATE_GIF_THUMB_LOADING)
                    STATE_GIF_THUMB_LOADED
                else
                    STATE_LOADED
                if (animatable != null) {
                    val a = animatable as AnimatedDrawable2
                    anim = a
                    if (autoPlayGif) a.start()
                    else state = STATE_GIF_PAUSED
                }
            }
        }
    }

    fun loadMeme(meme: Meme, resizeWidth: Int = 0, resizeHeight: Int = resizeWidth) {
        mid = meme.id!!
        imageId = meme.imageId!!
        ratio = meme.imageRatio.toFloat()
        type = meme.getType()
        this.resizeWidth = resizeWidth
        this.resizeHeight = resizeHeight
        //l8ggt9gesqesqxbpsfahmze 0.82129276
        load()
    }

    private fun load() {
        state = STATE_LOADING
        if (type == MemeType.IMAGE) {
            loadImageMeme()
        } else {
            loadGifMeme()
        }
    }


    private fun loadImageMeme() {
        val low = "http://res.cloudinary.com/innov8/image/upload/c_fit,e_blur:$blurSize,h_$lowSize,q_$lowQuality,w_$lowSize/$id"
        val high = getImageMemeUrl(imageId, ratio)
        val lowReq = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(low))
                .setPostprocessor(IterativeBoxBlurPostProcessor(2))
                .build()
        var req = ImageRequest.fromUri(high)
        if (resizeWidth > 0 && resizeHeight > 0)
            req = ImageRequestBuilder.fromRequest(req)
                    .setResizeOptions(ResizeOptions.forDimensions(resizeWidth, resizeHeight))
                    .build()
        val c = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(lowReq)
                .setImageRequest(req)
                .setControllerListener(CL(imageId))
                .setOldController(controller)
                .build()
        controller = c
    }

    private fun setReq(req: ImageRequest) {
        controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(req)
                .setControllerListener(CL(imageId))
                .setOldController(controller)
                .build()
    }

    private fun loadGifMeme() {
        val gifUrl = getGifMemeUrl(imageId, ratio)
        val gifDownloaded = Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(gifUrl))
        if (gifDownloaded) {
            setReq(makeHighResGifReq(gifUrl, resizeWidth, resizeHeight))
        } else {
            state = STATE_GIF_THUMB_LOADING
            setReq(makeLowResGifReq(imageId))
            Fresco.getImagePipeline().isInDiskCache(Uri.parse(gifUrl)).subscribe(object : DataSubscriber<Boolean?> {
                override fun onFailure(dataSource: DataSource<Boolean?>) {
                }

                override fun onCancellation(dataSource: DataSource<Boolean?>) {
                }

                override fun onProgressUpdate(dataSource: DataSource<Boolean?>) {

                }

                override fun onNewResult(dataSource: DataSource<Boolean?>) {
                    if (dataSource.result == true) {
                        launch(UI) {
                            state = STATE_LOADING
                            loadGifWithThumb(gifUrl)
                        }
                    } else {
                        if (autoLoadGif) {
                            state = STATE_LOADING
                            loadGifWithThumb(gifUrl)
                        }
                    }
                }
            }, CallerThreadExecutor.getInstance())

        }
    }

    private fun loadGifWithThumb(gifUrl: String = getGifMemeUrl(imageId, ratio)) {
        controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(makeLowResGifReq(imageId))
                .setImageRequest(makeHighResGifReq(gifUrl, resizeWidth, resizeHeight))
                .setControllerListener(CL(imageId))
                .build()
    }


    private fun getImageMemeUrl(id: String, ratio: Float = 1f): String {
        val w = (screenWidth * fac) step 50
        val h = ((screenWidth / ratio).toInt()
                .trim(200.dp, screenHeight - 200.dp) * fac) step 50
        return "http://res.cloudinary.com/innov8/image/upload/c_fit,h_$h,q_$quality,w_$w/$id"
    }

    private fun getGifMemeUrl(id: String, ratio: Float = 1f): String {
        val w = (screenWidth * fac) step 50
        val h = ((screenWidth / ratio).toInt()
                .trim(200.dp, screenHeight - 200.dp) * fac) step 50
        return "http://res.cloudinary.com/innov8/image/fetch/c_fit,h_$h,q_$quality,w_$w/${Uri.encode(id)}"
    }

    private fun lowResUrl(id: String): String =
            "http://res.cloudinary.com/innov8/image/fetch/f_jpg,c_fit,e_blur:$blurSize,h_$lowSize,q_$lowQuality,w_$lowSize/${Uri.encode(id)}"


    private fun makeLowResGifReq(url: String) =
            ImageRequestBuilder.fromRequest(ImageRequest.fromUri(lowResUrl(url)))
                    .setPostprocessor(IterativeBoxBlurPostProcessor(2))
                    .build()

    private fun makeHighResGifReq(gifUrl: String, resizeWidth: Int, resizeHeight: Int): ImageRequest {
        var req = ImageRequest.fromUri(gifUrl)
        if (resizeWidth > 0 && resizeHeight > 0)
            req = ImageRequestBuilder.fromRequest(req)
                    .setResizeOptions(ResizeOptions.forDimensions(resizeWidth, resizeHeight))
                    .build()
        return req

    }
}
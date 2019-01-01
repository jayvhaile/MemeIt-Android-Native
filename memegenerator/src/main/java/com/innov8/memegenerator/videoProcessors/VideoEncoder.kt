package com.innov8.memegenerator.videoProcessors

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import java.io.ByteArrayOutputStream
import com.innov8.memegenerator.webp.graphics.WebpBitmapEncoder
import java.io.File


fun encode() {

    val bufferInfo = MediaCodec.BufferInfo()
    val codec = MediaCodec.createEncoderByType("video/mp4")
    val format = MediaFormat.createVideoFormat("video/mp4", 100, 100).apply {
        setInteger(MediaFormat.KEY_BIT_RATE, 125000)
        setInteger(MediaFormat.KEY_FRAME_RATE, 12)
        setInteger(MediaFormat.KEY_COLOR_FORMAT, if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        } else {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        })
        setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,5)
    }

    codec.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)

    codec.start()


    val inputBuffers = codec.inputBuffers

    val byteArrayOutputStream = ByteArrayOutputStream()
//    image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // image is the bitmap
    val input = byteArrayOutputStream.toByteArray()


    val inputBufferIndex = codec.dequeueInputBuffer(-1)
    if (inputBufferIndex >= 0) {
        val inputBuffer = input[inputBufferIndex]
        /*inputBuffer.clear()
        inputBuffer.put(input)*/
        codec.queueInputBuffer(inputBufferIndex, 0, input.size, 0, 0)
    }
}
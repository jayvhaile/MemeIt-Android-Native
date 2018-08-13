package com.innov8.memegenerator.utils

import android.content.Context
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast

fun Context.toast(message:String,duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(this,message,duration).show()
}
fun SeekBar.onProgressChanged(change:(progress:Int, fromUser:Boolean)->Unit){
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            change(i,b)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    })
}
fun log(vararg messages: Any) {
    Log.d("#MemeIt", messages.joinToString (" , "))
}
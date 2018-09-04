package com.innov8.memegenerator.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import java.io.ByteArrayOutputStream

class MyAsyncTask<Return>: AsyncTask<Unit, Unit, Return>(){
    var task:(()->Return)?=null
    var onPostExecute:((Return)->Unit)?=null

    override fun doInBackground( p0: Array<Unit>): Return {
        return task?.invoke()!!
    }
    fun start(t:()->Return):MyAsyncTask<Return>{
        task=t
        execute()
        return this
    }
    fun onFinished(p:((Return)->Unit)){
        onPostExecute=p
    }

    override fun onPostExecute(result: Return) {
        super.onPostExecute(result)
        onPostExecute?.invoke(result)
    }


}


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
fun TabLayout.onTabSelected(onSelected:(TabLayout.Tab)->Unit){

    addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            onSelected(tab!!)
        }
    })
}
fun log(vararg messages: Any) {
    Log.d("#MemeIt", messages.joinToString (" , "))
}
 fun androidx.recyclerview.widget.RecyclerView.initWithGrid(spanCount:Int, orientation:Int= RecyclerView.VERTICAL, rev:Boolean=false){
    val glm= androidx.recyclerview.widget.GridLayoutManager(this.context, spanCount, orientation, rev)
    this.layoutManager=glm
    this.itemAnimator= androidx.recyclerview.widget.DefaultItemAnimator()
}
fun androidx.recyclerview.widget.RecyclerView.initWithStagger(spanCount:Int, orientation:Int= androidx.recyclerview.widget.LinearLayoutManager.VERTICAL){
    val glm= androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, orientation)
    this.layoutManager=glm
    this.itemAnimator= androidx.recyclerview.widget.DefaultItemAnimator()
}

fun androidx.fragment.app.FragmentManager.replace(id:Int, fragment: androidx.fragment.app.Fragment){
    beginTransaction().replace(id,fragment).commit()
}
fun android.app.FragmentManager.replace(id:Int, fragment: android.app.Fragment){
    beginTransaction().replace(id,fragment).commit()
}

fun calcSampleSize(option:BitmapFactory.Options, reqWidth:Int, reqHeight:Int):Int{
    val width=option.outWidth
    val height=option.outHeight
    var size=1
    if(width>reqWidth||height>reqHeight){
        val hw=width/2
        val hh=height/2
        while (hw/size>=reqWidth&&hh/size>=reqHeight){
            size*=2
        }
    }
    return size
}
fun calcSampleSize(option:BitmapFactory.Options,quality: Float):Int{
    val size=Math.max(option.outWidth,option.outHeight)
    val reqSize=size*if (quality>1f) 1f else quality
    var sampleSize=1
    if(size>reqSize){
        val halfSize=size/2
        while (halfSize/sampleSize>reqSize) sampleSize*=2
    }
    return sampleSize
}
fun Context.loadBitmap(id:Int,quality:Float):Bitmap{
    val opt=BitmapFactory.Options()
    opt.inJustDecodeBounds=true
    BitmapFactory.decodeResource(resources,id,opt)
    opt.inSampleSize= calcSampleSize(opt,quality)
    opt.inJustDecodeBounds=false
    return BitmapFactory.decodeResource(resources,id,opt)
}
fun Context.getDrawableIdByName(name:String):Int{
    return resources.getIdentifier(name,"drawable",packageName)
}
fun Float.toSP(context:Context):Float{
        return this/context.resources.displayMetrics.scaledDensity
}
fun Float.fromSP(context:Context):Float{
    return this*context.resources.displayMetrics.scaledDensity
}
fun Float.toDP(context:Context):Float{
    return this/context.resources.displayMetrics.density
}
fun Float.fromDPToPX(context:Context):Float{
    return this*context.resources.displayMetrics.density
}
fun Int.fromDPToPX(context:Context):Int{
    return (this*context.resources.displayMetrics.density).toInt()
}
fun Context.goTo(clazz: Class<out Activity>){
    startActivity(Intent(this,clazz))
}
fun Activity.goTo(clazz:Class<out Activity>,finish:Boolean=false){
    startActivity(Intent(this,clazz))
    if(finish)
        finish()
}
fun Activity.goToWithString(clazz:Class<out Activity>,data:String,finish:Boolean=false){
    val intent=Intent(this,clazz)
    intent.putExtra("string",data)
    startActivity(intent)
    if(finish)
        finish()
}
fun Bitmap.toByteArray(format:Bitmap.CompressFormat=PNG, quality:Int=100):ByteArray{
    val stream= ByteArrayOutputStream()
    compress(format, quality, stream)
    return stream.toByteArray()
}
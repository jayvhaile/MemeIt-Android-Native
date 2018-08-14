package com.innov8.memegenerator.utils

import android.content.Context
import android.os.AsyncTask
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
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
inline fun log(vararg messages: Any) {
    Log.d("#MemeIt", messages.joinToString (" , "))
}
inline fun RecyclerView.initWithGrid(spanCount:Int,orientation:Int=LinearLayoutManager.VERTICAL,rev:Boolean=false){
    val glm=GridLayoutManager(this.context,spanCount,orientation,rev)
    this.layoutManager=glm
    this.itemAnimator=DefaultItemAnimator()
}
inline fun FragmentManager.replace(id:Int, fragment: Fragment){
    beginTransaction().replace(id,fragment).commit()
}
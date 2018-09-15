package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Adapters.ReactorAdapter
import com.innov8.memeit.R
import com.innov8.memeit.makeLinear
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Reaction
import com.memeit.backend.utilis.Listener
import kotlinx.android.synthetic.main.activity_reactor_list.*
import java.lang.IllegalStateException

class ReactorListActivity : AppCompatActivity() {

    private lateinit var reactorAdapter:ReactorAdapter
    private lateinit var mid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mid=intent?.getStringExtra("mid")?:throw IllegalStateException("Should pass mid(Reactions)")
        val rc=intent?.getStringExtra("rc")?:0

        setContentView(R.layout.activity_reactor_list)
        reactorAdapter= ReactorAdapter(this)
        reactors_list.makeLinear()
        reactors_list.adapter=reactorAdapter
        reactor_count.text=if(rc==0)"No Reaction" else "$rc People reacted"

        reactor_swipe_refresh.setOnRefreshListener {
            load()
        }
        load()
    }
    private fun load(){
        //todo implement skip limit , show the total count not the limited one
       MemeItMemes.getInstance().getReactorsForMeme(mid,0,100000,Listener<List<Reaction>>({
           reactorAdapter.setAll(it)
           val rc=it.size
           reactor_count.text=if(rc==0)"No Reaction" else "$rc People reacted"
           reactor_swipe_refresh.isRefreshing=false

       },{
           toast("Reaction list failed ${it.message}")
           reactor_swipe_refresh.isRefreshing=false
       }))
    }
}
package com.memeit.backend

import org.junit.Assert.assertEquals
import org.junit.Test

class Test {


    @Test
    fun x() {
       val q= listOf(.4f,.6f,.8f,.9f,1f)
        val w=860

        var fw=0

        fw=(w*q[0]).step(100)
        assertEquals(fw,300)

        fw=(w*q[1]).step(100)
        assertEquals(fw,500)

        fw=(w*q[2]).step(50)
        assertEquals(fw,600)

        fw=(w*q[3]).step(100)
        assertEquals(fw,700)

        fw=(w*q[4]).step(100)
        assertEquals(fw,800)
    }


    fun Float.step(step:Int):Int{
        val x=Math.max(this/step,1f).toInt()
        return x*step
    }
}
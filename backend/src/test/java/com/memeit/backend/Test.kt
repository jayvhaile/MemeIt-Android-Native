package com.memeit.backend

import org.junit.Assert.assertEquals
import org.junit.Test

class Test {


    @Test
    fun x() {
        val q = mutableListOf(.4f, .6f, .8f, .9f, 1f)
        assertEquals(1,q.indexOf(.6f))
        q.remove(.4f)
        assertEquals(0,q.indexOf(.6f))
    }


    fun Float.step(step: Int): Int {
        val x = Math.max(this / step, 1f).toInt()
        return x * step
    }
}
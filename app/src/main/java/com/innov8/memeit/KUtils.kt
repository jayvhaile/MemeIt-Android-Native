package com.innov8.memeit

import kotlinx.coroutines.experimental.*

fun launchTask(block:suspend CoroutineScope.()->Unit): Job {
    return launch {  block()}
}

suspend fun<T> runAsync(block: suspend  CoroutineScope.()->T):T= async(CommonPool) { block() }.await()

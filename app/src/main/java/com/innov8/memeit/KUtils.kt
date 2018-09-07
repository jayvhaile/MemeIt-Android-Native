package com.innov8.memeit

import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat

fun launchTask(block:suspend CoroutineScope.()->Unit): Job {
    return launch {  block()}
}

suspend fun<T> runAsync(block: suspend  CoroutineScope.()->T):T= async(CommonPool) { block() }.await()

val SECOND_MILLIS = 1000L
val MINUTE_MILLIS = SECOND_MILLIS * 60
val HOUR_MILLIS = MINUTE_MILLIS * 60
val DAY_MILLIS = HOUR_MILLIS * 24
val WEEK_MILLIS = DAY_MILLIS * 7
val MONTH_MILLIS = DAY_MILLIS * 30
val YEAR_MILLIS = DAY_MILLIS * 365

fun formatDate(date: Long): String {
    val now = System.currentTimeMillis()


    val sdf = SimpleDateFormat("hh:mm a")
    val sdf2 = SimpleDateFormat("MMM dd, yyyy")
    if (date < 0 || date > now) {
        throw IllegalArgumentException("Illegal Date")
    }

    val diff = now - date
    val formated =
            when {
                diff < MINUTE_MILLIS -> "just now"
                diff < 2 * MINUTE_MILLIS -> "a minute ago"
                diff < HOUR_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
                diff < 2 * HOUR_MILLIS -> "an hour ago"
                diff < DAY_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
                diff < 2 * DAY_MILLIS -> "yesterday at ${sdf.format(date)}"
                diff < WEEK_MILLIS -> "${diff / DAY_MILLIS} days ago"
                diff < 2 * WEEK_MILLIS -> "a week ago"
                else -> sdf2.format(date)
            }
    return formated
}
fun Long.formateAsDate():String= formatDate(this)

package com.liuzifan.kotlin

import com.liuzifan.alltogether.activity.revert
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

fun Test() {
    println("kotlin:Start")

    val c = AtomicLong()

    for (i in 1..1_000_000L)
        GlobalScope.launch {
            
            c.addAndGet(i)
        }

    println("kotlin:" + c.get())

    println("kotlin:Stop")

    val str = "";

    str.revert();
}

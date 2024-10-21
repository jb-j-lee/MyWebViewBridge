package com.myjb.mywebview

interface SecondJavaBridgeInterface {
    fun init()
    fun change(status: String)
    fun callback(message: String)
    fun close()
}
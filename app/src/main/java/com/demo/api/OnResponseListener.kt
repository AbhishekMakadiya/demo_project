package com.demo.api

abstract class OnResponseListener<in T> : OnListener<T> {
    override fun onRequestSuccess(response: T) {

    }

    override fun onRequestSuccess(isSuccess: Boolean, response: T, message: String) {

    }

    override fun onRequestFailed(t: Throwable) {

    }

    override fun onRequestFailed(message: String) {

    }
}

interface OnListener<in T> {
    fun onRequestSuccess(response: T)
    fun onRequestSuccess(isSuccess: Boolean, response: T, message: String)  // use this method in handle pagination response
    fun onRequestFailed(t: Throwable)
    fun onRequestFailed(message: String)
}

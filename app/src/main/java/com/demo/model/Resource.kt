package com.demo.model

import com.demo.constants.Const

data class Resource<out T>(val status: Const.Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: Response<T>?): Resource<Response<T>> {
            return Resource(Const.Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Const.Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Const.Status.LOADING, data, null)
        }

    }

}
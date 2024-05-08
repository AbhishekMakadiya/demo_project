package com.demo.fcmNotification

interface FcmTokenValidator {
    fun onFCMTokenReceived(token: String, requestCode: Int)
    fun onFCMTokenRetrievalFailed(failureReason: String)
}
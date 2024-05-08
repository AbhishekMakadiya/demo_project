package com.demo.api

object

Api {
    val MAINURL = APIURL.DEVELOPER

    const val SCHEME = "https"
    const val AUTHORITY_DEVELOPER = "http://staging.abc.com" // staging
    const val AUTHORITY_PRODUCTION = "http://staging.abc.com" // live
    const val PATH = "api/"

    const val TERMS_CONDITION = "terms_and_condition.html"
    const val PRIVACY_POLICY = "privacy-policy.html"
    const val SUPPORT = "support.html"
    const val PAYMENT = "payment.html"
    const val EXCHANGES = "exchanges.html"
    const val WALLET = "wallet.html"

    const val API_SIGNUP = "signup"
    const val API_LOGIN = "login"
    const val API_FORGOT_PASSWORD = "forgot_password"
    const val API_REFRESH = "refresh"
    const val API_LOGOUT = "logout"

    const val API_GET_ADVERTISEMENT = "get_ad_list"

    const val API_SEND_OTP = "send_otp.php"
    const val API_VERITY_OTP = "verify_otp.php"
}

enum class APIURL {
    DEVELOPER, PRODUCTION
}


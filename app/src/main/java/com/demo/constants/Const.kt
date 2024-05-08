package com.demo.constants

object Const {
    const val MAXIMUM_IMAGE_SIZE_TO_UPLOAD =
        1024 * 1024 * 2 //2MB (Byte * Kilobyte * Megabyte).toLong()
    const val DEVICETYPE = 1
    const val API_KEY = 123456

    /**
     *  User Type
     * */

    const val USER_TYPE_FARMER = 2
    const val USER_TYPE_USER = 1

    /**
     *  Login Type
     * */
    const val LOGIN_TYPE_MOBILE_NO = 0
    const val LOGIN_TYPE_FB = 1
    const val LOGIN_TYPE_GOOGLE = 2

    const val REQUEST_CODE_SELECT_COUNTRY = 101

    // Font
    const val FONT_TYPE_REGULAR = 1
    const val FONT_TYPE_SEMI_BOLD = 2
    const val FONT_TYPE_MEDIUM = 3
    const val FONT_TYPE_BOLD = 4

    const val FONT_PATH_BOLD = "fonts/AN-Bold.otf"
    const val FONT_PATH_LIGHT = "fonts/AN-Regular.otf"
    const val FONT_PATH_MEDIUM = "fonts/AN-Medium.otf"
    const val FONT_PATH_REGULAR = "fonts/AN-Regular.otf"
    const val FONT_PATH_SEMI_BOLD = "fonts/AN-Bold.otf"

    //chat content
    const val CONTENT_TEXT = "TEXT"
    const val CONTENT_AUDIO = "AUDIO"
    const val CONTENT_VIDEO = "VIDEO"
    const val CONTENT_IMAGE = "IMAGE"

    // Item Type
    const val ITEM_TYPE_HEADER = 1
    const val ITEM_TYPE_CONTENT = 2
    const val ITEM_TYPE_LOADING = 3

    // Status
    const val PENDING = "Pending"
    const val ACCEPTED = "Accepted"
    const val REJECTED = "Rejected"
    const val CANCEL = "Cancel"
    const val COMPLETED = "Completed"

    const val STATUS_PENDING = 0
    const val STATUS_COMPLETED = 1
    const val STATUS_FAILED = 2
    const val STATUS_REJECTED = 3

    // Response Code
    const val SUCCESS = 200
    const val SUGGESTION = 201
    const val CHARACTER_LENGTH = 200
    const val LOGOUT = 403
    const val BLOCKED_BY_ADMIN = 502

    // Request Parameters
    const val PARAM_AUTHORIZATION = "Authorization"
    const val PARAM_USER_ID = "user_id"
    const val PARAM_DEVICE_TYPE = "device_type"
    const val PARAM_DEVICE_TOKEN = "device_token"
    const val PARAM_EMAIL = "email"
    const val PARAM_PASSWORD = "password"
    const val PARAM_PHONE = "phone"
    const val PARAM_OTP = "otp"
    const val PARAM_NID = "nid"
    const val PARAM_UDID = "udid"
    const val PARAM_LANGUAGE = "language"
    const val PARAM_TIMEZONE = "timezone"
    const val PARAM_TYPE = "type"
    const val PARAM_COUNTRY_CODE = "country_code"
    const val PARAM_COUNTRY = "country"
    const val UDID = "123456"
    const val LANGUAGE = "en"

    // Extra parameters
    const val EXTRA_URI = "EXTRA_URI"
    const val EXTRA_DATA = "EXTRA_DATA"
    const val EXTRA_ACTION = "EXTRA_ACTION"
    const val EXTRA_FCM_TAG = "EXTRA_FCM_TAG"

    // Language param
    const val CUSTOM_INTENT_CHANGE_LANGUAGE = "custom.intent.action.ChangeLanguage"
    const val LOCALE_ENGLISH = 1
    const val LOCALE_ARABIC = 2

    /**
     * broadcast receivers intent filter
     * */
    const val INTENT_USER_ID = "com.demo.ads.INTENT_USER_ID"
    const val REFRESH_USERS_POST_DATA = "com.demo.ads.REFRESH_USERS_POST_DATA"
    const val INTENT_GET_NEW_CHAT_MESSAGE = "com.demo.ads.INTENT_GET_NEW_CHAT_MESSAGE"
    const val INTENT_GET_BLOCKED_USER = "com.demo.ads.INTENT_GET_BLOCKED_USER"

    const val ACTION_SESSION_EXPIRE = "android.intent.action.SESSION_EXPIRE"
    const val ACTION_SCREEN_CLOSE = "android.intent.action.SCREEN_CLOSE"
    const val ACTION_LOGOUT = "android.intent.action.ACTION_LOGOUT"
    const val ACTION_TAB = "android.intent.action.ACTION_TAB"
    const val INTENT_REFRESH_SCREEN = "android.intent.action.ACTION_REFRESH_SCREEN"
    const val INTENT_REFRESH = "android.intent.action.ACTION_REFRESH"
    const val INTENT_SUB = "android.intent.action.ACTION_SUB"
    const val INTENT_FINISH = "android.intent.action.ACTION_FINISH"
    const val INTENT_CHECK_SUB = "android.intent.action.ACTION_CHECK_SUB"

    // Urls
    const val URL_INSTAGRAM = "https://instagram.com/"
    const val URL_TWITTER = "https://twitter.com/"
    const val URL_FACEBOOK = "https://facebook.com/"

    const val SCREEN_YOUR_PROFILE = 1
    const val SCREEN_UPLOAD_PICTURE = 2
    const val SCREEN_INTEREST_SCREEN = 3

    // Enum Const
    enum class ACTION {
        ADD, EDIT
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }



    // Twilio const variables

    /*
    * You must provide the URL to the publicly accessible Twilio access token server route
    *
    * For example: https://myurl.io/accessToken
    *
    * If your token server is written in PHP, TWILIO_ACCESS_TOKEN_SERVER_URL needs .php extension at the end.
    *
    * For example : https://myurl.io/accessToken.php
    */

    const val TWILIO_BASE_URL = "http://34.83.201.38/Twilio/"
    //const val TWILIO_ACCESS_TOKEN_SERVER_URL = "https://twiliocatchups.tk/accessToken.php"
    const val TWILIO_ACCESS_TOKEN_SERVER_URL = TWILIO_BASE_URL + "accessToken.php"
    const val TWILIO_VIDEO_ACCESS_TOKEN_SERVER_URL = "videoCall.php"
    const val TWILIO_NOTIFICATION_URL = "videoCallNotification.php"
    const val TWILIO_BINDING_URL = "register-binding.php"
    const val TWILIO_UNBINDING_URL = "deregisterVideoToken.php"

    const val COUNTRY_CODE = "COUNTRY_CODE"
    const val CALL_SID_KEY = "CALL_SID"
    const val VOICE_CHANNEL_LOW_IMPORTANCE = "notification-channel-low-importance"
    const val VOICE_CHANNEL_HIGH_IMPORTANCE = "notification-channel-high-importance"
    const val INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE"
    const val CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE"
    const val INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID"
    const val ACTION_ACCEPT = "ACTION_ACCEPT"
    const val ACTION_REJECT = "ACTION_REJECT"
    const val ACTION_INCOMING_CALL_NOTIFICATION = "ACTION_INCOMING_CALL_NOTIFICATION"
    const val ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL"
    const val ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL"
    const val ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN"

}

package com.demo.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.demo.R
import com.demo.constants.Const
import com.demo.fcmNotification.FcmTokenValidator
import com.demo.utils.alertmessages.ActivityHelper
import com.demo.utils.alertmessages.AlertMessage
import com.demo.views.MyCustomEditText
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.MessageFormat
import java.util.*
import java.util.regex.Pattern


object Util {


    /**
     * check isInternetConnected
     * */
    fun isConnectedToInternet(mContext: Context?): Boolean {
        return try {
            val cm =
                mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetwork: NetworkInfo? = cm?.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * show toast messages
     */
    fun message(mContext: Context?, message: String?) {
        if (mContext != null && message != null && !message.trim().isEmpty()) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * show toast messages
     */
    fun showMessage(title: String, message: String, mContext: Context) {
        if (message.trim().isNotEmpty()) {
            AlertMessage.showMessage(mContext, message, title)
        }
    }

    /**
     * show toast messages
     */
    fun showMessage(mContext: Context?, message: String?) {
        if (mContext != null && message != null && !message.trim().isEmpty()) {
            AlertMessage.showMessage(mContext, message)
        }
    }

    /**
     * Hide Keyboard
     */
    fun hideKeyboard(mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            imm.hideSoftInputFromWindow((mContext as Activity).window.currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }

    fun hideKeyboard(dialog: Dialog?) {
        try {
            if (dialog != null) {
                if (dialog.window != null) {
                    val imm: InputMethodManager =
                        dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(dialog.window?.currentFocus?.windowToken, 0)
                }
            }
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }

    /**
     * show keyboard
     * */
    fun showKeyboard(mContext: Context) {
        try {
            val inputMethodManager =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                (mContext as Activity).window.currentFocus?.windowToken,
                InputMethodManager.SHOW_FORCED,
                0
            )
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }


    /**
     * load image resource
     * */
    fun loadImageResource(mContext: Context, imageResourceId: Int, imageView: ImageView) {
        Glide.with(mContext).load(imageResourceId).into(imageView)
    }

    /**
     * load image bitmap
     * */
    fun loadImageBitmap(
        mContext: Context,
        bitmap: Bitmap?,
        placeHolder: Int,
        imageView: ImageView,
    ) {
        Glide.with(mContext).load(bitmap)
            .apply(RequestOptions().placeholder(placeHolder).error(placeHolder))
            .into(imageView)
    }

    /**
     * load local image
     * */
    fun loadLocalImage(
        mContext: Context,
        imageUrl: String?,
        placeHolder: Int,
        imageView: ImageView,
    ) {
        try {
            val requestOptions = RequestOptions()
                .placeholder(placeHolder)
                .signature(ObjectKey(File(imageUrl!!).lastModified()))
                .error(placeHolder)
                .dontAnimate()

            Glide.with(mContext).load(imageUrl)
                .apply(requestOptions)
                .into(imageView)
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }

    fun loadImageUrl(
        mContext: Context,
        imageUrl: String?,
        placeHolder: Int?,
        imageView: ImageView,
    ) {
        try {
            if (placeHolder != null) {
                val requestOptions = RequestOptions()
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .dontAnimate()

                Glide.with(mContext).load(imageUrl)
                    .apply(requestOptions)
                    .into(imageView)
            } else {
                Glide.with(mContext).load(imageUrl)
                    .into(imageView)
            }

        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }

    private fun getUrlWithHeaders(url: String, token: String): GlideUrl {
        return GlideUrl(
            url, LazyHeaders.Builder()
                .addHeader("Authorization", token)
                .build()
        )
    }

    fun changeTabsFont(mContext: Context, tabLayout: TabLayout) {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildCount = vgTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = Typeface.createFromAsset(mContext.assets, Const.FONT_PATH_BOLD)
                    tabViewChild.isAllCaps = false
                    tabViewChild.textSize = mContext.resources.getDimension(R.dimen.txt_12sp)
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun getSecureKey(mContext: Context): String? {
        return Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /*
    * Spannable two string
    * */
    fun SpanTwoString(
        txtFirst: String,
        txtSecond: String,
        firstTxtColor: Int,
        secondTxtColor: Int,
    ): SpannableStringBuilder {
        val span1 = SpannableStringBuilder(txtFirst)
        span1.setSpan(
            ForegroundColorSpan(firstTxtColor),
            0,
            span1.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        val span2 = SpannableStringBuilder(txtSecond)
        span2.setSpan(
            ForegroundColorSpan(secondTxtColor),
            0,
            span2.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        /*val span3 = SpannableStringBuilder(txtSeprator)
        span3.setSpan(ForegroundColorSpan(firstTxtColor), 0, span3.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)*/

        //val concatenated: Spanned = TextUtils.concat(span1, span3, span2) as Spanned
        val concatenated: Spanned = TextUtils.concat(span1, " ", span2) as Spanned
        val result = SpannableStringBuilder(concatenated)
        return result
    }

    /**
     * show progress dialog
     */
    @Suppress("DEPRECATION")
    fun getProgressDialog(mContext: Context): ProgressDialog {
        val dialog = ProgressDialog(mContext)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.isIndeterminate = true
        dialog.setMessage(mContext.getString(R.string.please_wait))
        return dialog
    }

    fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString)
    }

    /**
     * get mime type from url
     */
    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }


    /**
     * This method will check for fcm token. If not received then it try to get it.
     * If not getting token then we will notify it by method with reason.
     * If we get token then it will notify it by passing token.
     */
    fun validateTokenAndCallAPI(
        @NonNull mContext: Context, fcmTokenValidator: FcmTokenValidator?,
        isShowProgressDialog: Boolean = true, @NonNull requestCode: Int,
    ) {
        if (!isConnectedToInternet(mContext)) {
            fcmTokenValidator?.onFCMTokenRetrievalFailed(mContext.getString(R.string.error_no_internet))
            return
        }
        val preferenceManager = PreferenceManager(mContext)
        val storedFCMToken = preferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
        if (storedFCMToken == null || storedFCMToken.isEmpty()) {
            LogHelper.e(
                "validateTokenAndCallAPI",
                "token not available in local, so try to get new token"
            )
            //when we are fetching token then show dialog, so that user aware of this.
            val progressDialog = getProgressDialog(mContext)
            progressDialog.setMessage(mContext.getString(R.string.fetch_token_information))

            if (isShowProgressDialog)
                ActivityHelper.showDialog(progressDialog)

            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                //When we get response of then hide progress dialog and change its message.Because this dialog is used for showing login api progress.
                //So in login api call we show message for token that is not valid. So simply change message and hide dialog.

                if (isShowProgressDialog)
                    ActivityHelper.dismissDialog(progressDialog)

                if (task.isSuccessful && task.result != null && task.result!!.token.isNotEmpty()) {
                    LogHelper.e("validateTokenAndCallAPI", "getting token here, not from receiver")
                    preferenceManager.setStringPreference(
                        PreferenceManager.FCM_TOKEN,
                        task.result!!.token
                    )

                    fcmTokenValidator?.onFCMTokenReceived(task.result!!.token, requestCode)
                } else {
                    val exception = task.exception
                    exception?.printStackTrace()
                    if (exception?.message != null) {
                        //If we have any error message then we will directly display that to user.
                        fcmTokenValidator?.onFCMTokenRetrievalFailed(exception.message!!)
                    } else {
                        //if we didn't have any proper message then simply say that we are failed to fetch token.
                        fcmTokenValidator?.onFCMTokenRetrievalFailed(mContext.getString(R.string.fcm_token_not_received))
                    }
                }
            }
        } else {
            //If token available then directly notify it.
            fcmTokenValidator?.onFCMTokenReceived(storedFCMToken, requestCode)
        }

    }


    fun validateTokenAndCallAPI(
        @NonNull mContext: Context,
        @NonNull fcmTokenValidator: FcmTokenValidator,
        @NonNull requestCode: Int,
    ) {
        if (!isConnectedToInternet(mContext)) {
            fcmTokenValidator.onFCMTokenRetrievalFailed(mContext.getString(R.string.error_no_internet))
            return
        }
        val preferenceManager = PreferenceManager(mContext)
        val storedFCMToken = preferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
        if (storedFCMToken == null || storedFCMToken.isEmpty()) {
            LogHelper.e(
                "validateTokenAndCallAPI",
                "token not available in local, so try to get new token"
            )
            //when we are fetching token then show dialog, so that user aware of this.
            val progressDialog = getProgressDialog(mContext)
            progressDialog.setMessage(mContext.getString(R.string.fetch_token_information))
            //            progressDialog.show()
            ActivityHelper.showDialog(progressDialog)
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                //When we get response of then hide progress dialog and change its message.Because this dialog is used for showing login api progress.
                //So in login api call we show message for token that is not valid. So simply change message and hide dialog.
                //                progressDialog.hide()
                ActivityHelper.dismissDialog(progressDialog)
                if (task.isSuccessful && task.result != null && task.result!!.token.isNotEmpty()) {
                    LogHelper.e("validateTokenAndCallAPI", "getting token here, not from receiver")
                    preferenceManager.setStringPreference(
                        PreferenceManager.FCM_TOKEN,
                        task.result!!.token
                    )

                    fcmTokenValidator.onFCMTokenReceived(task.result!!.token, requestCode)
                } else {
                    val exception = task.exception
                    exception?.printStackTrace()
                    if (exception?.message != null) {
                        //If we have any error message then we will directly display that to user.
                        fcmTokenValidator.onFCMTokenRetrievalFailed(exception.message!!)
                    } else {
                        //if we didn't have any proper message then simply say that we are failed to fetch token.
                        fcmTokenValidator.onFCMTokenRetrievalFailed(mContext.getString(R.string.fcm_token_not_received))
                    }
                }
            }
        } else {
            //If token available then directly notify it.
            fcmTokenValidator.onFCMTokenReceived(storedFCMToken, requestCode)
        }

    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display ring1 dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    /* private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

     fun checkPlayServices(activity: Activity): Boolean {
         val apiAvailability = GoogleApiAvailability.getInstance()
         val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
         if (resultCode != ConnectionResult.SUCCESS) {
             if (apiAvailability.isUserResolvableError(resultCode)) {
                 apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                     .show()
             } else {
                 LogHelper.e("MyTagUtil.java", "This device is not supported.")
                 //                activity.finish();
             }
             return false
         }
         return true
     }*/

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /*
    * Start with rating the app
    * Determine if the Play Store is installed on the device
    *
    * */
    fun rateApp(context: Context) {
        try {
            val rateIntent = rateIntentForUrl("market://details", context)
            context.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details", context)
            context.startActivity(rateIntent)
        }

    }

    private fun rateIntentForUrl(url: String, context: Context): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, context.packageName))
        )
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, "com.cricbuzz.android")))
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        if (Build.VERSION.SDK_INT >= 21) {
            flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                flags = flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            }
        }
        intent.addFlags(flags)
        return intent
    }

    /**
     * crop activity
     */
    // fun startCropActivity(uri: Uri, activity: Activity, x: Float, y: Float) {
    // var uCrop = UCrop.of(uri, getUri(activity))
    //
    // uCrop = uCrop.withAspectRatio(x, y)  // set image crop ratio
    // val option = UCrop.Options()
    // option.setCompressionFormat(Bitmap.CompressFormat.JPEG)
    // option.setCompressionQuality(100)
    //
    // uCrop = uCrop.withOptions(option)
    // uCrop.start(activity)
    // }

    /**
     * crop activity
     */
    // fun startFreeCropActivity(uri: Uri, activity: Activity) {
    // var uCrop = UCrop.of(uri, getUri(activity))
    // val option = UCrop.Options()
    // option.setCompressionFormat(Bitmap.CompressFormat.JPEG)
    // option.setCompressionQuality(100)
    //
    // uCrop = uCrop.withOptions(option)
    // uCrop.start(activity)
    // }

    /**
     * crop activity
     */
    // fun startCropActivity(uri: Uri, activity: Activity, requestCode: Int, isFreeScale: Boolean) {
    // var uCrop = UCrop.of(uri, getUri(activity))
    //
    // if (!isFreeScale) {
    // uCrop = uCrop.withAspectRatio(16f, 9f)  // set image crop ratio
    // }
    // val option = UCrop.Options()
    // option.setCompressionFormat(Bitmap.CompressFormat.JPEG)
    // option.setCompressionQuality(100)
    // option.setFreeStyleCropEnabled(isFreeScale)
    //
    // uCrop = uCrop.withOptions(option)
    // uCrop.start(activity, requestCode)
    // }

    // get uri
    private fun getUri(activity: Activity): Uri {
        val fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), "jpeg")
        return Uri.fromFile(
            File(
                activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
        )
    }

    //-- To get file path
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context?, uri: Uri): String? {
        val isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context!!, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1]) as Array<String?>?
                return getDataColumn(
                    context!!,
                    contentUri,
                    selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context!!, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String?>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display ring1 dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    fun checkPlayServices(activity: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    activity,
                    resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )
                    ?.show()
            } else {
                LogHelper.e("MyTagUtil.java", "This device is not supported.")
                //                activity.finish();
            }
            return false
        }
        return true
    }

    /* get facebook keyhase sha-1*/
    @SuppressLint("PackageManagerGetSignatures")
    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName
            //Retriving package info
            packageInfo =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

            LogHelper.e("Package Name=", context.applicationContext.packageName)

            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(android.util.Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.i("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            LogHelper.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            LogHelper.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            LogHelper.e("Exception", e.toString())
        }
        return key
    }

    /**
     *
     *      Validation for Alpha Numeric
     * */

    fun isAlphaNumeric(s: String): Boolean {
        val pattern = "^[a-zA-Z0-9]*$"
        return s.matches(pattern.toRegex())
    }

    /**
     *
     *      Validation for Valid Email
     * */

    fun isValidEmailId(email: String): Boolean {

        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     *
     *  Create temp file from bitmap and return it
     *
     * */

    fun convertBitmapToFile(mContext: Context, bitmapImage: Bitmap): File {
        //create a file to write bitmap data
        val f = File(mContext.externalCacheDir, "video_thumbnail.jpeg")
        f.createNewFile()

        //Convert bitmap to byte array
        val bitmap = bitmapImage
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return f
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }

    fun openBookCab(
        mContext: Context,
        currLat: Double?,
        currLng: Double?,
        dropLat: Double?,
        dropLng: Double?,
    ) {
        val mIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://olawebcdn.com/assets/ola-universal-link.html?lat=$currLat&lng=$currLng&category=share&utm_source=xapp_token&landing_page=bk&drop_lat=$dropLat&drop_lng=$dropLng&affiliate_uid=12345")
        )
        mContext.startActivity(mIntent)
    }

    fun openUberCab(
        mContext: Context,
        nickName: String,
        currLat: Double?,
        currLng: Double?,
        dropLat: Double?,
        dropLng: Double?,
    ) {
        val pm: PackageManager = mContext.packageManager
        try {
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES)
            val uri = "uber://?" +
                    "pickup[longitude]=$currLng&" +
                    "pickup[latitude]=$currLat&" +
                    "pickup[nickname]=$nickName" +
                    "dropoff[formatted_address]=$nickName, Ahmedabad, Gujarat, India&" +
                    "dropoff[latitude]=$dropLat&" +
                    "dropoff[longitude]=$dropLng&" +
                    "action=setPickup"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(uri)
            mContext.startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                mContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.ubercab")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                mContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.ubercab")
                    )
                )
            }
        }
    }

    fun share(mContext: Context, mTitle: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_content, mTitle))
        mContext.startActivity(
            Intent.createChooser(
                shareIntent,
                mContext.getString(R.string.send_to)
            )
        )
    }

    fun getExtention(url: String): String? {
        return url.substring(url.lastIndexOf("."))
    }

    fun getDirection1(mContext: Context, mSiteName: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$mSiteName, Ahmedabad, Gujarat, India")
        )
        intent.setPackage("com.google.android.apps.maps")
        try {
            mContext.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            try {
                val unrestrictedIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=$mSiteName, Ahmedabad, Gujarat, India")
                )
                mContext.startActivity(unrestrictedIntent)
            } catch (innerEx: ActivityNotFoundException) {
                showMessage(mContext, "Please install a maps application")
            }
        }
    }

    fun getDirection2(mContext: Context, mDestinationLatLng: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$mDestinationLatLng")
        )
        intent.setPackage("com.google.android.apps.maps")
        try {
            mContext.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            try {
                val unrestrictedIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=$mDestinationLatLng")
                )
                mContext.startActivity(unrestrictedIntent)
            } catch (innerEx: ActivityNotFoundException) {
                showMessage(mContext, "Please install a maps application")
            }
        }
    }

    fun galleryAddPic(mContext: Context, mCurrentPhotoPath: String?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f: File = File(mCurrentPhotoPath!!)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        mContext.sendBroadcast(mediaScanIntent)
    }

    fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(context: Context, sp: Float): Float {
        val scale = context.resources.displayMetrics.scaledDensity
        return sp * scale
    }

    fun encodeImageToBase64(mContext:Context, filePath: String, name: String): String {
        val currentTimeMillis = System.currentTimeMillis().toString()

        val bytes = File(filePath).readBytes()
        val  imageString = Base64.encodeToString(bytes, Base64.NO_WRAP)
        //val file = File(mContext.getCacheDir(), currentTimeMillis+"_"+name)
        //file.writeText(imageString)
        //return file.path
        return imageString
    }

    fun decodeBase64ToImage(mContext: Context, base64: String, name:String): String {
        val bytes = Base64.decode(base64, Base64.NO_WRAP)
        val file = File(mContext.getCacheDir(), name)
        file.writeBytes(bytes)
        return file.path
    }

    // this set the cursor at the text length of edittext
    fun setEdtCursorAtText(edtView: MyCustomEditText) {
        edtView.text?.let { edtView.setSelection(it.length) }
    }

    // start whatsapp
    fun startWhatsApp(mContext: Context) {
        val contactNumber = "918877310000"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+$contactNumber/"))
        mContext.startActivity(browserIntent)
    }

    fun getStatus(status: String): String {
        return when(status) {
            "0" -> "Pending"
            "1" -> "Completed"
            "2" -> "Failed"
            "3" -> "Rejected"
            else -> ""
        }
    }

    fun getTransType(type: String): String {
        return when(type) {
            "Cr" -> "+"
            "Dr" -> "-"
            else -> ""
        }
    }

    fun mSendIntent(redirect: String?, context: Context) {
        try {
            if (!TextUtils.isEmpty(redirect) && redirect?.isNotEmpty()!!) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(redirect)
                context.startActivity(i)
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    fun copyTextToClipBoard(mContext: Context, value: String, message:String) {
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", value)
        clipboard.setPrimaryClip(clip)
        showMessage(mContext, message)
    }
}
package com.demo.ui.webview

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.DownloadListener
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.demo.R
import com.demo.databinding.ActivityWebViewBinding
import com.demo.ui.base.BaseActivity
import com.demo.constants.Const
import com.demo.utils.LogHelper
import com.demo.utils.LogHelper.e
import com.demo.utils.LogHelper.printStackTrace
import com.demo.utils.Util
import com.demo.utils.Util.isConnectedToInternet
import com.demo.utils.alertmessages.AlertMessage
import com.demo.utils.alertmessages.AlertMessageListener
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {

    var mTitleString: String = ""
    var mUrl: String = ""
    var isPdfFile = false
    override fun bindViews() {

    }

    override fun getViewBinding() = ActivityWebViewBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isConnectedToInternet(mContext)) {
            Util.showMessage(mContext, getString(R.string.error_no_internet))
            finish()
        }
        requestedOrientation = if (intent.hasExtra(Intent.EXTRA_REFERRER))
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(mBinding.root)
        mUrl = intent.getStringExtra(Const.EXTRA_URI) as String
        mTitleString = intent.getStringExtra(Intent.EXTRA_TITLE) as String
        isPdfFile = intent.getBooleanExtra(Intent.EXTRA_TEXT, false)

        setUpToolBar(mTitleString, R.drawable.ic_back_arrow_black)

        mBinding.webView?.webViewClient = WebViewClient()
        mBinding.webView?.settings?.javaScriptEnabled = true
        mBinding.webView.addJavascriptInterface(WebAppInterface(this), "Android")

        if (isPdfFile) {

            val fileName = mUrl.substring(mUrl.lastIndexOf("/") + 1, mUrl.length)
            val urlPath = mUrl.substring(0, mUrl.lastIndexOf("/") + 1)
            try { //                fileName=fileName.replace("+"," ");
                var encodedStr = URLEncoder.encode(fileName, "UTF-8")
                encodedStr = encodedStr.replace("+", "%20")
                e(TAG, "encodedStr=$encodedStr")
                encodedStr = URLEncoder.encode(encodedStr, "UTF-8")
                e(TAG, "AgainencodedStr=$encodedStr")
                mUrl = urlPath + encodedStr
            } catch (e: UnsupportedEncodingException) {
                printStackTrace(e)
            }
            //            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
//stackoverflow.com/questions/14578530/how-to-open-display-documents-pdf-doc-without-external-app
            mBinding.webView?.loadUrl("http://docs.google.com/gview?embedded=true&url=$mUrl")
            //set the WebViewClient
//        https://stackoverflow.com/questions/27717214/android-webview-remove-pop-out-option-in-google-drive-doc-viewer
            mBinding.webView?.webViewClient = object : WebViewClient() {
                //once the page is loaded get the html element by class or id and through javascript hide it.
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    mBinding.webView?.loadUrl(
                        "javascript:(function() { " +
                                "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()"
                    )
                }
            }
        } else {
            mBinding.webView?.settings?.builtInZoomControls = true
            mBinding.webView?.settings?.displayZoomControls = false
            //new to set initial scale to fit screen
            mBinding.webView?.settings?.loadWithOverviewMode = true
            mBinding.webView?.settings?.useWideViewPort = true
            mBinding.webView?.loadUrl(mUrl)
            e("tag", "url==$mUrl")
        }
        mBinding.webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                mBinding.progressBar.progress = progress
                if (progress == 100)
                    mBinding.progressBar.visibility = View.GONE

                val userData = mPreferenceManager.getUserData()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    mBinding.webView.evaluateJavascript("load_data('"+userData?.fullname+"', '"+userData?.id+"', '"+userData?.walletAmount+"', '"+ Const.DEVICETYPE+"')", null)
                } else {
                    mBinding.webView.loadUrl("javascript:load_data('"+userData?.fullname+"', '"+userData?.id+"', '"+userData?.walletAmount+"', '"+ Const.DEVICETYPE+"')")
                }
            }
        }


        mBinding.webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                })
    }

    /** Instantiate the interface and set the context  */
    inner class WebAppInterface(private val mContext: Context) {

        /** Get callback from the web page  */
        @JavascriptInterface
        fun showToast(fromWeb: String) {
            LogHelper.d("callbackWeb", fromWeb)
            AlertMessage.showMessage(
                mContext,
                fromWeb,
                mContext.getString(R.string.ok),
                object : AlertMessageListener(){
                    override fun onPositiveButtonClick() {
                        super.onPositiveButtonClick()
                        finish()
                    }
                }
            )
        }
    }

    companion object {
        fun startActivity(
            mContext: Context,
            mUrl: String?,
            mTitle: String?,
            isPdfFile: Boolean,
            isOrientation: Boolean
        ) {
            val intent = Intent(mContext, WebViewActivity::class.java)
            intent.putExtra(Const.EXTRA_URI, mUrl)
            intent.putExtra(Intent.EXTRA_TITLE, mTitle)
            intent.putExtra(Intent.EXTRA_TEXT, isPdfFile)
            if (isOrientation)
                intent.putExtra(Intent.EXTRA_REFERRER, isOrientation)
            mContext.startActivity(intent)
        }
    }
}
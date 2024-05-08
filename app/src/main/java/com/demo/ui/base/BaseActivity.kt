package com.demo.ui.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputLayout
import com.demo.R
import com.demo.constants.Const
import com.demo.utils.*
import com.demo.utils.location.LocationHelper

//import kotlinx.android.synthetic.main.progress_view_layout.view.*
//import kotlinx.android.synthetic.main.toolbar.view.*


abstract class BaseActivity<MBinding: ViewBinding> : AppCompatActivity() {
    private var mLocation: Location? = null
    private var mLocationHelper: LocationHelper? = null
    lateinit var mPreferenceManager: PreferenceManager
    internal var mContext: Context = this
    private var progress: View? = null
    var TAG = this.javaClass.simpleName
    lateinit var mBinding: MBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferenceManager = PreferenceManager(mContext)
        mBinding = getViewBinding()
        //setContentView(mBinding.root)
    }


    val secureKey: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     *
     * In ViewBinding getting bindViews() override method, need to require override this bellow method
     *
     */
    override fun setContentView(view: View?) {
        super.setContentView(view)
        bindViews()
    }

    /*override fun setContentView(layoutResourceId: Int) {
        super.setContentView(layoutResourceId)
        //bindViews()
    }*/

    fun setUpToolBar(title: String, upIndicatorResourceId: Int) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //val toolbar = mBinding.root.toolbar

        if (toolbar != null) {
            setSupportActionBar(toolbar)

            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                if (upIndicatorResourceId != 0)
                    supportActionBar!!.setHomeAsUpIndicator(upIndicatorResourceId)
            }

            (findViewById<TextView>(R.id.toolbar_title)).text = title
            //mBinding.root.toolbar_title.text = title
        }

    }

    fun setTitle(title: String) {
        try {
            (findViewById<TextView>(R.id.toolbar_title)).text = title
            //mBinding.root.toolbar_title.text = title
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setUpToolBar(resourceId: Int) {
        try {
            setUpToolBar(getString(resourceId), 0)
        } catch (e: Exception) {
            setUpToolBar("", 0)
            e.printStackTrace()
        }

    }

    fun setUpToolBar(title: String) {
        setUpToolBar(title, 0)
    }

    fun disableBackIcon() {
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home) {
            /*  NavUtils.navigateUpFromSameTask(this)
              return true*/
            Util.hideKeyboard(mContext)
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun bindViews()
    abstract fun getViewBinding(): MBinding

    /**
     * set progress view visibility
     * */
    private fun setProgressLayout(visibility: Int) {
        try {
            progress = findViewById(R.id.progressView)
            //progress = mBinding.root.progressView
            progress?.visibility = visibility

//            Util.loadGifImage(mContext, findViewById(R.id.imgGif))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showProgressView() {
        setProgressLayout(View.VISIBLE)
    }

    fun hideProgressView() {
        setProgressLayout(View.GONE)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base!!))
    }

    var mConfigurationChangeReceiver: ConfigurationChangeReceiver = object : ConfigurationChangeReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            recreate()
        }
    }

    private var sessionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Const.ACTION_SESSION_EXPIRE) {
                val message = intent.getStringExtra(Intent.EXTRA_TEXT)

                logout()

                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(mContext)

                // set message of alert dialog
                dialogBuilder.setMessage(message)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(getString(R.string.login)) { _, _ ->
                        // start login activity
//                        val intent = Intent(mContext, LoginActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        finish()
                    }

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(getString(R.string.app_name))
                // show alert dialog
                alert.show()
                //if (mPreferenceManager.getUserLogin()) callApiForUserLogout()
            }
        }
    }

    private fun logout() {
        // clear Preference data
        val mPreferenceManager = PreferenceManager(mContext)
        if (!mPreferenceManager.getRememberMe()) mPreferenceManager.removePreference(PreferenceManager.USER_LOGIN_DATA)
        mPreferenceManager.setUserLogin(false)
        try {
            val notification =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notification.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * check edittext is empty or not
     */
    fun isEmptyEditText(
        editText: EditText
    ): Boolean {
        return TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })
    }

    /**
     * check edittext is empty or not and show error msg if it is empty
     */
    fun isEmptyEditText(
        editText: EditText,
        errorMessage: String,
    ): Boolean {
        return if (TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })) {
            editText.requestFocus()
            editText.error = errorMessage
            //AlertMessage.showMessage(editText.context, errorMessage)
            true
        } else
            false
    }

    /**
     * check edittext is empty or not and show error msg if it is empty
     */
    fun isEmptyEditText(
        mTextInputLayout: TextInputLayout,
        editText: EditText,
        errorMessage: String,
    ): Boolean {
        return if (TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })) {
            editText.requestFocus()
            mTextInputLayout.error = errorMessage
            //AlertMessage.showMessage(editText.context, errorMessage)
            true
        } else
            false
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(
            mConfigurationChangeReceiver,
            IntentFilter(Const.CUSTOM_INTENT_CHANGE_LANGUAGE)
        )
        registerReceiver(sessionReceiver, IntentFilter(Const.ACTION_SESSION_EXPIRE))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mConfigurationChangeReceiver)
        unregisterReceiver(sessionReceiver)
    }
}

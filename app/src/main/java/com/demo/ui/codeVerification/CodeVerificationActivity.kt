package com.demo.ui.codeVerification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.demo.BuildConfig
import com.demo.R
import com.demo.constants.Const
import com.demo.databinding.ActivityCodeVerificationBinding
import com.demo.databinding.ToolbarBinding
import com.demo.model.SendOtpModel
import com.demo.ui.base.BaseActivity
import com.demo.ui.wallpaper.home.HomeActivity
import com.demo.utils.*
import com.demo.utils.otpFillUtils.GenericKeyEvent
import com.demo.utils.otpFillUtils.GenericTextWatcher
import com.demo.viewModel.SendOtpViewModel
import com.demo.viewModel.VerifyOtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap

@AndroidEntryPoint
class CodeVerificationActivity : BaseActivity<ActivityCodeVerificationBinding>() {

    lateinit var mToolbarBinding: ToolbarBinding
    private var sendOtpModel: SendOtpModel? = null
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private val sendOtpViewModel: SendOtpViewModel by viewModels()

    override fun getViewBinding() = ActivityCodeVerificationBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

    override fun bindViews() {
        mToolbarBinding = mBinding.toolbarParent

        sendOtpModel = intent.getSerializableExtra(Intent.EXTRA_TEXT) as SendOtpModel?

        setUpToolBar("", R.drawable.ic_back_arrow_black)

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        mBinding.edt1.addTextChangedListener(GenericTextWatcher(mBinding.edt1, mBinding.edt2))
        mBinding.edt2.addTextChangedListener(GenericTextWatcher(mBinding.edt2, mBinding.edt3))
        mBinding.edt3.addTextChangedListener(GenericTextWatcher(mBinding.edt3, mBinding.edt4))
        mBinding.edt4.addTextChangedListener(GenericTextWatcher(mBinding.edt4, mBinding.edt5))
        mBinding.edt5.addTextChangedListener(GenericTextWatcher(mBinding.edt5, mBinding.edt6))
        mBinding.edt6.addTextChangedListener(GenericTextWatcher(mBinding.edt6, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        mBinding.edt1.setOnKeyListener(GenericKeyEvent(mBinding.edt1, null))
        mBinding.edt2.setOnKeyListener(GenericKeyEvent(mBinding.edt2, mBinding.edt1))
        mBinding.edt3.setOnKeyListener(GenericKeyEvent(mBinding.edt3, mBinding.edt2))
        mBinding.edt4.setOnKeyListener(GenericKeyEvent(mBinding.edt4, mBinding.edt3))
        mBinding.edt5.setOnKeyListener(GenericKeyEvent(mBinding.edt5, mBinding.edt4))
        mBinding.edt6.setOnKeyListener(GenericKeyEvent(mBinding.edt6, mBinding.edt5))

        mBinding.txtResendOtp.setOnClickListener {
            callApiSendOtp()
        }

        mBinding.btnContinue.setOnClickListener {
            Util.hideKeyboard(mContext)
            if (valid()) {
                if (Util.isConnectedToInternet(mContext)) {
                    callApiVerifyOtp()
                } else {
                    Util.showMessage(mContext, getString(R.string.error_no_internet))
                }
            }
        }

        setupVerifyOtpObserver()
        setupSendOtpObserver()
        setCode()
    }

    private fun setCode() {
        if (BuildConfig.DEBUG) {
            mBinding.edt1.setText("")
            mBinding.edt1.setText(sendOtpModel?.otp?.take(1))
            mBinding.edt2.setText("")
            mBinding.edt2.setText(sendOtpModel?.otp?.substring(1)?.take(1))
            mBinding.edt3.setText("")
            mBinding.edt3.setText(sendOtpModel?.otp?.substring(2)?.take(1))
            mBinding.edt4.setText("")
            mBinding.edt4.setText(sendOtpModel?.otp?.substring(3)?.take(1))
            mBinding.edt5.setText("")
            mBinding.edt5.setText(sendOtpModel?.otp?.substring(4)?.take(1))
            mBinding.edt6.setText("")
            mBinding.edt6.setText(sendOtpModel?.otp?.substring(5)?.take(1))
            mBinding.edt6.text?.let { mBinding.edt6.setSelection(it.length) }
        }
    }

    /*
    * Api call for verify otp
    */
    private fun callApiVerifyOtp(){
        val hashMap = HashMap<String, Any?>()

        hashMap[Const.PARAM_PHONE] = sendOtpModel?.phone.toString()
        hashMap[Const.PARAM_OTP] = mBinding.edt1.text?.trim().toString() + mBinding.edt2.text?.trim().toString() + mBinding.edt3.text?.trim().toString() + mBinding.edt4.text?.trim().toString() + mBinding.edt5.text?.trim().toString() + mBinding.edt6.text?.trim().toString()


        verifyOtpViewModel.callApiVerifyOtp(hashMap)
    }

    /*
    * Verify Otp api observer
    */
    private fun setupVerifyOtpObserver() {
        verifyOtpViewModel.apiVerifyOtp().observe(this, {
            when(it.status) {
                Const.Status.SUCCESS -> {
                    mBinding.lyProgress.progressView.visibility = View.GONE
                    LogHelper.d("mData", it.toString())
                    it.data?.let { response ->
                        LogHelper.d("verifyOtpResponse", response.toString())
                        if (response.isSuccess && response.code == Const.SUCCESS) {
                            mPreferenceManager.setUserLogin(true)
                            HomeActivity.startActivity(mContext)
                            finishAffinity()
                        } else {
                            Util.showMessage(mContext, response.message)
                        }
                    }
                }
                Const.Status.LOADING -> {
                    mBinding.lyProgress.progressView.visibility = View.VISIBLE
                }
                Const.Status.ERROR -> {
                    //Handle Error
                    mBinding.lyProgress.progressView.visibility = View.GONE
                    Util.showMessage(mContext, it.message)
                }
            }
        })
    }


    /*
    * Api call for send otp
    */
    private fun callApiSendOtp(){
        val hashMap = HashMap<String, Any?>()

        hashMap[Const.PARAM_PHONE] = sendOtpModel?.phone.toString()


        sendOtpViewModel.callApiSendOtp(hashMap)
    }

    /*
    * Send Otp api observer
    */
    private fun setupSendOtpObserver() {
        sendOtpViewModel.apiSendOtp().observe(this, {
            when(it.status) {
                Const.Status.SUCCESS -> {
                    mBinding.lyProgress.progressView.visibility = View.GONE
                    LogHelper.d("mData", it.toString())
                    it.data?.let { response ->
                        LogHelper.d("sendOtpResponse", response.toString())
                        Util.showMessage(mContext, response.message)
                        if (response.isSuccess && response.code == Const.SUCCESS) {
                            if (response.result != null) {
                                sendOtpModel = response.result
                                setCode()
                            }
                        }
                    }
                }
                Const.Status.LOADING -> {
                    mBinding.lyProgress.progressView.visibility = View.VISIBLE
                }
                Const.Status.ERROR -> {
                    //Handle Error
                    mBinding.lyProgress.progressView.visibility = View.GONE
                    Util.showMessage(mContext, it.message)
                }
            }
        })
    }


    /**
     *
     * Validation
     *
     * */
    private fun valid(): Boolean {
        val verificationCode = mBinding.edt1.text?.trim().toString() + mBinding.edt2.text?.trim().toString() + mBinding.edt3.text?.trim().toString() + mBinding.edt4.text?.trim().toString() + mBinding.edt5.text?.trim().toString() + mBinding.edt6.text?.trim().toString()
        return when {
            verificationCode.isEmpty() -> {
                Util.showMessage(mContext, getString(R.string.error_otp))
                false
            }
            verificationCode.length < 6 -> {
                Util.showMessage(mContext, getString(R.string.error_valid_otp))
                false
            }
            else -> true
        }
    }


    companion object {
        fun startActivity(mContext: Context, sendOtpModel: SendOtpModel?) {
            val intent = Intent(mContext, CodeVerificationActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, sendOtpModel)
            mContext.startActivity(intent)
        }
    }
}
package com.demo.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.demo.databinding.ActivitySplashBinding
import com.demo.ui.base.BaseActivity
import com.demo.ui.wallpaper.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getViewBinding() =  ActivitySplashBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

    override fun bindViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (mPreferenceManager.getUserLogin())
                HomeActivity.startActivity(mContext)
            else
                HomeActivity.startActivity(mContext)


            // close this activity
            finish()
        }, 3000)
    }
}
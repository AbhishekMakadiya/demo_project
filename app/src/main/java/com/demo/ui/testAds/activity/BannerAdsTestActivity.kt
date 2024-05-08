package com.demo.ui.testAds.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.demo.ui.base.BaseActivity
import com.demo.databinding.ActivityBannerAdsTestBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class BannerAdsTestActivity : BaseActivity<ActivityBannerAdsTestBinding>() {

    private var mInterstitialAd: InterstitialAd? = null
    lateinit var adRequest: AdRequest

    override fun getViewBinding() =  ActivityBannerAdsTestBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

    override fun bindViews() {
        adRequest = AdRequest.Builder().build()
        mBinding.adView.loadAd(adRequest)

        mBinding.adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                super.onAdClosed()
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                // Code to be executed when an ad request fails.
                mBinding.adView.loadAd(adRequest)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                super.onAdOpened()
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }


        mBinding.mButton.setOnClickListener {

            mBinding.adView.loadAd(adRequest)
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.adView.loadAd(adRequest)
    }

    companion object {
        fun startActivity(mContext: Context) {
            val intent = Intent(mContext, BannerAdsTestActivity::class.java)
            mContext.startActivity(intent)
        }
    }
}
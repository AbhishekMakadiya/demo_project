package com.demo.ui.advertisement

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.demo.R
import com.demo.databinding.ItemAdsBinding
import com.demo.model.AdsModel
import com.demo.constants.Const
import java.util.*

class AdsViewFragment : Fragment() {
    private var _binding: ItemAdsBinding? = null
    private val binding get() = _binding!!
    var ads: AdsModel? = null
    private var radius = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ItemAdsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.imgAds.setOnClickListener { mSendIntent(ads?.adLink) }
        try {
            if (ads?.adBannerUrl != null) {
                if (radius > 0 ){
                    val options: RequestOptions = RequestOptions()
                        .placeholder(R.drawable.ic_placeholder).centerInside()
                        .transform(RoundedCorners(radius))
                        .error(R.drawable.ic_placeholder)
                    Glide.with(this).load(ads?.adBannerUrl).apply(options).into(binding.imgAds)
                } else {
                    val options: RequestOptions = RequestOptions()
                        .placeholder(R.drawable.ic_placeholder).centerInside()
                        .error(R.drawable.ic_placeholder)
                    Glide.with(this).load(ads?.adBannerUrl).apply(options).into(binding.imgAds)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun mSendIntent(redirect: String?) {
        try {
            if (!TextUtils.isEmpty(redirect) && redirect?.isNotEmpty()!!) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(redirect)
                Objects.requireNonNull(activity)?.startActivity(i)
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ads = arguments?.getSerializable(Intent.EXTRA_TEXT) as AdsModel?
        radius = arguments?.getInt(Const.EXTRA_DATA)!!
    }
}
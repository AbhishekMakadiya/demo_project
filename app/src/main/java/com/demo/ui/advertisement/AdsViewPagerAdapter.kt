package com.demo.ui.advertisement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.demo.model.AdsModel
import com.demo.constants.Const
import java.util.*

class AdsViewPagerAdapter(fm: FragmentManager?, var result: ArrayList<AdsModel?>, var radius: Int) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putSerializable(Intent.EXTRA_TEXT, result[position])
        bundle.putInt(Const.EXTRA_DATA, radius)
        val adsViewFragment = AdsViewFragment()
        adsViewFragment.arguments = bundle
        return adsViewFragment
    }

    override fun getCount(): Int {
        return result.size //three fragments
    }
}
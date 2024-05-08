package com.demo.ui.advertisement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.demo.databinding.FragmentAdsBinding
import com.demo.model.AdsModel
import com.demo.constants.Const
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager
import com.demo.utils.Util
import com.demo.viewModel.AdsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager

@AndroidEntryPoint
class AdsFragment : Fragment() {

    private val adsListViewModel: AdsListViewModel by viewModels()

    private var mArrayList: ArrayList<AdsModel?> = ArrayList()

    private var _binding: FragmentAdsBinding? = null
    private val binding get() = _binding!!
    private val mViewPagerAdapter : AdsViewPagerAdapter? = null

    private var radius = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            radius = it.getInt(RADIUS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdsBinding.inflate(inflater, container, false)
        val view = binding.root
        bindViews()
        return view
    }

    private fun bindViews() {
        setUpGetAdsListObserver()
        callAPIAds()
        /*val response = ArrayList<AdsModel?>()
        response.add(AdsModel("0", "", "", "", R.drawable.banner_1, "0"))
        response.add(AdsModel("1", "", "", "", R.drawable.banner_2, "1"))
        response.add(AdsModel("2", "", "", "", R.drawable.banner_3, "2"))
        response.add(AdsModel("3", "", "", "", R.drawable.banner_4, "3"))
        response.add(AdsModel("4", "", "", "", R.drawable.banner_5, "4"))*/

        val mViewPagerAdapter =
            AdsViewPagerAdapter(
                childFragmentManager,
                mArrayList,
                radius
            )
        //binding.indicator.noOfPages = response.size
        binding.mPager.adapter = mViewPagerAdapter
        binding.mPager.setInterval(3000)
        binding.mPager.setDirection(AutoScrollViewPager.Direction.RIGHT)
        binding.mPager.setCycle(true)
        binding.mPager.setBorderAnimation(true)
        binding.mPager.setSlideBorderMode(AutoScrollViewPager.SlideBorderMode.TO_PARENT)
        binding.mPager.startAutoScroll()

        binding.mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                binding.indicator.onPageChange(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }


    /**
     *
     *  Call Api for get Ads List
     *
     * */
    private fun callAPIAds() {
        val mPreferenceManager = PreferenceManager(requireContext())
        if (!Util.isConnectedToInternet(context)) {
            return
        }
        val hashMap = HashMap<String, Any?>()
        hashMap[Const.PARAM_USER_ID] = mPreferenceManager.getUserId().toString()

        adsListViewModel.callApiGetAdsList(hashMap)
    }

    /**
     *
     *  Ads List Api observer
     *
     * */
    private fun setUpGetAdsListObserver() {
        adsListViewModel.apiGetAdsList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Const.Status.SUCCESS -> {
                    LogHelper.d("get_ads_list_response", it.toString())
                    it.data?.let { response ->
                        if(response.isSuccess && response.code == Const.SUCCESS){
                            if (response.result != null && response.result!!.size > 0) {
                                binding.mPager.visibility = View.VISIBLE
                                binding.indicator.visibility = View.VISIBLE
                                mArrayList.clear()
                                response.result?.let { it1 -> mArrayList.addAll(it1) }
                                val mViewPagerAdapter =
                                    AdsViewPagerAdapter(
                                        childFragmentManager,
                                        mArrayList,
                                        radius
                                    )
                                binding.indicator.noOfPages = mArrayList.size
                                binding.mPager.adapter = mViewPagerAdapter
                            } else {
                                binding.mPager.visibility = View.GONE
                                binding.indicator.visibility = View.GONE
                            }
                        } else {
                            LogHelper.d("get_ads_list_response", it.toString())
                        }
                    }
                }
                Const.Status.LOADING -> {

                }
                Const.Status.ERROR -> {
                    //Handle Error
                    val msg = it.message
                    LogHelper.d("get_ads_list_response", it.toString())
                }
            }
        })
    }

    companion object {
        const val RADIUS = "RADIUS"

        @JvmStatic
        fun newInstance(radius: Int) =
            AdsFragment().apply {
                arguments = Bundle().apply {
                    //putString(Intent.EXTRA_TEXT, message)
                    putInt(RADIUS, radius)
                }
            }
    }

}

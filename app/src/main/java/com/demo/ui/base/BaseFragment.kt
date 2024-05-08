package com.demo.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.demo.R
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager

abstract class BaseFragment<MBinding: ViewBinding> : Fragment() {
    var mContext: Context? = null
    lateinit var mPreferenceManager: PreferenceManager
    val TAG = this.javaClass.simpleName
    var mView: View? = null

    lateinit var mBinding: MBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferenceManager = PreferenceManager(mContext!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mBinding = getViewBindingFragment()
        mContext = context
    }

    abstract fun bindViews(view: View)
    abstract fun getViewBindingFragment(): MBinding               // reference : https://dev.to/enyason/how-to-set-up-a-base-fragment-class-with-viewbinding-and-viewmodel-on-android-57g1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //mBinding = getViewBindingFragment()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mView = view
        bindViews(view)
    }

    // set up toolbar
    protected fun setUpToolBar(msg: String) {
        try {
            val txtToolbarTitle = activity!!.findViewById<TextView>(R.id.toolbar_title)
            txtToolbarTitle.text = msg
        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
        }
    }

    /**
     * set progress view visibility
     * */
    private fun setProgressLayout(visibility: Int) {
        try {
            if (mView != null) {
                val progress: View = mView!!.findViewById(R.id.progressView)
                progress.visibility = visibility
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showProgressView() {
        return setProgressLayout(View.VISIBLE)
    }

    fun hideProgressView() {
        return setProgressLayout(View.GONE)
    }
}

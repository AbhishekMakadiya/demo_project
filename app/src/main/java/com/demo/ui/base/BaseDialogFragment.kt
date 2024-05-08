package com.demo.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.demo.R
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager

abstract class BaseDialogFragment : AppCompatDialogFragment() {
    var mContext: Context? = null
    lateinit var preferenceManager: PreferenceManager
    val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(mContext!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    abstract fun bindViews()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
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

    /*  fun showProgressView() {
          if (activity is BaseActivity)
              (activity as BaseActivity).showProgressView()
      }

      fun hideProgressView() {
          if (activity is BaseActivity)
              (activity as BaseActivity).hideProgressView()
      }*/
}

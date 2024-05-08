package com.demo.pagination

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.api.NetworkUtils.getErrorMessage
import com.demo.api.NoConnectivityException
import com.demo.utils.alertmessages.AlertMessage
import java.net.UnknownHostException


class RecyclerAdapterHelper<T>(
        private val mContext: Context,
        private val errorLayout: View?,
        val arrayList: ArrayList<T?>?,
        private val recyclerView: RecyclerView,
        private val progress: View?,
        private val callBack: RecyclerAdapterHelperCallback
) {

    private var errTitle: TextView? = null
    private var errMessage: TextView? = null
    private var errbutton: Button? = null

    init {
        if (errorLayout != null) {
            errTitle = errorLayout.findViewById(R.id.txtTitle)
            errMessage = errorLayout.findViewById(R.id.txtMsg)
            errbutton = errorLayout.findViewById(R.id.btnRetry)

            errbutton?.setOnClickListener {
                errorLayout.visibility = View.GONE
                callBack.onRetryPage()
            }
        }
    }


    fun resetValues() {
        arrayList?.clear()
    }

    fun setProgressLayout(visibility: Int) {
        if (progress != null) {

            try {
                progress.visibility = visibility
                handleErrorView(View.GONE, "", View.GONE, View.GONE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun setSuccessResponse(isSuccess: Boolean, arrayList1: List<T>?, message: String) {
        setProgressLayout(View.GONE)


        if (isSuccess && !arrayList1.isNullOrEmpty()) {
            handleErrorView(View.GONE, message, View.GONE, View.GONE)
            arrayList?.clear()
            arrayList?.addAll(arrayList1)
            recyclerView.adapter?.notifyDataSetChanged()

        } else {
            handleErrorView(View.VISIBLE, message, View.GONE, View.GONE)
        }

    }

    fun setFailureResponse(t: Throwable) {
        setProgressLayout(View.GONE)
        handleErrorView(View.GONE, "", View.GONE, View.GONE)

        if (t is NoConnectivityException || t is UnknownHostException) {
            if (arrayList!!.size > 0)
                AlertMessage.showMessage(mContext, mContext.getString(R.string.error_no_internet))
            else
                handleErrorView(
                    View.VISIBLE,
                    mContext.getString(R.string.error_no_internet),
                    View.VISIBLE,
                    View.VISIBLE
                )
        } else {
            if (arrayList!!.size > 0)
                AlertMessage.showMessage(mContext, getErrorMessage(mContext, t))
            else
                handleErrorView(View.VISIBLE, getErrorMessage(mContext, t), View.VISIBLE, View.VISIBLE)

        }
    }

    private fun handleErrorView(visibility: Int, message: String, btnVisibility: Int, titleVisibility: Int) {
        if (errorLayout != null) {
            errorLayout.visibility = visibility
            errMessage?.text = message
            errbutton?.visibility = btnVisibility
            errTitle?.visibility = titleVisibility
        }
    }

    interface RecyclerAdapterHelperCallback {
        fun onRetryPage()
    }
}

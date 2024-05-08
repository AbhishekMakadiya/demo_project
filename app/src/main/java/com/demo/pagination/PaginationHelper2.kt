package com.demo.pagination

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.api.NetworkUtils
import com.demo.api.NoConnectivityException
import com.demo.R
import com.demo.utils.alertmessages.AlertMessage
import java.util.*


class PaginationHelper2<T>(private val mContext: Context, private val errorLayout: View?, private val arrayList: ArrayList<T?>?, private val recyclerView: RecyclerView, private val layoutManager: LinearLayoutManager, private val progress: View?, private val paginationCallback: PaginationCallback) {

    private var isLoadingData = false
    private var canIncreasePageSize = true
    var PAGE_INDEX = 0

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
                paginationCallback.onRetryPage(PAGE_INDEX)
            }
        }
    }

    fun setPaginationRecycler() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                callOnScrollListener(dy)
            }
        })

        //  paginationCallback.onNewPage(PAGE_INDEX);
    }

    fun resetValues() {
        isLoadingData = false
        canIncreasePageSize = true
        PAGE_INDEX = 0
    }

    fun setProgressLayout(visibility: Int) {
        if (progress != null) {
            if (PAGE_INDEX == 0) {
                try {
                    progress.visibility = visibility
//                    Util.loadGifImage(mContext, progress.findViewById(R.id.imgGif))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                progress.visibility = View.GONE
            }
        }
    }


    private fun callOnScrollListener(dy: Int) {
        val totalItem = layoutManager.itemCount
        val prevItem = layoutManager.findFirstVisibleItemPosition()
        val currentItem = layoutManager.childCount
        //Here the right side of || operator is used because when screen is too large and all 20 items are loaded then the pagination will never called. So its helps to load more data if there are available.
        if (/*dy > 0 && */prevItem + currentItem >= totalItem && !isLoadingData || dy == 0 && arrayList!!.size == currentItem && !isLoadingData) {
            if (canIncreasePageSize) {
                isLoadingData = true
                PAGE_INDEX += 1
                paginationCallback.onNewPage(PAGE_INDEX)

            } else {
                if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] != null) {
                    arrayList.add(null)
                    recyclerView.post { recyclerView.adapter?.notifyDataSetChanged() }

                }
//                if (!recyclerView.canScrollVertically(1)) {
                isLoadingData = true
                paginationCallback.onNewPage(PAGE_INDEX)
//                }
            }
        }
    }

    fun setSuccessResponse(isSuccess: Boolean, arrayList1: List<T?>?, message: String) {
        setProgressLayout(View.GONE)
        handleErrorView(View.GONE, message, View.GONE, View.GONE)

        if (isSuccess) {
            isLoadingData = false
            canIncreasePageSize = true

            if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null)
                arrayList.removeAt(arrayList.size - 1)

            if (arrayList1 != null)
                arrayList.addAll(arrayList1)

            // Comment Next 3 Line if page Size is not fixed
            if (arrayList.size < PaginationHelper.PAGE_SIZE)
                isLoadingData = true
            else
                arrayList.add(null)
            recyclerView.adapter?.notifyDataSetChanged()

            if (PAGE_INDEX == 0) {
                recyclerView.scrollToPosition(0)
            }

        } else {
            if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null) {
                arrayList.removeAt(arrayList.size - 1)
                recyclerView.adapter?.notifyItemRemoved(arrayList.size)
            }
        }

        if (PAGE_INDEX == 0 && arrayList.isEmpty()) {
            handleErrorView(View.VISIBLE, message, View.GONE, View.GONE)
        }
    }

    fun setFailureResponse(t: Throwable) {
        setProgressLayout(View.GONE)
        handleErrorView(View.GONE, "", View.GONE, View.GONE)

        if (t is NoConnectivityException) {
            //only for internet failure we are allow users to load more data if previous data available.
            //In other cases we don't need to load more data because suppose there is parse error then we don't need to allow user to load it again.
            if (arrayList!!.size > 0) {
                canIncreasePageSize = false
                isLoadingData = false
            }

            if (PAGE_INDEX == 0) {
                handleErrorView(View.VISIBLE, mContext.getString(R.string.error_no_internet), View.VISIBLE, View.VISIBLE)
            } else {
                AlertMessage.showMessage(mContext, mContext.getString(R.string.error_no_internet))
            }
        } else {
            if (PAGE_INDEX == 0) {
                handleErrorView(View.VISIBLE, NetworkUtils.getErrorMessage(mContext, t), View.VISIBLE, View.VISIBLE)
            }
        }

        if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null) {
            arrayList.removeAt(arrayList.size - 1)
            recyclerView.adapter?.notifyItemRemoved(arrayList.size)
        }
    }

    fun handleErrorView(visibility: Int, message: String, btnVisibility: Int, titleVisibility: Int) {
        if (errorLayout != null) {
            errorLayout.visibility = visibility
            errMessage?.text = message
            errbutton?.visibility = btnVisibility
            errTitle?.visibility = titleVisibility
        }
    }

    interface PaginationCallback {
        fun onNewPage(pageNumber: Int)
        fun onRetryPage(pageNumber: Int)
    }

}

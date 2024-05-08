package com.demo.pagination

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.api.NetworkUtils.getErrorMessage
import com.demo.R
import com.demo.utils.alertmessages.AlertMessage
import java.io.IOException
import java.util.*


class TokenPaginationHelper<T>(
        private val mContext: Context,
        private val errorLayout: View?,
        private val arrayList: ArrayList<T?>?,
        private val recyclerView: RecyclerView,
        private val layoutManager: LinearLayoutManager,
        private val progress: View?,
        private val paginationCallback: PaginationCallback
) {

    private var isLoadingData = false
    private var canIncreasePageSize = true
    var nextPageToken: String? = null
    private var errTitle: TextView? = null
    private var errMessage: TextView? = null
    private var errbutton: Button? = null

    companion object {
        const val PAGE_SIZE = 50
        const val KEY_PAGE_NUMBER = "page"
        const val KEY_PAGE_SIZE = "size"
    }

    init {
        if (errorLayout != null) {
            errTitle = errorLayout.findViewById(R.id.txtTitle)
            errMessage = errorLayout.findViewById(R.id.txtMsg)
            errbutton = errorLayout.findViewById(R.id.btnRetry)

            errbutton?.setOnClickListener {
                errorLayout.visibility = View.GONE
                paginationCallback.onRetryPage(nextPageToken)
            }
        }
        setPaginationRecycler()
    }

    private fun setPaginationRecycler() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                callOnScrollListener(dy)
            }
        })

        //  paginationCallback.onNewPage(PAGE_INDEX);
    }

    fun resetValues() {
        arrayList?.clear()
        isLoadingData = false
        canIncreasePageSize = true
        nextPageToken = null
    }

    fun setProgressLayout(visibility: Int) {
        if (progress != null) {
            if (nextPageToken == null) {
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
        if (dy > 0 && prevItem + currentItem >= totalItem && !isLoadingData || dy == 0 && arrayList!!.size == currentItem && !isLoadingData) {
            if (canIncreasePageSize) {
                isLoadingData = true
                if (!nextPageToken.isNullOrEmpty()) {
                    paginationCallback.onNewPage(nextPageToken)
                }


            } else {
                if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] != null) {
                    if (nextPageToken != null && nextPageToken!!.isNotEmpty())
                        arrayList.add(null)
                    recyclerView.post { recyclerView.adapter?.notifyDataSetChanged() }

                }
                if (!recyclerView.canScrollVertically(1)) {
                    isLoadingData = true
                    if (!nextPageToken.isNullOrEmpty()) {
                        paginationCallback.onNewPage(nextPageToken)
                    }
                }
            }
        }
    }

    fun setSuccessResponse(nextToken: String, arrayList1: List<T?>?, message: String) {
        setProgressLayout(View.GONE)
        handleErrorView(View.GONE, message, View.GONE, View.GONE)

        nextPageToken = null.takeIf { nextToken.isEmpty() } ?: nextToken
        val isSuccess = nextToken.isNotEmpty()

        if (isSuccess) {
            isLoadingData = false
            canIncreasePageSize = true

            if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null)
                arrayList.removeAt(arrayList.size - 1)

            if (arrayList1 != null)
                arrayList.addAll(arrayList1)

            if (arrayList.size < PAGE_SIZE)
                isLoadingData = true
            else {
                arrayList.add(null)
            }
            recyclerView.adapter?.notifyDataSetChanged()

        } else {
            if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null)
                arrayList.removeAt(arrayList.size - 1)

            if (arrayList1 != null)
                arrayList.addAll(arrayList1)

            recyclerView.adapter?.notifyDataSetChanged()
        }

        if (nextPageToken == null && arrayList.isEmpty()) {
            handleErrorView(View.VISIBLE, message, View.GONE, View.GONE)
        }
    }

    fun setFailureResponse(t: Throwable) {
        setProgressLayout(View.GONE)
        handleErrorView(View.GONE, "", View.GONE, View.GONE)

        if (t is IOException) {
            //only for internet failure we are allow users to load more data if previous data available.
            //In other cases we don't need to load more data because suppose there is parse error then we don't need to allow user to load it again.
            if (arrayList!!.size > 0) {
                canIncreasePageSize = false
                isLoadingData = false
            }

            if (nextPageToken == null && arrayList.size == 0) {
                handleErrorView(
                    View.VISIBLE,
                    mContext.getString(R.string.error_no_internet),
                    View.VISIBLE,
                    View.VISIBLE
                )
            } else {
                AlertMessage.showMessage(mContext, mContext.getString(R.string.error_no_internet))
            }
        } else {
            if (nextPageToken == null) {
                handleErrorView(View.VISIBLE, getErrorMessage(mContext, t), View.VISIBLE, View.VISIBLE)
            } else
                AlertMessage.showMessage(mContext, getErrorMessage(mContext, t))
        }

        if (arrayList!!.size > 0 && arrayList[arrayList.size - 1] == null) {
            arrayList.removeAt(arrayList.size - 1)
            recyclerView.adapter?.notifyItemRemoved(arrayList.size)
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

    interface PaginationCallback {
        fun onNewPage(token: String?)
        fun onRetryPage(token: String?)
    }

}

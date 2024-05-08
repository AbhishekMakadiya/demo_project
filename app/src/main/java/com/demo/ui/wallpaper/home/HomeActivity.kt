package com.demo.ui.wallpaper.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.api.Api
import com.demo.api.NetworkUtils.getWebUrl
import com.demo.ui.base.BaseActivity
import com.demo.ui.webview.WebViewActivity
import com.demo.R
import com.demo.constants.Const
import com.demo.databinding.ActivityHomeBinding
import com.demo.model.CategoryModel
import com.demo.pagination.PaginationHelper2
import com.demo.utils.itemDecoration.SpacesItemDecoration
import com.demo.viewModel.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(), View.OnClickListener, HomeCategoryAdapter.Callback {

    @Inject
    lateinit var mAdapter: HomeCategoryAdapter
    private var mArrayListCategory: ArrayList<CategoryModel?> = ArrayList()
    var paginationHelper: PaginationHelper2<CategoryModel?>? = null
    // private var mLayoutManager: GridLayoutManager? = null
    private var mLayoutManager: LinearLayoutManager? = null

    private val categoryViewModel: CategoryViewModel by viewModels()


    override fun getViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

    override fun bindViews() {

        //  setUpToolBar(getString(R.string.browse_players), R.drawable.ic_back)
        val spanCount = 3 // 3 columns
        val spacing = resources.getDimensionPixelSize(R.dimen._12dp)

        mLayoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false)

        mAdapter = HomeCategoryAdapter(this)
        mAdapter.setAdapterData(mContext,mArrayListCategory)

        with(mBinding.rvCategory) {
            addItemDecoration(SpacesItemDecoration(spacing))
            //addItemDecoration(SpacesGridItemDecoration(spanCount, spacing, true))
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        // set up pagination - browse
        /*paginationHelper = PaginationHelper2(
            this, binding.lyNoInternet.noInternetLayout,
            mArrayList,
            binding.mRecyclerViewBrowsePlayer,
            mLayoutManager!!,
            binding.lyProgress.progressView, this
        )*/
        /* paginationHelper = PaginationHelper(
             this, mBinding.lyNoInternet.noInternetLayout,
             mArrayList,
             mBinding.mRecyclerViewBrowsePlayer,
             mLayoutManager!!,
             mBinding.lyProgress.progressView,
             this
         )*/
        //paginationHelper?.setPaginationRecycler()
        //mBinding.mRecyclerViewBrowsePlayer.scrollToPosition(0)

//        CallApiCategoryList()

        mArrayListCategory.add(CategoryModel("Category1"))
        mArrayListCategory.add(CategoryModel("Category2"))
        mArrayListCategory.add(CategoryModel("Category3"))
        mArrayListCategory.add(CategoryModel("Category4"))
        mArrayListCategory.add(CategoryModel("Category5"))
        mArrayListCategory.add(CategoryModel("Category6"))
        mArrayListCategory.add(CategoryModel("Category7"))
        mArrayListCategory.add(CategoryModel("Category8"))



        mAdapter.setAdapterData(mContext,mArrayListCategory)
         mAdapter.notifyDataSetChanged()
    }

    /*
        * Api CallApiCategoryList
        */
    private fun CallApiCategoryList() {
        val hashMap = java.util.HashMap<String, Any?>()
        hashMap["search_string"] = ""
        categoryViewModel.callApiCategoryList(hashMap, mContext)
    }


    /**
     *
     *  get browse team
     *
     * */
    private fun setUpCategoryListObserver () {

        categoryViewModel.apiCategoryList().observe(this) {
            when (it.status) {
                Const.Status.SUCCESS -> {
                    mBinding.lyProgress.progressView.visibility = View.GONE
                    it.data?.let { response ->
                        Log.e("data", response.result.toString())
                        if (response.isSuccess) {
                            if (response.result != null) {
                                paginationHelper?.setSuccessResponse(
                                    response.isSuccess,
                                    response.result,
                                    response.message
                                )
                            }
                        }
                    }
                }
                Const.Status.LOADING -> {
                    paginationHelper?.setProgressLayout(View.VISIBLE)
                }
                Const.Status.ERROR -> {
                    //Handle Error
                    paginationHelper?.setProgressLayout(View.GONE)
                    Toast.makeText(mContext, "" + it.message, Toast.LENGTH_SHORT).show()
                    // paginationHelper?.setFailureResponse(it.message)
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when(v) {

        }
    }

    private fun openWebPage(path: String, title: String) {
        WebViewActivity.startActivity(mContext, getWebUrl(Api.MAINURL, path), title, isPdfFile = false, isOrientation = false)
    }

    override fun onItemClick(categoryModel: CategoryModel?) {
        Toast.makeText(mContext, categoryModel?.name, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun startActivity(mContext: Context) {
            val intent = Intent(mContext, HomeActivity::class.java)
            mContext.startActivity(intent)
        }
    }




}
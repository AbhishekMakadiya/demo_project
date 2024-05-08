package com.demo.ui.wallpaper.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.constants.Const
import com.demo.databinding.ItemCategoryBinding
import com.demo.databinding.ItemProgressBinding
import com.demo.model.CategoryModel
import com.demo.pagination.ProgressViewHolder
import com.demo.utils.LogHelper
import javax.inject.Inject

class HomeCategoryAdapter @Inject constructor(val callback: Callback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mContext: Context? = null
    var mArrayList: ArrayList<CategoryModel?> = ArrayList()

    fun setAdapterData(mContext: Context?, mArrayList: ArrayList<CategoryModel?>) {
        this.mContext = mContext
        this.mArrayList = mArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Const.ITEM_TYPE_LOADING -> {
                return ProgressViewHolder(
                    ItemProgressBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            Const.ITEM_TYPE_CONTENT -> {
                return MyViewHolder(
                    ItemCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return MyViewHolder(
                    ItemCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            if (position != -1) {
                val mData = mArrayList[position]
                if (mData != null)
                    holder.bindViews(mData, mContext)
            }
        }
    }


    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            mArrayList[position] == null -> Const.ITEM_TYPE_LOADING
            else -> Const.ITEM_TYPE_CONTENT
        }
    }

    internal inner class MyViewHolder(val mBinding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindViews(mData: CategoryModel, mContext: Context?) {
            mBinding.tvMenuName.text = mData.name.toString()
        }

        init {
            mBinding.root.setOnClickListener {
                LogHelper.d("TAGCHECK", mArrayList[adapterPosition].toString())
                if (adapterPosition != -1) {
                    callback.onItemClick(mArrayList[adapterPosition])
                }
            }

        }
    }

    interface Callback {
        fun onItemClick(categoryModel: CategoryModel?)
    }
}
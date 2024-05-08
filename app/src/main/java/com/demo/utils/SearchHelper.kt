package com.demo.utils

import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.demo.R

class SearchHelper(val mContext: Context, private val searchMenu: MenuItem, val onSearchCallback: OnSearchCallback) {

    val TAG = this.javaClass.simpleName
    lateinit var searchView: SearchView
    var searchText: String = ""

    fun getSearch() {
        searchView = searchMenu.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG, "submit = $query")
                if (query.trim() != searchText) {
                    searchText = query.trim()
                    onSearchCallback.onSearchQuery(searchText)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText!!
                newText.let { onSearchCallback.onSearchQuery(it) }
                return true
            }
        })

        searchMenu.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                Log.i(TAG, "expand")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                Log.i(TAG, "collapse")
                if (!searchText.trim().isEmpty()) {
                    searchText = ""
                    onSearchCallback.onSearchQuery(searchText)
                }
                onSearchCallback.onCollapseActionMenu()
                return true
            }

        })
    }

    fun setHint(hint: String) {
        searchView.queryHint = hint
    }

    fun setHintColor(colorResource: Int) {
        try {
            searchView.findViewById<EditText>(R.id.search_src_text).setHintTextColor(ContextCompat.getColor(mContext, colorResource))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnSearchCallback {
        fun onSearchQuery(search: String)
        fun onCollapseActionMenu()
    }
}
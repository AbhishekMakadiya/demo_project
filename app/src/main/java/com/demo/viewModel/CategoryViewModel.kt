package com.demo.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.api.ApiHelper
import com.demo.api.NetworkUtils
import com.demo.model.CategoryModel
import com.demo.model.Resource
import com.demo.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val apiHelper: ApiHelper)  : ViewModel() {

    private val categorylistResult = MutableLiveData<Resource<Response<ArrayList<CategoryModel?>>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for Login
    */
    fun callApiCategoryList(param: HashMap<String, Any?>,mContext: Context) {
        categorylistResult.postValue(Resource.loading(null))
        viewModelScope.launch {
            apiHelper.apiCategoryList(param)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it.subscribe({
                                response -> categorylistResult.postValue(Resource.success(response)) },
                            { throwable ->
                                categorylistResult.postValue(
                                    Resource.error(
                                        NetworkUtils.getErrorMessage(mContext, throwable),
                                        null
                                    )
                                )
                            })
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun apiCategoryList(): MutableLiveData<Resource<Response<ArrayList<CategoryModel?>>>> {
        return categorylistResult
    }

}
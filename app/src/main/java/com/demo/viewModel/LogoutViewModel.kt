package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.Response
import com.demo.repository.LogoutRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val logoutRepository: LogoutRepository)  : ViewModel() {

    private val logoutDataList = MutableLiveData<Resource<Response<Any>>>()
    private val compositeDisposable = CompositeDisposable()


    /*
    * Api call for Logout
    */
    fun callApiLogout(param: HashMap<String, Any?>) {
        logoutDataList.postValue(Resource.loading(null))
        viewModelScope.launch {
            logoutRepository.apiLogout(param)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())?.let {
                        compositeDisposable.add(
                                it
                                        .subscribe({ response ->
                                            logoutDataList.postValue(Resource.success(response))
                                        }, { throwable ->
                                            logoutDataList.postValue(
                                                    Resource.error(
                                                            throwable.message.toString(),
                                                            null
                                                    )
                                            )
                                            LogHelper.d("mDataError", throwable.message.toString())
                                        })
                        )
                    }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun apiLogout(): MutableLiveData<Resource<Response<Any>>> {
        return logoutDataList
    }
}
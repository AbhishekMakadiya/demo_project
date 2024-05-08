package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.Response
import com.demo.repository.VerifyOtpRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(private val verifyOtpRepository: VerifyOtpRepository)  : ViewModel() {

    private val verifyOtpResult = MutableLiveData<Resource<Response<Any?>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for Verify Otp
    */
    fun callApiVerifyOtp(param: HashMap<String, Any?>) {
        verifyOtpResult.postValue(Resource.loading(null))
        viewModelScope.launch {
            verifyOtpRepository.apiVerifyOtp(param)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it
                            .subscribe({ response ->
                                verifyOtpResult.postValue(Resource.success(response))
                            }, { throwable ->
                                verifyOtpResult.postValue(
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

    fun apiVerifyOtp(): MutableLiveData<Resource<Response<Any?>>> {
        return verifyOtpResult
    }

}
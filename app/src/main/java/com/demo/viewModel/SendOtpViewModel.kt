package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.Response
import com.demo.model.SendOtpModel
import com.demo.repository.SendOtpRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendOtpViewModel @Inject constructor(private val sendOtpRepository: SendOtpRepository)  : ViewModel() {

    private val sendOtpResult = MutableLiveData<Resource<Response<SendOtpModel?>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for Send Otp
    */
    fun callApiSendOtp(param: HashMap<String, Any?>) {
        sendOtpResult.postValue(Resource.loading(null))
        viewModelScope.launch {
            sendOtpRepository.apiSendOtp(param)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it
                            .subscribe({ response ->
                                sendOtpResult.postValue(Resource.success(response))
                            }, { throwable ->
                                sendOtpResult.postValue(
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

    fun apiSendOtp(): MutableLiveData<Resource<Response<SendOtpModel?>>> {
        return sendOtpResult
    }

}
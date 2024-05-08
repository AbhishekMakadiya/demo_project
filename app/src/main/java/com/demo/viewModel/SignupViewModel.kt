package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.Response
import com.demo.model.SignupModel
import com.demo.repository.SingupRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val signupRepository: SingupRepository)  : ViewModel() {

    private val userList = MutableLiveData<Resource<Response<SignupModel>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for signup
    */
    fun registerUser(param: HashMap<String, RequestBody?>, files: ArrayList<MultipartBody.Part?>) {
        userList.postValue(Resource.loading(null))
        viewModelScope.launch {
            signupRepository.userRegister(param, files)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it
                            .subscribe({ response ->
                                userList.postValue(Resource.success(response))
                            }, { throwable ->
                                userList.postValue(
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

    fun userRegister(): MutableLiveData<Resource<Response<SignupModel>>> {
        //registerUser(param, files)
        return userList
    }

}
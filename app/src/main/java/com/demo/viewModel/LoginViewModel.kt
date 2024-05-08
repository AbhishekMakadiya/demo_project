package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.Response
import com.demo.model.UserModel
import com.demo.repository.LoginRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository)  : ViewModel() {

    private val userList = MutableLiveData<Resource<Response<UserModel>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for Login
    */
    fun login(param: HashMap<String, Any?>) {
        userList.postValue(Resource.loading(null))
        viewModelScope.launch {
            loginRepository.apiLogin(param)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it.subscribe({
                                response -> userList.postValue(Resource.success(response)) },
                            { throwable ->
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

    fun apiLogin(): MutableLiveData<Resource<Response<UserModel>>> {
        return userList
    }

}
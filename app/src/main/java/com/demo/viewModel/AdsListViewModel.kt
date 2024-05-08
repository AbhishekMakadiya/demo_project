package com.demo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.model.AdsModel
import com.demo.model.Response
import com.demo.repository.AdsRepository
import com.demo.utils.LogHelper
import com.demo.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdsListViewModel @Inject constructor(private val adsRepository: AdsRepository)  : ViewModel() {

    private val adsList = MutableLiveData<Resource<Response<ArrayList<AdsModel?>>>>()
    private val compositeDisposable = CompositeDisposable()

    /*
    * Api call for Ads List
    */
    fun callApiGetAdsList(param: HashMap<String, Any?>) {
        adsList.postValue(Resource.loading(null))
        viewModelScope.launch {
            adsRepository.apiGetAdvertise(param)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.let {
                    compositeDisposable.add(
                        it
                            .subscribe({ response ->
                                adsList.postValue(Resource.success(response))
                            }, { throwable ->
                                adsList.postValue(
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

    fun apiGetAdsList(): MutableLiveData<Resource<Response<ArrayList<AdsModel?>>>> {
        return adsList
    }

}
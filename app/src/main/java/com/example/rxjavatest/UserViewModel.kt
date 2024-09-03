package com.example.rxjavaproj

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val userLiveData = MutableLiveData<User>()
    private val errorLiveData = MutableLiveData<String>()
    private val compositeDisposable = CompositeDisposable()

    fun getUserLiveData(): LiveData<User> = userLiveData
    fun getErrorLiveData(): LiveData<String> = errorLiveData

    fun fetchUser(id: Int) {
        val disposable = userRepository.getUser(id)
            .subscribe(
                { user -> userLiveData.postValue(user) },
                { error -> errorLiveData.postValue(error.message) }
            )

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

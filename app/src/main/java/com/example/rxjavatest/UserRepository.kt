package com.example.rxjavaproj


import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserRepository(private val apiService: ApiService) {

    fun getUser(id: Int): Single<User> {
        return apiService.getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

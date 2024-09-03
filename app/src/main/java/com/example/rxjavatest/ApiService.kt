package com.example.rxjavaproj

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Single<User>
}

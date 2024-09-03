package com.example.rxjavaproj

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Single<User>
}

/*
1.
Получается так. Эта часть кода определяет метоб getUser, кодотрый делает GET-запрос для получения
информации о пользователе с опредленным id (id как идентификатор).
Ответ от сервера будет представлен в виде объекта User, который мы оборачиваем в Single.
Сам Sigle удобный тип Observable в данном случае, потому что мы ожидаем или что получим данные о User,
или что нам выдаст ошибку. ----> User

p.s Для того, чтобы ты понимал ход моей мысли, то все комментарии я буду нумеровать
+ стрелочкой указывать куда я дальше пошел.
 */
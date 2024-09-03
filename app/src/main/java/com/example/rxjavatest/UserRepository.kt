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


/*
3.
Мы вызываем метод getUser из нашего Api (c 10 строки), для получения данных о пользователе
через его id. Сам метод возвращает Single<User>, который уже продолжает цепочку Rx.

.subscribeOn(Schedulers.io()) - мы указываем в каком потоке должен происходить вызов метода
getUser из Api. Делать мы это хотим в фоновом потоке, по этой причине Schedulers.io().
Ну а уже обработка в фоновом режиме нам нужна для того, чтобы мы не заблокировали основной поток

.observeOn(AndroidSchedulers.mainThread()) - мы указываем в каком потоке будет происходить
наблюдение за Single<User>. Делать мы это хотим в UI-потоке, для того, чтобы результат запроса
или ошибка, могли быть обработаны на main потоке, а это нам нужно
чтобы обновить пользовательский интерфейс. Т.е или выдать результат, или выдать ошибку.

----> UserViewModel

 */

package com.example.rxjavaproj

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val userLiveData = MutableLiveData<User>() //Содержит данные о пользователе. LiveData
    // позволяет UI-компонентам наблюдать заизменениями данных и автоматически обновлять их

    private val errorLiveData = MutableLiveData<String>() //Ну просто хранит в себе сообщения об ошибках.
    // Включается если была ошибка

    private val compositeDisposable = CompositeDisposable() //Самое сладкое)) этот объект собирает все disposable в одном месте (результат подписки на наш Single из репозитория)
    // для того, чтобы их можно было (в нашем случае) легко разом очистить. Это помогает избежать утечку памяти.

    fun getUserLiveData(): LiveData<User> = userLiveData //Возвращает объект LiveData<User>, за которым можно наблюдать в UI
    //Activity на него подписывается в MainActivity (строки 39-41) для того чтобы обновлять интерфейс в случае чего.

    fun getErrorLiveData(): LiveData<String> = errorLiveData //Аналагично, но уже для наблюдения за ошибками
    //Activity на него подписывается в MainActivity (строки 43-46)

    fun fetchUser(id: Int) {
        val disposable = userRepository.getUser(id) //запускаем асинхронный запрос
            // на получение данных о пользователе через репозиторий
            .subscribe( //данный метод подписывается на результат выполнения Single<User>
                { user -> userLiveData.postValue(user) }, //если успешно, то передаем в userLiveData
                // при помощи метода postValue - это автоматически уведомляет наблюдателей, чтобы они обновили свой интерфейс
                { error -> errorLiveData.postValue(error.message) } //Аналагично, но уже уведомляет об ошибке
            )

        compositeDisposable.add(disposable) //добавляет наш disposable в контейнер
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        //Очищает все подписки, чтобы избежать утечки памяти.
    // Гарантирует, что больше нет активных подписок на асинхронные операции
    }
}


/*
4.
Код обеспечивает асинхронное получение данных о пользователе через репозиторий и
передачу инфы в UI через LiveData.
Кроме того код управляет подписками на Rx через compositeDisposable
----> MainActivity
 */

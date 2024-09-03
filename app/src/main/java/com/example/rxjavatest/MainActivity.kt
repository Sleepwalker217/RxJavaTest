package com.example.rxjavaproj


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.rxjavaproj.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val apiService by lazy { //При первом обращении к Api (к серверу) инициализируем ретрофит
        //если задушнить, то скажу что by lazy - это делегирование, которое откладывает инициализацию
        // до момента первого обращения к переменной
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val userRepository by lazy { UserRepository(apiService) }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(userRepository)
        //ну... наверное при первом обращении создается экземпляр UserRepository, которому передается apiService
        //для получения из него необходимых данных. Наверное под "необходимыми данными" можно считать наш Single?
        //Ну и с userViewModel все просто. Без нее мы не сможем управлять данными и отправлять их для отображения в UI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Наблюдаем за изменениями данных пользователя
        userViewModel.getUserLiveData().observe(this, Observer { user ->
            binding.textView.text = "Name: ${user.name}\nUsername: ${user.username}\nEmail: ${user.email}"
        }) //мы следим за методом getUserLiveData из userViewModel и отображаем данные (если нет ошибки)
        // по формату написанным на 45 строке

        // Наблюдаем за ошибками. Тут и добавить нечего) Все аналогично ф-ии выше, но только уже про ошибку
        userViewModel.getErrorLiveData().observe(this, Observer { error ->
            binding.textView.text = "Error: $error"
        })

        // Добавляем TextWatcher для EditText
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Проверяем, что введенный текст не пуст и является числом
                val userId = s?.toString()?.toIntOrNull()
                if (userId != null) {
                    // Запрашиваем данные пользователя
                    userViewModel.fetchUser(userId)
                } else {
                    binding.textView.text = "Invalid ID"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}


/*5. Конец (фабрику коментить не буду)
1. Репозиторий отправляет запрос на сервер с использованием нашего Api. Это событие происходит в фоновом потоке (io)
2. Когда с сервера приходят данные, то ViewModel передает их в LiveData (у ViewModel эти данные есть потому что она зависима от репозитория)
Как только данные обновляются, то автоматически показывается информация о пользователе (на основе data-class)
3.Так же во ViewModel мы обрабатываем ошибки.
4. Мы прописывали compositeDisposable для того, чтобы он хранил в себе потоки и при закрытии приложения
прекращал выполнение всех фоновых потоков (в нашем случае он один). Таким образом мы избегаем утечки данных
 */
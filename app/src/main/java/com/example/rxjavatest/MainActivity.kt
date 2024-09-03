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

    private val apiService by lazy {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Наблюдаем за изменениями данных пользователя
        userViewModel.getUserLiveData().observe(this, Observer { user ->
            binding.textView.text = "Name: ${user.name}\nUsername: ${user.username}\nEmail: ${user.email}"
        })

        // Наблюдаем за ошибками
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
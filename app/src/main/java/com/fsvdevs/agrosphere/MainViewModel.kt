package com.fsvdevs.agrosphere

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.database.DatabaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = DatabaseRepository()

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> get() = _data

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Database Error", "Error during database operation", throwable)
    }

    fun fetchData(query: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val resultSet = repository.fetchDataFromDatabase(query)
            val resultBuilder = StringBuilder()
            resultSet?.let {
                while (it.next()) {
                    resultBuilder.append(it.getString("CITY")).append("\n")
                }
            }
            _data.postValue(resultBuilder.toString())
        }
    }
}
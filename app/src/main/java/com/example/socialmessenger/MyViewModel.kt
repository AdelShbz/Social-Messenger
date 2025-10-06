package com.example.socialmessenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val _newText = MutableLiveData<String>()
    val newText : LiveData<String> = _newText

    fun setNewText(text: String){
        _newText.postValue(text)
    }
}
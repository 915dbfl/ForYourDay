package com.cookandroid.foryourday.d_day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DDayViewModel: ViewModel() {
    private val _updatePosition = MutableLiveData<Int>()

    val updatePosition: LiveData<Int> = _updatePosition

    fun setUpdatePosition(p: Int){
        _updatePosition.postValue(p)
    }
}
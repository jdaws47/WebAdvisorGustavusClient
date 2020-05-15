package edu.gustavus.webadvisorapp.ui.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoursesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is courses Fragment"
    }
    val text: LiveData<String> = _text
}
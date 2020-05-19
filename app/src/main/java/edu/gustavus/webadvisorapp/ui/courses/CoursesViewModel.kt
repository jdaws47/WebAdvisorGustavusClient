package edu.gustavus.webadvisorapp.ui.courses

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CoursesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is courses Fragment"
    }
    val text: LiveData<String> = _text
    val current_date = Date()
    var buttonsHidden = false
    var buttonsEnabled = true
}
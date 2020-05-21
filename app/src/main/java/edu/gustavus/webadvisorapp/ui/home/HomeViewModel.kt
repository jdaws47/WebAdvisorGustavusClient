package edu.gustavus.webadvisorapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.gustavus.webadvisorapp.R
import java.net.URL

class HomeViewModel : ViewModel() {

    var firstLoad = true
    var secondLoad = true
    var loggedIn = false

    var welcomeString = "Please Login to Continue"

    override fun onCleared() {
        Log.i("HomeViewModel", "onCleared() called")
        super.onCleared()
    }
}
package edu.gustavus.webadvisorapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URL

class HomeViewModel : ViewModel() {

    var firstLoad = true
    var firstPress = true
    var secondLoad = true
    var loggedIn = false

    var welcomeString = "Please Login to Continue"
}
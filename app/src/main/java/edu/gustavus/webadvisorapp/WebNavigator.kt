package edu.gustavus.webadvisorapp

import android.graphics.Bitmap
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

private val STARTING_URL = "https://wa.gac.edu/WebAdvisor"

class WebNavigator() {

    fun getLogin(){
        Thread(Runnable {
            val url = URL(STARTING_URL)
            val connection = url.openConnection()
            connection.connect()
            val contentType = connection.contentType
            val content = connection.content
            Log.i("WEB_NAVIGATOR", "got content of type $contentType")
            val bufferedReader = BufferedReader(InputStreamReader(connection.getInputStream()))
            bufferedReader.forEachLine {
                Log.i("WEB_NAVIGATOR", "got content: ${it}")
            }
        }).start()
    }

    companion object {
        fun newInstance(): WebNavigator {
            return WebNavigator()
        }
    }
}
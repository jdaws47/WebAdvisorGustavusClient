package edu.gustavus.webadvisorapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


private val STARTING_URL = "https://wa.gac.edu/WebAdvisor"

class HomeFragment : Fragment() {

    private lateinit var webView: WAWebView
    private lateinit var loginButton: Button
    private lateinit var textView: TextView

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        webView = requireActivity().webview
        loginButton = root.findViewById(R.id.login_button) as Button
        textView = root.findViewById(R.id.text_home) as TextView

        textView.text = homeViewModel.welcomeString

        webView.isVisible = false

        loginButton.setOnClickListener {
            if(homeViewModel.firstPress) {
                login()
            } else {
                Log.i("HomeFragment", "toggling visibility on second press")
                toggleVisibility()
            }
        }

        updateUi()

        return root
    }

    override fun onStart() {
        super.onStart()
        checkTime()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkTime()
        }
    }

    private fun toggleVisibility() {
        webView.isVisible = !webView.isVisible
        login_button.isGone = !loginButton.isGone
        textView.isGone = !textView.isGone
    }

    private fun showWebView() {
        webView.isVisible = true
        login_button.isGone = true
        textView.isGone = true
    }

    private fun checkTime() {
        val cstCdtFormat = SimpleDateFormat("HH")
        cstCdtFormat.timeZone = TimeZone.getTimeZone("CST6CDT")
        val hour = cstCdtFormat.format(Date())
        Log.i("HOME", "the hour of the time in CST/CDT is $hour")
        if (hour.toInt() >= 2 && hour.toInt() < 4){
            loginButton.isGone = false
            loginButton.isEnabled = false
            textView.text = "WebAdvisor is unavailable daily from 2:00am to 4:00am CST"
        }
    }

    private fun login() {
        var didNotReachLoginScreen = true

        val wvc = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String){
                webView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();")
                { html ->
                    Log.d("HTML", html)
                    if(homeViewModel.firstLoad) {
                        val loginIndex = html.indexOf("acctLogin")
                        if(loginIndex != -1) {
                            didNotReachLoginScreen = false
                            val loginSub = html.substring(loginIndex - 10..loginIndex + 300)
                            Log.i("HTML", "${loginSub} at index $loginIndex")
                            val hrefTagStart = "href=\\\""
                            val hrefTagStop = "\\\" "
                            val loginHref = loginSub.substring(
                                loginSub.indexOf(hrefTagStart) + hrefTagStart.length,
                                loginSub.indexOf(hrefTagStop)
                            )
                            Log.i("HTML", "Next Link: ${loginHref}")
                            homeViewModel.firstLoad = false
                            // this is the theoretical js implementation of simulating the click,
                            // however this does not get reflected visually, so is not used
                            /*webView.loadUrl("javascript:(function(){" +
                                    webView.clickElementById("acctLogin")+
                                    "})()")*/
                            requireActivity().runOnUiThread(Runnable {
                                webView.loadUrl(loginHref.replace("&amp;", "&").replace(":443", ""))
                                showWebView()
                            })
                        }
                    }
                    if(html.indexOf("acctLogout") != -1 && homeViewModel.secondLoad) {
                        Log.i("HTML", "Above is signed in HTML")

                        homeViewModel.secondLoad = false

                        webView.evaluateJavascript("javascript:(function(){" +
                                webView.getInnerTextById("mainBody", 3) +
                        "})()") {
                            Log.i("HTML", "found inner text: $it")
                            homeViewModel.welcomeString = it.replace("\"", "")
                            requireActivity().runOnUiThread {
                                textView.text = homeViewModel.welcomeString
                            }

                            webView.loadUrl("javascript:(function(){" +
                                    webView.clickElementByInnerText("Students", "mainMenu") +
                            "})()"
                            )
                        }

                        homeViewModel.loggedIn = true
                        webView.loggedIn = true
                        requireActivity().runOnUiThread {
                            login_button.isGone = true
                            toggleVisibility()
                            updateUi()
                        }
                    }
                }
            }
        }

        //webView.clearCache(false)
        //webView.webChromeClient = WebChromeClient()
        webView.setWebViewClient(wvc)
        webView.loadUrl(STARTING_URL)

        Timer().schedule(5000) {
            Log.i("LoginTimer","loginTimer executing")
            if(didNotReachLoginScreen) {
                requireActivity().runOnUiThread {
                    Log.i("LoginTimer","webview execution blocked: clearing cookies")
                    homeViewModel.firstLoad = true
                    homeViewModel.secondLoad = true
                    homeViewModel.loggedIn = false
                    webView.clearCache(true)
                    CookieManager.getInstance().removeAllCookies(null)
                    webView.loadUrl(STARTING_URL)
                }
            }
        }
    }

    private fun updateUi() {
        loginButton.isGone = homeViewModel.loggedIn
    }
}

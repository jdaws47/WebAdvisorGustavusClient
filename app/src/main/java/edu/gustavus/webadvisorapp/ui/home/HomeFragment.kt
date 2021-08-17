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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

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

        homeViewModel.welcomeString = getString(R.string.welcome_logged_out)
        textView.text = homeViewModel.welcomeString

        webView.isVisible = false

        loginButton.setOnClickListener {
            if(!webView.loggedIn) {
                login()
            } else {
                logout()
            }
        }

        updateUi()

        return root
    }

    override fun onStart() {
        checkTime()
        super.onStart()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            checkTime()
            updateUi()
        }
        super.onHiddenChanged(hidden)
    }

    private fun toggleVisibility() {
        webView.isVisible = !webView.isVisible
        loginButton.isGone = !loginButton.isGone
        textView.isGone = !textView.isGone
    }

    private fun showWebView() {
        webView.isVisible = true
        loginButton.isGone = true
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
            homeViewModel.welcomeString = "WebAdvisor is unavailable daily from 2:00am to 4:00am CST"
            updateUi()
        }
    }

    private fun login() {
        Log.i("HomeFragment", "Logging in")
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
                                    webView.clickElementByInnerText("Students", "mainMenu", "a") +
                            "})()"
                            )
                        }

                        homeViewModel.loggedIn = true
                        webView.loggedIn = true
                        requireActivity().runOnUiThread {
                            loginButton.text = getString(R.string.logout_prompt)
                            toggleVisibility()
//                            webView.isVisible = !webView.isVisible
                            updateUi()
                        }
                    }
                }
            }
        }

        //webView.clearCache(false)
        //webView.webChromeClient = WebChromeClient()
        webView.setWebViewClient(wvc)
        webView.loadUrl(webView.STARTING_URL)

        Timer().schedule(10000) {
            Log.i("LoginTimer","loginTimer executing")
            if(didNotReachLoginScreen) {
                requireActivity().runOnUiThread {
                    Log.i("LoginTimer","webview execution blocked: clearing cookies")
                    homeViewModel.firstLoad = true
                    homeViewModel.secondLoad = true
                    homeViewModel.loggedIn = false
                    webView.loggedIn = false
                    webView.clearCache(true)
                    CookieManager.getInstance().removeAllCookies(null)
                    webView.loadUrl(webView.STARTING_URL)
                }
            }
        }
    }

    private fun logout() {
        Log.i("HomeFragment", "Logging out")

        val wvc = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String){
                webView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();")
                { html ->
                    Log.d("HTML", html)
                    if (webView.title.toLowerCase().indexOf("main webadvisor menu") != -1 && webView.loggedIn) {
                        webView.evaluateJs(webView.clickElementById("acctLogout", 1))
                        requireActivity().runOnUiThread {
                            homeViewModel.firstLoad = true
                            homeViewModel.secondLoad = true
                            webView.loggedIn = false
                            homeViewModel.loggedIn = false
                            homeViewModel.welcomeString = getString(R.string.welcome_logged_out)
                            updateUi()
                            Log.i("HomeFragment", "Logged out successfully")
                        }
                    }
                }
            }
        }

        webView.setWebViewClient(wvc)
        webView.loadUrl(webView.STARTING_URL)
    }

    private fun updateUi() {
        loginButton.text = if(webView.loggedIn) { getString(R.string.logout_prompt) } else { getString(R.string.login_button_prompt) }
        textView.text = homeViewModel.welcomeString
    }
}

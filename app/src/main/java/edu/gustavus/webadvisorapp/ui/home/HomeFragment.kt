package edu.gustavus.webadvisorapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.gustavus.webadvisorapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.log


private val STARTING_URL = "https://wa.gac.edu/WebAdvisor"

class HomeFragment : Fragment() {

    private lateinit var webView: WebView
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
        //root.findViewById(R.id.webview) as WebView
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

    private fun toggleVisibility() {
        webView.isVisible = !webView.isVisible
        login_button.isGone = !loginButton.isGone
        textView.isGone = !textView.isGone
    }

    private fun login() {
        val wvc = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String){
                webView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();")
                { html ->
                    Log.d("HTML", html)
                    if(homeViewModel.firstLoad) {
                        val loginIndex = html.indexOf("acctLogin")
                        if(loginIndex != -1) {
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
                            requireActivity().runOnUiThread(Runnable {
                                webView.loadUrl(loginHref.replace("&amp;", "&").replace(":443", ""))
                                toggleVisibility()
                            })
                        }
                    }
                    if(html.indexOf("acctLogout") != -1 && homeViewModel.secondLoad) {
                        Log.i("HTML", "Above is signed in HTML")
                        val welcomeIndexStart = html.indexOf("Welcome ")
                        val welcomeIndexEnd = html.indexOf("\\u003C/strong>")
                        val welcomeSub = html.substring(welcomeIndexStart until welcomeIndexEnd)
                        Log.i("HTML", "${welcomeSub} at index $welcomeIndexStart")

                        val studentIndexStart = html.indexOf("XWAMST_Bars") + "XWAMST_BARS\\\" href=\\\"".length
                        val studentIndexEnd = html.indexOf(" Students") - "\\\" onfocus=\\\"blur()\\\">".length
                        val studentUrl = html.substring(studentIndexStart until studentIndexEnd)
                        Log.i("HTML", "${studentUrl} at index $studentIndexStart to $studentIndexEnd")

                        homeViewModel.secondLoad = false
                        homeViewModel.welcomeString = welcomeSub
                        homeViewModel.loggedIn = true
                        requireActivity().runOnUiThread {
                            textView.text = welcomeSub
                            login_button.isGone = true
                            webView.loadUrl(studentUrl.replace("&amp;", "&").replace(":443", ""))
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
    }

    private fun updateUi() {
        loginButton.isGone = homeViewModel.loggedIn
    }
}

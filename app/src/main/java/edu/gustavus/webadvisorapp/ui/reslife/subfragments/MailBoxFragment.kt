package edu.gustavus.webadvisorapp.ui.reslife.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import kotlinx.android.synthetic.main.activity_main.*

class MailBoxFragment : Fragment() {

    private lateinit var webView: WAWebView

    private lateinit var mailBoxNumber: TextView
    private lateinit var mailBoxCombo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_mail_box, container, false)

        webView = requireActivity().webview

        mailBoxNumber = root.findViewById(R.id.mail_box_number_text)
        mailBoxCombo = root.findViewById(R.id.mail_box_combo_text)

        webView.webViewClient = wvc
        webView.reload()

        return root
    }

    private val wvc = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String){
            view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })(), document.title;")
            { html ->
                Log.i("Schedule", "title of html is ${view.title}")
                if(view.title.toLowerCase().indexOf("box and combination") != -1) {
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getInnerTextById("VAR1")+
                            "})()") {
                        requireActivity().runOnUiThread {
                            mailBoxNumber.text = it.replace("\"", "")
                        }
                    }
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getInnerTextById("VAR2")+
                            "})()") {
                        requireActivity().runOnUiThread {
                            mailBoxCombo.text = it.replace("\"", "")
                        }

                    }
                } else if(view.title.toLowerCase().indexOf("webadvisor for students") != -1) {
                    webView.evaluateJs(webView.clickElementBySpan("Box and Combination", "bodyForm"))
                }
            }
        }
    }
}
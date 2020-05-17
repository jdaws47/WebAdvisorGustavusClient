package edu.gustavus.webadvisorapp.ui.courses.subfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isGone

import edu.gustavus.webadvisorapp.R
import kotlinx.android.synthetic.main.activity_main.*


class TranscriptFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_transcript, container, false)

        webView = requireActivity().webview
        webView.setWebViewClient(wvc)
        webView.reload()

        return root
    }

    private val wvc = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String){
            view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })(), document.title;")
            { html ->
                Log.i("TRANSCRIPT", "title of html is ${view.title}")
                if(view.title.toLowerCase().indexOf("webadvisor for students") != -1) {
                    Log.i("TRANSCRIPT", "clicking link")
                    view.loadUrl("javascript:(function(){"+
                        "var spans = document.getElementById('bodyForm').getElementsByTagName('span');"+
                        "for (var i=0;i<spans.length;i++){"+
                            "if(spans[i].innerText.toString().localeCompare(\"View Transcript\") == 0){"+
                                "return spans[i].parentElement.click();"+
                            "}"+
                        "}"+
                    "})()")
                } else if(view.title.toLowerCase().indexOf("view transcript") != -1) {
                    Log.i("TRANSCRIPT", "submitting default transcript")
                    view.loadUrl("javascript:(function(){"+
                            "var elements = document.getElementsByName('SUBMIT2');"+
                            "elements[0].click();"+
                    "})()")
                } else if(view.title.toLowerCase().indexOf("transcript") != -1) {
                    
                }
            }
        }
    }
}

package edu.gustavus.webadvisorapp

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class WAWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {

    public var loggedIn : Boolean = false
    public val STARTING_URL = "https://wa.gac.edu/WebAdvisor"

    public fun clickElementBySpan(span: String, parentTag: String = "") : String {
        return (
                "var spans = document"+(if(!parentTag.isBlank()){".getElementById('"+parentTag+"')"} else {""})+".getElementsByTagName('span');"+
                "for (var i=0;i<spans.length;i++){"+
                    "if(spans[i].innerText.toString().localeCompare(\""+span+"\") == 0){"+
                        "return spans[i].parentElement.click();"+
                    "}"+
                "}")
    }

    public fun clickElementByInnerText(innerText: String, parentTag: String = "") : String {
        return (
                "var elems = document"+(if(!parentTag.isBlank()){".getElementById('"+parentTag+"')"} else {""})+".getElementsByTagName('*');"+
                "for (var i=0;i<elems.length;i++){"+
                    "if(elems[i].innerText.toString().localeCompare(\""+innerText+"\") == 0){"+
                        "return elems[i].click();"+
                    "}"+
                "}")
    }

    public fun clickElementByNameTag(name: String, firstChildOfRepeat: Int = 0) : String {
        return(
                "var elements = document.getElementsByName('"+name+"')"+ (".children[0]".repeat(firstChildOfRepeat)) +";"+
                "elements[0].click();")
    }

    public fun clickElementById(id: String, firstChildOfRepeat: Int = 0) : String {
        return(
                "var element = document.getElementById('"+id+"')"+ (".children[0]".repeat(firstChildOfRepeat)) +";"+
                "element.click();")
    }

    public fun getInnerTextById(id: String, firstChildOfRepeat: Int = 0) : String {
        return (
                "var element = document.getElementById('"+id+"');"+
                "return element"+ (".children[0]".repeat(firstChildOfRepeat)) +".innerText;")
    }

    public fun loadUrlWithJs(js: String) {
        loadUrl("javascript:(function(){" +
                js +
                "})()"
        )
    }

    public fun getAllChildrenInnerTextById(id: String) : String {
        return (
                "var parentElem = document.getElementById('"+id+"');"+
                "var returnStr = '';"+
                "for(var i=0;i<parentElem.children.length;i++){"+
                    "returnStr = returnStr.concat(parentElem.children[i].innerText.replace(/,/g,';'));"+
                    "returnStr = returnStr.concat(',');"+
                "}"+
                "return returnStr;")
    }

    public fun getTableByDivId(id: String) : String {
        return (
                "var div = document.getElementById('"+id+"');"+
                "var mainTable = div.children[2];"+
                "var returnStr = '';"+
                    "for(var i=0;i<mainTable.rows.length;i++){"+
                        "for (var j=0;j<mainTable.rows[i].cells.length;j++){"+
                            "if(j == 0){continue;}"+
                            "var text = '';"+
                            "if(i == 0){"+
                                "text = mainTable.rows[i].cells[j].innerText;}"+
                            "else{"+
                                "text = mainTable.rows[i].cells[j].children[0].children[0].innerText;}"+
                            "if(text.localeCompare('\\n') != 0){"+
                                "returnStr = returnStr.concat(text.replace(/,/g,';'));}"+
                            "returnStr = returnStr.concat(',');"+
                        "}"+
                    "returnStr = returnStr.concat('|');"+
                    "}"+
                "return returnStr;")
    }

    public fun convertTableStringTo2DArray(str: String) : List<MutableList<String>> {
        val rows = str.replace("\"","").replace("\\n", "").split("|")
        val arr = List<MutableList<String>>(rows.size - 1) { MutableList<String>(0) {""} }
        for (i in arr.indices) {
            val cols = rows[i].split(",")
            arr[i].addAll(cols)
        }

        for (row in arr)
            for (col in row.indices)
                row[col] = row[col].replace(";", ",")

        return arr
    }

    public fun navigateToStudentMenu(nextWebClient: WebViewClient? = null, callbacks:(() -> Unit) = {}){
        val wvc = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String){
                view.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })(), document.title;")
                { html ->
                    Log.i("WAWebView", "title of html is ${view.title}")
                    if(view.title.toLowerCase().indexOf("webadvisor for students") != -1) {
                        Log.i("WAWebView", "at student menu")
                        callbacks()
                        if(nextWebClient != null) {
                            webViewClient = nextWebClient
                            reload()
                        }
                    } else if(view.title.toLowerCase().indexOf("main webadvisor menu") != -1) {
                        loadUrlWithJs(clickElementByInnerText("Students", "mainMenu"))
                    } else {
                        loadUrlWithJs(clickElementById("acctMain", 1))
                    }
                }
            }
        }
        webViewClient = wvc

        if(title.toLowerCase().indexOf("webadvisor for students") != -1) {
            Log.i("WAWebView", "at student menu")
            callbacks()
            if(nextWebClient != null) {
                webViewClient = nextWebClient
                reload()
            }
        } else {
            loadUrl(STARTING_URL)
        }
    }
}

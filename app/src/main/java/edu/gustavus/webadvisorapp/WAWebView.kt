package edu.gustavus.webadvisorapp

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class WAWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {

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
}

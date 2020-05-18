package edu.gustavus.webadvisorapp.ui.courses.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.gustavus.webadvisorapp.R
import kotlinx.android.synthetic.main.activity_main.*


class TranscriptFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView

    private var adapter: TranscriptAdapter? = TranscriptAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_transcript, container, false)

        webView = requireActivity().webview
        webView.setWebViewClient(wvc)
        webView.reload()

        recyclerView = root.findViewById(R.id.recyclerview_transcript)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

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
                    Log.i("TRANSCRIPT", "converting table")
                    view.evaluateJavascript("javascript:(function(){"+
                            "var div = document.getElementsByClassName('envisionWindow')[0];"+
                            "var mainTable = div.children[2];"+
                            "var returnStr = '';"+
                            "for(var i=0;i<mainTable.rows.length;i++){"+
                                "for (var j=0;j<mainTable.rows[i].cells.length;j++){"+
                                    "if(j == 0){continue;}"+
                                    "var text = '';"+
                                    "if(i == 0){"+
                                        "text = mainTable.rows[i].cells[j].innerText;}"+
                                    "else{"+
                                        "text = mainTable.rows[i].cells[j].children[0].children[0].innerHTML;}"+
                                    "if(text.localeCompare('\\n') != 0){"+
                                        "returnStr = returnStr.concat(text.replace(',',';'));}"+
                                    "returnStr = returnStr.concat(',');"+
                                "}"+
                                "returnStr = returnStr.concat('|');"+
                            "}"+
                            "return returnStr;"+
                            "})()") { table ->
                        Log.i("TRANSCRIPT", "table: $table")
                        val tableArr = convertStringTo2DArray(table)
                        for(row in tableArr)
                            for(col in row.indices)
                                row[col] = row[col].replace(";", ",")
                        Log.i("TRANSCRIPT", "table: $tableArr")
                        adapter?.arr = tableArr
                        adapter?.submitList(tableArr)
                    }
                }
            }
        }
    }

    private fun convertStringTo2DArray(str: String) : List<MutableList<String>> {
        val rows = str.replace("\"","").split("|")
        val arr = List<MutableList<String>>(rows.size - 1) { MutableList<String>(0) {""} }
        for (i in arr.indices) {
            val cols = rows[i].split(",")
            arr[i].addAll(cols)
        }
        return arr
    }

    private inner class CourseHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var row: MutableList<String>

        private val textViews = mutableListOf<TextView>(
            itemView.findViewById(R.id.list_item_text_0) as TextView,
            itemView.findViewById(R.id.list_item_text_1) as TextView,
            itemView.findViewById(R.id.list_item_text_2) as TextView,
            itemView.findViewById(R.id.list_item_text_3) as TextView,
            itemView.findViewById(R.id.list_item_text_4) as TextView
        )

        fun bind(row: MutableList<String>) {
            this.row = row
            for(i in textViews.indices) {
                if(i<row.size)
                    textViews[i].text = row[i]
            }
        }
    }

    private inner class TranscriptAdapter(var arr: List<MutableList<String>>) : androidx.recyclerview.widget.ListAdapter<MutableList<String>, CourseHolder>(TranscriptCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CourseHolder {
            val view = layoutInflater.inflate(R.layout.transcript_list_item, parent, false)
            return CourseHolder(view)
        }

        override fun getItemCount() = arr.size

        override fun onBindViewHolder(holder: CourseHolder, position: Int) {
            val row = arr[position]
            holder.bind(row)
        }
    }

    private inner class TranscriptCallback: DiffUtil.ItemCallback<MutableList<String>>() {
        override fun areItemsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return old == new
        }

        override fun areContentsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return  old == new
        }
    }
}

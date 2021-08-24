package edu.gustavus.webadvisorapp.ui.reslife.subfragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import kotlinx.android.synthetic.main.activity_main.*

class MealPlanFragment : Fragment() {

    private lateinit var webView: WAWebView
    private lateinit var recyclerView: RecyclerView

    private var adapter: MarketplaceAdapter? = MarketplaceAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_meal_plan, container, false)

        webView = requireActivity().webview
        webView.setWebViewClient(wvc)
        webView.reload()

        recyclerView = root.findViewById(R.id.recyclerview_marketplace)
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
                    Log.i("MARKETPLACE", "clicking link")
                    view.loadUrl("javascript:(function(){"+
                            webView.clickElementBySpan("Meal Plan/Marketplace Summary", "bodyForm")+
                            "})()")
                } else if(view.title.toLowerCase().indexOf("marketplace") != -1) {
                    Log.i("MARKETPLACE", "converting table")
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getTableByDivId("GROUP_Grp_LIST_VAR4", headerRows = 2)+
                            "})()") { table ->
                        Log.i("MARKETPLACE", "table: $table")
                        val tableArr = webView.convertTableStringTo2DArray(table)
                        Log.i("MARKETPLACE", "table: $tableArr")
                        adapter?.arr = tableArr
                        adapter?.submitList(tableArr)
                    }
                }
            }
        }
    }

    private inner class DaySummaryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var row: MutableList<String>

        private val textViews = mutableListOf<TextView>(
            itemView.findViewById(R.id.list_item_text_0) as TextView,
            itemView.findViewById(R.id.list_item_text_1) as TextView,
            itemView.findViewById(R.id.list_item_text_2) as TextView
        )

        fun bind(row: MutableList<String>, position: Int) {
            this.row = row
            for(i in textViews.indices) {
                if(i<row.size)
                    textViews[i].text = row[i]
                else
                    textViews[i].text = ""
                if(position == 1)
                    textViews[i].setTypeface(null, Typeface.BOLD)
                else
                    textViews[i].setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private inner class MarketplaceAdapter(var arr: List<MutableList<String>>) : androidx.recyclerview.widget.ListAdapter<MutableList<String>, DaySummaryHolder>(MarketplaceCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : DaySummaryHolder {
            val view = layoutInflater.inflate(R.layout.marketplace_list_item, parent, false)
            return DaySummaryHolder(view)
        }

        override fun getItemCount() = arr.size

        override fun onBindViewHolder(holder: DaySummaryHolder, position: Int) {
            val row = arr[position]
            holder.bind(row, position)
        }
    }

    private inner class MarketplaceCallback: DiffUtil.ItemCallback<MutableList<String>>() {
        override fun areItemsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return old == new
        }

        override fun areContentsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return  old == new
        }
    }
}
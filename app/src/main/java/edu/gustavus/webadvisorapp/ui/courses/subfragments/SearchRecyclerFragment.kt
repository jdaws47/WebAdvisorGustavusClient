package edu.gustavus.webadvisorapp.ui.courses.subfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.gustavus.webadvisorapp.R

private const val ARG_TABLE = "data_table"

class SearchRecyclerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private var adapter: SearchAdapter? = SearchAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val table = (arguments?.getSerializable(ARG_TABLE) as Array<MutableList<String>>).toList()
        adapter?.arr = table
        adapter?.submitList(table)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_search_recycler, container, false)

        recyclerView = root.findViewById(R.id.recyclerview_search)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return root
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
            itemView.findViewById(R.id.list_item_text_4) as TextView,
            itemView.findViewById(R.id.list_item_text_5) as TextView,
            itemView.findViewById(R.id.list_item_text_6) as TextView,
            itemView.findViewById(R.id.list_item_text_7) as TextView
        )

        fun bind(row: MutableList<String>) {
            this.row = row
            for(i in textViews.indices) {
                if(i<row.size)
                    textViews[i].text = row[i]
            }
        }
    }

    private inner class SearchAdapter(var arr: List<MutableList<String>>) : androidx.recyclerview.widget.ListAdapter<MutableList<String>, CourseHolder>(SearchCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CourseHolder {
            val view = layoutInflater.inflate(R.layout.search_list_item, parent, false)
            return CourseHolder(view)
        }

        override fun getItemCount() = arr.size

        override fun onBindViewHolder(holder: CourseHolder, position: Int) {
            val row = arr[position]
            holder.bind(row)
        }
    }

    private inner class SearchCallback: DiffUtil.ItemCallback<MutableList<String>>() {
        override fun areItemsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return old == new
        }

        override fun areContentsTheSame(old: MutableList<String>, new: MutableList<String>): Boolean {
            return  old == new
        }
    }

    companion object {
        fun newInstance(dataTable: Array<MutableList<String>>): SearchRecyclerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TABLE, dataTable)
            }
            return SearchRecyclerFragment().apply {
                arguments = args
            }
        }
    }
}
package edu.gustavus.webadvisorapp.ui.courses.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import kotlinx.android.synthetic.main.activity_main.*


class CourseScheduleFragment : Fragment() {

    private lateinit var webView: WAWebView
    private lateinit var promptText: TextView
    private lateinit var dropdownSelection: Spinner
    private lateinit var submitButton: Button

    private val termOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_course_schedule, container, false)

        webView = requireActivity().webview

        promptText = root.findViewById(R.id.prompt_text)
        dropdownSelection = root.findViewById(R.id.dropdown_schedule)
        submitButton = root.findViewById(R.id.submit_course_term)

        webView.webViewClient = wvc
        webView.reload()

        val listObserver = Observer<List<String>> { newList ->
            val adapter : ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, newList)
            dropdownSelection.adapter = adapter
        }
        termOptions.observe(viewLifecycleOwner, listObserver)

        submitButton.setOnClickListener {
            webView.loadUrlWithJs(
                "document.getElementById('VAR4').selectedIndex = '"+dropdownSelection.selectedItemPosition+"';"+
                    webView.clickElementByNameTag("SUBMIT2")
            )
        }

        childFragmentManager.addOnBackStackChangedListener {
            if(childFragmentManager.backStackEntryCount == 0) {
                showFields()
                Log.i("CoursesFragment","navigating to student menu")
                webView.navigateToStudentMenu(wvc)
            }
        }

        return root
    }

    private fun showFields() {
        promptText.isGone = false
        submitButton.isGone = false
        dropdownSelection.isGone = false
    }

    private fun hideFields() {
        promptText.isGone = true
        submitButton.isGone = true
        dropdownSelection.isGone = true
    }

    private val wvc = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String){
            view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })(), document.title;")
            { html ->
                Log.i("Schedule", "title of html is ${view.title}")
                if(view.title.toLowerCase().indexOf("my class schedule") != -1) {
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("VAR4")+
                    "})()") { listStr ->
                        val formattedListStr = listStr.replace("\"", "")
                        val list: MutableList<String> = formattedListStr.substring(0, formattedListStr.length-1).split(',').toMutableList()
                        for (i in list.indices) {
                            list[i] = list[i].replace(";", ",")
                        }
                        termOptions.value = list
                    }
                } else if(view.title.toLowerCase().indexOf("webadvisor for students") != -1) {
                    webView.loadUrlWithJs(webView.clickElementBySpan("My Class Schedule", "bodyForm"))
                } else if(view.title.toLowerCase().indexOf("class schedule") != -1) {
                    Log.i("Schedule", "converting table")
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getTableByDivId("GROUP_Grp_LIST_VAR6")+
                    "})()") { table ->
                        Log.i("Schedule", "table: $table")
                        //val tableArr = convertStringTo2DArray(table)
                        val tableArr = webView.convertTableStringTo2DArray(table)
                        for (row in tableArr)
                            for (col in row.indices)
                                row[col] = row[col].replace(";", ",")
                        Log.i("Schedule", "table: $tableArr")
                        requireActivity().runOnUiThread {
                            hideFields()
                            val fragment = ScheduleRecyclerFragment.newInstance(tableArr.toTypedArray(), dropdownSelection.selectedItem.toString())
                            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            childFragmentManager.beginTransaction()
                                .replace(R.id.child_fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }
        }
    }
}

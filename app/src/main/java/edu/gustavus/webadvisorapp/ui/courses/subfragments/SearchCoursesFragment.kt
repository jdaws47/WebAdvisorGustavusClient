package edu.gustavus.webadvisorapp.ui.courses.subfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import kotlinx.android.synthetic.main.activity_main.*


class SearchCoursesFragment : Fragment() {

    private lateinit var webView: WAWebView
    private lateinit var fieldsContainer: LinearLayout
    private lateinit var promptText: TextView
    private lateinit var termSelection: Spinner
    private lateinit var departmentSelection: Spinner
    private lateinit var courseNumberField: EditText
    private lateinit var sectionNumberField: EditText
    private lateinit var startingAfterSelection: Spinner
    private lateinit var endingBeforeSelection: Spinner
    private lateinit var keywordField: EditText
    private lateinit var instructorLastNameField: EditText
    private lateinit var areaApprovalSelection: Spinner
    private lateinit var submitButton: Button

    private val termOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }
    private val departmentOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }
    private val startingAfterOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }
    private val endingBeforeOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }
    private val areaApprovalOptions: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_search_courses, container, false)

        webView = requireActivity().webview

        fieldsContainer = root.findViewById(R.id.search_fields_container)

        promptText = root.findViewById(R.id.prompt_text_search)
        termSelection = root.findViewById(R.id.term_dropdown_search)
        departmentSelection = root.findViewById(R.id.department_search)
        courseNumberField = root.findViewById(R.id.course_number_search)
        sectionNumberField = root.findViewById(R.id.section_search)
        startingAfterSelection = root.findViewById(R.id.meeting_after_dropdown_search)
        endingBeforeSelection = root.findViewById(R.id.ending_before_dropdown_search)
        keywordField = root.findViewById(R.id.course_keyword_search)
        instructorLastNameField = root.findViewById(R.id.course_instructor_search)
        areaApprovalSelection = root.findViewById(R.id.area_approval_selection_search)
        submitButton = root.findViewById(R.id.submit_search)

        webView.webViewClient = wvc
        webView.reload()

        termOptions.observe(viewLifecycleOwner, createStringListObserver(termSelection))
        departmentOptions.observe(viewLifecycleOwner, createStringListObserver(departmentSelection))
        startingAfterOptions.observe(viewLifecycleOwner, createStringListObserver(startingAfterSelection))
        endingBeforeOptions.observe(viewLifecycleOwner, createStringListObserver(endingBeforeSelection))
        areaApprovalOptions.observe(viewLifecycleOwner, createStringListObserver(areaApprovalSelection))

        submitButton.setOnClickListener {
            if(termSelection.selectedItemPosition != 0) {
                webView.loadUrlWithJs(
                    "document.getElementById('VAR1').selectedIndex = '" + termSelection.selectedItemPosition + "';" +
                        "document.getElementById('LIST_VAR1_1').selectedIndex = '" + departmentSelection.selectedItemPosition + "';" +
                        "document.getElementById('LIST_VAR3_1').value = '" + courseNumberField.text + "';" +
                        "document.getElementById('LIST_VAR4_1').value = '" + sectionNumberField.text + "';" +
                        "document.getElementById('VAR7').selectedIndex = '" + startingAfterSelection.selectedItemPosition + "';" +
                        "document.getElementById('VAR8').selectedIndex = '" + endingBeforeSelection.selectedItemPosition + "';" +
                        "document.getElementById('VAR3').value = '" + keywordField.text + "';" +
                        "document.getElementById('VAR9').value = '" + instructorLastNameField.text + "';" +
                        "document.getElementById('VAR23').selectedIndex = '" + areaApprovalSelection.selectedItemPosition + "';" +
                            webView.clickElementByNameTag("SUBMIT2")
                )
            } else {
                Toast.makeText(requireContext(),"Term is a required field", Toast.LENGTH_LONG).show()
            }
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
        fieldsContainer.isGone = false
    }

    private fun hideFields() {
        fieldsContainer.isGone = true
    }

    private fun splitStringToList(listStr: String) : MutableList<String> {
        val formattedListStr = listStr.replace("\"", "")
        val list: MutableList<String> = formattedListStr.substring(0, formattedListStr.length-1).split(',').toMutableList()
        for (i in list.indices) {
            list[i] = list[i].replace(";", ",")
        }
        return list
    }

    private fun createStringListObserver(selectionSpinner: Spinner) : Observer<List<String>> {
        val listObserver = Observer<List<String>> { newList ->
            val adapter : ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, newList)
            selectionSpinner.adapter = adapter
        }
        return listObserver
    }

    private val wvc = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String){
            view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })(), document.title;")
            { html ->
                Log.i("Schedule", "title of html is ${view.title}")
                if(view.title.toLowerCase().indexOf("search for classes") != -1) {
                    Log.i("CourseSearch", "Arrived at searching fields page")
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("VAR1")+
                            "})()") { termOptions.value = splitStringToList(it) }
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("LIST_VAR1_1")+
                            "})()") { departmentOptions.value = splitStringToList(it) }
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("VAR7")+
                            "})()") { startingAfterOptions.value = splitStringToList(it) }
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("VAR8")+
                            "})()") { endingBeforeOptions.value = splitStringToList(it) }
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getAllChildrenInnerTextById("VAR23")+
                            "})()") { areaApprovalOptions.value = splitStringToList(it) }
                } else if(view.title.toLowerCase().indexOf("webadvisor for students") != -1) {
                    webView.loadUrlWithJs(webView.clickElementBySpan("Search for Classes", "bodyForm"))
                } else if(view.title.toLowerCase().indexOf("section selection results") != -1) {
                    Log.i("CourseSearch", "Arrived at searching results")
                    Log.i("Schedule", "converting table")
                    view.evaluateJavascript("javascript:(function(){"+
                            webView.getTableByDivId("GROUP_Grp_LIST_VAR6")+
                            "})()") { table ->
                        Log.i("Schedule", "table: $table")
                        //val tableArr = convertStringTo2DArray(table)
                        val tableArr = webView.convertTableStringTo2DArray(table)
                        Log.i("Schedule", "table: $tableArr")
                        requireActivity().runOnUiThread {
                            hideFields()
                            val fragment = SearchRecyclerFragment.newInstance(tableArr.toTypedArray())
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

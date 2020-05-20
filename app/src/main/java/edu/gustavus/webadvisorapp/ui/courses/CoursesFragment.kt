package edu.gustavus.webadvisorapp.ui.courses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import edu.gustavus.webadvisorapp.ui.courses.subfragments.CourseScheduleFragment
import edu.gustavus.webadvisorapp.ui.courses.subfragments.SearchCoursesFragment
import edu.gustavus.webadvisorapp.ui.courses.subfragments.TranscriptFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_courses.*

class CoursesFragment : Fragment() {

    private lateinit var webView: WAWebView
    private lateinit var searchButton: Button
    private lateinit var currentClassesButton: Button
    private lateinit var transcriptButton: Button

    private lateinit var buttonGroup: Group

    private val coursesViewModel: CoursesViewModel by lazy {
        ViewModelProviders.of(this).get(CoursesViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_courses, container, false)

        webView = requireActivity().webview

        searchButton = root.findViewById(R.id.search_courses) as Button
        currentClassesButton = root.findViewById(R.id.view_current_classes) as Button
        transcriptButton = root.findViewById(R.id.view_transcript) as Button
        buttonGroup = root.findViewById(R.id.button_group) as Group

        searchButton.setOnClickListener {
            hideButtons()
            val fragment =
                SearchCoursesFragment()
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            childFragmentManager.beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        currentClassesButton.setOnClickListener {
            hideButtons()
            val fragment =
                CourseScheduleFragment()
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            childFragmentManager.beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        transcriptButton.setOnClickListener {
            hideButtons()
            val fragment =
                TranscriptFragment()
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            childFragmentManager.beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        childFragmentManager.addOnBackStackChangedListener {
            if(childFragmentManager.backStackEntryCount == 0) {
                showButtons()
                enableButtons(false)
                Log.i("CoursesFragment","navigating to student menu")
                webView.navigateToStudentMenu() {
                    enableButtons(true)
                }
            }
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        enableButtons(false)
        Log.i("CoursesFragment","navigating to student menu")
        webView.navigateToStudentMenu() {
            enableButtons(true)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            enableButtons(false)
            Log.i("CoursesFragment", "navigating to student menu")
            webView.navigateToStudentMenu() {
                enableButtons(true)
            }
        }
    }

    private fun hideButtons() {
        coursesViewModel.buttonsHidden = true
        updateButtons()
    }

    private fun showButtons() {
        coursesViewModel.buttonsHidden = false
        updateButtons()
    }

    private fun enableButtons(enable: Boolean){
        coursesViewModel.buttonsEnabled = enable
        updateButtons()
    }

    private fun updateButtons() {
        button_group.isGone = coursesViewModel.buttonsHidden
        for(button in buttonGroup.referencedIds) {
            if(requireView().findViewById<View>(button) is Button)
                requireView().findViewById<Button>(button).isEnabled = coursesViewModel.buttonsEnabled
        }
        /*searchButton.isGone = coursesViewModel.buttonsHidden
        currentClassesButton.isGone = coursesViewModel.buttonsHidden
        transcriptButton.isGone = coursesViewModel.buttonsHidden*/
    }
}
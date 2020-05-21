package edu.gustavus.webadvisorapp.ui.reslife

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import edu.gustavus.webadvisorapp.R
import edu.gustavus.webadvisorapp.WAWebView
import edu.gustavus.webadvisorapp.ui.courses.subfragments.CourseScheduleFragment
import edu.gustavus.webadvisorapp.ui.courses.subfragments.SearchCoursesFragment
import edu.gustavus.webadvisorapp.ui.courses.subfragments.TranscriptFragment
import edu.gustavus.webadvisorapp.ui.reslife.subfragments.MailBoxFragment
import edu.gustavus.webadvisorapp.ui.reslife.subfragments.MealPlanFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_courses.*

class ResLifeFragment : Fragment() {

    private lateinit var webView: WAWebView

    private lateinit var textView: TextView
    private lateinit var mailboxButton: Button
    private lateinit var mealPlanButton: Button

    private lateinit var buttonGroup: Group

    private val resLifeViewModel: ResLifeViewModel by lazy {
        ViewModelProviders.of(this).get(ResLifeViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_reslife, container, false)

        webView = requireActivity().webview

        textView = root.findViewById(R.id.text_reslife)
        mailboxButton = root.findViewById(R.id.mail_box_button)
        mealPlanButton = root.findViewById(R.id.meal_plan_button)
        buttonGroup = root.findViewById(R.id.reslife_button_group) as Group

        mailboxButton.setOnClickListener {
            hideButtons()
            val fragment = MailBoxFragment()
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            childFragmentManager.beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        mealPlanButton.setOnClickListener {
            hideButtons()
            val fragment = MealPlanFragment()
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
        // getting shown
        if (!hidden) {
            enableButtons(false)
            Log.i("CoursesFragment", "navigating to student menu")
            webView.navigateToStudentMenu() {
                enableButtons(true)
            }
        }
    }

    private fun hideButtons() {
        resLifeViewModel.buttonsHidden = true
        updateButtons()
    }

    private fun showButtons() {
        resLifeViewModel.buttonsHidden = false
        updateButtons()
    }

    private fun enableButtons(enable: Boolean){
        resLifeViewModel.buttonsEnabled = enable
        updateButtons()
    }

    private fun updateButtons() {
        buttonGroup.isGone = resLifeViewModel.buttonsHidden
        for(button in buttonGroup.referencedIds) {
            if(requireView().findViewById<View>(button) is Button)
                if (!resLifeViewModel.buttonsEnabled || webView.loggedIn || button == R.id.search_courses)
                    requireView().findViewById<Button>(button).isEnabled = resLifeViewModel.buttonsEnabled
        }
        if (!webView.loggedIn && resLifeViewModel.buttonsEnabled) {
            Toast.makeText(requireContext(),"Please login to use all functionality", Toast.LENGTH_LONG).show()
        }
    }
}

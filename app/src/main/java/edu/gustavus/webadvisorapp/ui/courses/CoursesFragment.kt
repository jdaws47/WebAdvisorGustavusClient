package edu.gustavus.webadvisorapp.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.gustavus.webadvisorapp.R

class CoursesFragment : Fragment() {

    private val coursesViewModel: CoursesViewModel by lazy {
        ViewModelProviders.of(this).get(CoursesViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_courses, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        coursesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
package edu.gustavus.webadvisorapp.ui.reslife.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class MealPlanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("MealPlan","Passed to MealPlanFragment correctly")

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
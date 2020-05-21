package edu.gustavus.webadvisorapp

import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.gustavus.webadvisorapp.ui.courses.CoursesFragment
import edu.gustavus.webadvisorapp.ui.home.HomeFragment
import edu.gustavus.webadvisorapp.ui.reslife.ResLifeFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val webView: WAWebView = findViewById(R.id.webview)

        CookieSyncManager.createInstance(applicationContext)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        //CookieManager.getInstance().removeAllCookies(null)
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.domStorageEnabled = true
        webView.isGone = true

        /* DEFAULT FRAGMENT IMPLEMENTATION:
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_courses, R.id.navigation_home, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
        */

        navView.setOnNavigationItemSelectedListener {
            val selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.navigation_home -> {
                    changeFragment(HomeFragment(), HomeFragment::class.java.simpleName)
                }
                R.id.navigation_courses -> {
                    changeFragment(CoursesFragment(), CoursesFragment::class.java.simpleName)
                }
                R.id.navigation_reslife -> {
                    changeFragment(ResLifeFragment(), ResLifeFragment::class.java.simpleName)
                }
            }

            true
        }

        Log.i("MainActivityHome","backstack: ${supportFragmentManager.backStackEntryCount}")

        //Manually displaying the first fragment - one time only
        changeFragment(HomeFragment(), HomeFragment::class.java.simpleName)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        Log.i("MainActivityHome","backstack: ${supportFragmentManager.backStackEntryCount}")
        Log.i("MainActivityHome","backstack: ${supportFragmentManager.primaryNavigationFragment}")
    }


    private fun changeFragment(fragment: Fragment, tagFragmentName: String?) {
        val mFragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = mFragmentManager.beginTransaction()
        val currentFragment: Fragment? = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }
        var fragmentTemp: Fragment? = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName)
        } else {
            fragmentTransaction.show(fragmentTemp)
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        //fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }
}

package core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import core.lib.R
import core.lib.databinding.ActivityBottomNavigationBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * Main Bottom Navigation Activity launcher class holding navHost and initial position at Splash.
 */
class BottomNavigationActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>
    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var navController: NavController
    var ishidemenu: Boolean = false

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navigation)
        binding.bottomNavViewModel = ViewModelProvider(this).get(BottomNavViewModel::class.java)
        binding.toolbarViewModel = ViewModelProvider(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        setUpNavigation()
        // temp fix for app restarting while switching apps
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action.equals(Intent.ACTION_MAIN)) {

            finish()
            return
        }
    }

    private fun setUpNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        setupWithNavController(navView, navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()


    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.action_menu)
        menuItem.isVisible = !ishidemenu
        return true
    }

    /*
    To hide navigation and status bars
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                val editText = currentFocus;
                if (editText is EditText) {
                    val outRect = Rect()
                    editText.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        editText.clearFocus()
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)
                    }
                    editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                hideSystemUI()
                                return false
                            }
                            return false
                        }

                    })
                }
                hideSystemUI()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun hidemenu() {
        ishidemenu = true
        invalidateOptionsMenu()
    }

    fun showmenu() {
        ishidemenu = false
        invalidateOptionsMenu()
    }

    fun setToolbarTitle(title: String) {
        binding.toolbarViewModel?.toolbarTitle?.set(title)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


}

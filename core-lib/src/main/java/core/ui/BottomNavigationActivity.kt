package core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import core.appstate.AppState
import core.lib.R
import core.lib.databinding.ActivityBottomNavigationBinding
import core.lib.databinding.NavDrawerHeaderBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


/**
 * Main Bottom Navigation Activity launcher class holding navHost and initial position at Splash.
 */
class BottomNavigationActivity : AppCompatActivity(), HasAndroidInjector,
    NavigationView.OnNavigationItemSelectedListener {

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
        setUpNavigationDrawer()
        setMenuBinding()
        // temp fix for app restarting while switching apps
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action.equals(Intent.ACTION_MAIN)
        ) {

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

    private fun setUpNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.navSlideView.setNavigationItemSelectedListener(this);
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun setMenuBinding() {
        val viewHeader = binding.navSlideView.getHeaderView(0)
        val navViewHeaderBinding: NavDrawerHeaderBinding = NavDrawerHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.bottomNavViewModel = binding.bottomNavViewModel
    }

    fun setMenuItem(isGuest: Boolean) {
        val navMenu: Menu = binding.navSlideView.getMenu()
        navMenu.findItem(R.id.nav_graph_logout).isVisible = !isGuest
        setMenuItemColor(navMenu.findItem(R.id.nav_graph_logout), getColor(R.color.logout_red))
        navMenu.findItem(R.id.nav_graph_sign_up).isVisible = isGuest
        setMenuItemColor(navMenu.findItem(R.id.nav_graph_sign_up), getColor(R.color.sign_in_blue))
    }

    private fun setMenuItemColor(menu: MenuItem, color: Int) {
        val spanString = SpannableString(menu.title.toString())
        spanString.setSpan(ForegroundColorSpan(color), 0, spanString.length, 0)
        menu.title = spanString
    }


    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()


    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.action_menu)
        menuItem.isVisible = !ishidemenu
        return true
    }

    fun onDrawerItemClick(item: MenuItem) {
        binding.drawerLayout.openDrawer(Gravity.RIGHT)
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
                        override fun onEditorAction(
                            v: TextView?,
                            actionId: Int,
                            event: KeyEvent?
                        ): Boolean {
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            R.id.nav_graph_about, R.id.nav_graph_support, R.id.nav_graph_settings, R.id.nav_graph_faq, R.id.nav_graph_software_updates, R.id.nav_graph_sign_up -> {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                false
            }
            R.id.nav_graph_logout -> {
                AppState.logout()
                AppState.setIsGuest(true)
                binding.bottomNavViewModel?.isGuestBase?.set(true)
                setMenuItem(true)
                binding.bottomNavViewModel?.userEmailBase?.set("")
                binding.bottomNavViewModel?.userFirstNameBase?.set("")
                binding.bottomNavViewModel?.userLastNameBase?.set("")
                binding.bottomNavViewModel?.userPhoneBase?.set("")
                binding.bottomNavViewModel?.refreshMenu(this)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                false
            }
            else -> {
                false
            }
        }

    }
}

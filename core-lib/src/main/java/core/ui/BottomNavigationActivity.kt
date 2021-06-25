package core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import core.ui.adapter.ExpandableMenuListAdapter
import core.ui.common.NoScrollExListView
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
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
    lateinit var expandableListView: NoScrollExListView
    lateinit var expandableListAdapter : ExpandableMenuListAdapter

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
        // temp fix for app restarting while switching apps
        bindMenuHeader()
        populateExpandableList()

        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action.equals(Intent.ACTION_MAIN)
        ) {

            finish()
            return
        }
        binding.bottomNavViewModel!!.disposable += binding.bottomNavViewModel!!.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(
        event: BottomNavViewModel.Event?
    ) {
        when (event) {
            is BottomNavViewModel.Event.NavigateToLogin -> {
                Log.d("EVENT", "LOGOUT CLICKED")
                binding.bottomNavViewModel?.visibility?.set(false)
                binding.toolbarViewModel?.isShowActionBar?.set(false)
                binding.toolbarViewModel?.isShowTransparentActionBar?.set(false)
                hidemenu()
                navController.navigate(R.id.action_splashActivity_to_LoginFragment)
            }

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


    private fun populateExpandableList() {
        expandableListAdapter = ExpandableMenuListAdapter(this, binding.bottomNavViewModel!!.headerList,
            binding.bottomNavViewModel!!.childList)
        expandableListView =  binding.navSlideView.getHeaderView(0).findViewById(R.id.expandableListView)
        expandableListView.setAdapter(expandableListAdapter)
        expandableListView.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
            if (binding.bottomNavViewModel!!.headerList.get(groupPosition).subMenu == null) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                binding.bottomNavViewModel!!.headerList.get(groupPosition).menuName?.let {
                    handlemenuClick(
                        it
                    )
                }
            } else {
                if (parent.isGroupExpanded(groupPosition)) {
                    v?.findViewById<ImageView>(R.id.ic_menu_drop_image)
                        ?.setImageResource(R.drawable.ic_menu_down)
                    // Do your Staff
                } else {
                    v?.findViewById<ImageView>(R.id.ic_menu_drop_image)
                        ?.setImageResource(R.drawable.ic_menu_up)
                    // Expanded ,Do your Staff
                }
            }
            false
        })
        expandableListView.setOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
//            if(binding.bottomNavViewModel!!.equals(this.getString(R.string.str_menu_ws_pro_settings))){
//                navController.navigate(R.id.action_fragments_to_wssettings)
//            }
            if (binding.bottomNavViewModel!!.childList.get(binding.bottomNavViewModel!!.headerList.get(groupPosition)) != null) {
                if(binding.bottomNavViewModel!!.childList.get(binding.bottomNavViewModel!!.headerList.get(groupPosition))?.get(childPosition)
                        ?.menuName!!.equals(this.getString(R.string.str_menu_ws_pro_settings))){

                    if(navController.currentDestination?.label?.equals("Home")!!) {
                        navController.navigate(R.id.action_homeFragment_to_wssettings_fragment)
                    }
                }


                if ( binding.bottomNavViewModel!!.childList.get(binding.bottomNavViewModel!!.headerList.get(groupPosition))?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.str_menu_manage_projector))){
                    if(navController.currentDestination?.label?.equals("Home")!!) {
                        navController.navigate(R.id.action_homeFragment_to_nav_graph_manage)
                    }
                }

                if ( binding.bottomNavViewModel!!.childList.get(binding.bottomNavViewModel!!.headerList.get(groupPosition))?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.privacy_policy))){
                    if(navController.currentDestination?.label?.equals("Home")!!) {
                        navController.navigate(R.id.action_homeFragment_to_privacyAndSettingFragment)
                    }
                }

                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
            false
        })
        expandableListAdapter.notifyDataSetChanged()
    }

    fun refreshMenuItem() {
        expandableListAdapter.notifyDataSetChanged()
    }

    fun bindMenuHeader() {
        val viewHeader = binding.navSlideView.getHeaderView(0)
        val navViewHeaderBinding: NavDrawerHeaderBinding = NavDrawerHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.bottomNavViewModel = binding.bottomNavViewModel
    }

    private fun setMenuItemColor(menu: MenuItem, color: Int) {
        val spanString = SpannableString(menu.title.toString())
        spanString.setSpan(ForegroundColorSpan(color), 0, spanString.length, 0)
        menu.title = spanString
    }
      
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            return
        }
        super.onBackPressed()
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

    fun setToolbarIcon() {
        binding.toolbar?.setNavigationIcon(R.drawable.ic_back_button)
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

    @SuppressLint("ResourceType")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            R.id.nav_graph_settings, R.id.nav_graph_software_updates -> {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                false
            }
              R.id.nav_graph_support -> {
              binding.drawerLayout.closeDrawer(Gravity.RIGHT)

                navController.navigate(R.id.action_fragments_to_customerCareFragment)
                true
            }

            R.id.nav_graph_about -> {
             /*   binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                navController.navigate(R.id.action_homeFragment_to_aboutAppFragment)*/
                true
            }
            R.id.nav_graph_faq -> {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                navController.navigate(R.id.action_destination_to_FQAfragment)
                true
            }
            R.id.nav_graph_logout -> {
                logoutUser(true)
                true
            }
            R.id.nav_graph_sign_up -> {
                logoutUser(false)
                true
            }
           else -> {
                false
            }
        }

    }

    private fun logoutUser(isLogout: Boolean) {
        AppState.logout()
        AppState.setIsLogged(false)
        binding.bottomNavViewModel?.isGuestBase?.set(true)
        binding.bottomNavViewModel?.userEmailBase?.set("")
        binding.bottomNavViewModel?.userFirstNameBase?.set("")
        binding.bottomNavViewModel?.userLastNameBase?.set("")
        binding.bottomNavViewModel?.userPhoneBase?.set("")
        binding.bottomNavViewModel?.refreshMenu(this)
        binding.drawerLayout.closeDrawer(Gravity.RIGHT)
        if (isLogout) {
            binding.bottomNavViewModel?.logout()
        } else {
            binding.bottomNavViewModel?.sigin()
        }
    }

    private fun handlemenuClick (selectedmenu : String){
        if (selectedmenu.equals(this.getString(R.string.str_menu_customersupport))){
            if(navController.currentDestination?.label?.equals("Home")!!) {
                navController.navigate(R.id.action_fragments_to_customerCareFragment)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        } else  if (selectedmenu.equals(this.getString(R.string.str_menu_faq))){
            if(navController.currentDestination?.label?.equals("Home")!!) {
                navController.navigate(R.id.action_destination_to_FQAfragment)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)

            }
        }else  if (selectedmenu.equals(this.getString(R.string.str_menu_logout))){
            if(navController.currentDestination?.label?.equals("Home")!!) {
                logoutUser(true)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }else  if (selectedmenu.equals(this.getString(R.string.str_menu_signin))){
            if(navController.currentDestination?.label?.equals("Home")!!) {
                logoutUser(false)
            }
        }
        else  if (selectedmenu.equals(this.getString(R.string.about_app_policies))){
            if(navController.currentDestination?.label?.equals("Home")!!) {
              /*  navController.navigate(R.id.action_homeFragment_to_aboutAppFragment)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)*/
            }
        }
        else{
            Toast.makeText(this, selectedmenu, Toast.LENGTH_LONG).show()
        }
    }
}

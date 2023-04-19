package core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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
import core.network.NetworkUtility
import core.ui.adapter.ExpandableMenuListAdapter
import core.ui.common.NoScrollExListView
import core.ui.common.Utility
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


/**
 * Main Bottom Navigation Activity launcher class holding navHost and initial position at Splash.
 */
class BottomNavigationActivity : AppCompatActivity(), HasAndroidInjector,
    NavigationView.OnNavigationItemSelectedListener, Utility.CustomCallbackDialogListener {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>
    private lateinit var binding: ActivityBottomNavigationBinding
    private lateinit var navController: NavController
    lateinit var expandableListView: NoScrollExListView
    lateinit var expandableListAdapter: ExpandableMenuListAdapter
    lateinit var navViewHeaderBinding: NavDrawerHeaderBinding
    private lateinit var versionDisposable: Disposable
    private lateinit var bottomNavViewModel: BottomNavViewModel
    private lateinit var toolbarViewModel: ToolbarViewModel

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        bottomNavViewModel = ViewModelProvider(this).get(BottomNavViewModel::class.java)
        toolbarViewModel = ViewModelProvider(this).get(ToolbarViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navigation)
        binding.bottomNavViewModel = bottomNavViewModel
        binding.toolbarViewModel = toolbarViewModel
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
        window.navigationBarColor = resources.getColor(R.color.nav_item_grey2);
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; //For setting material color into black of the navigation bar

        /**
         * Deeplinking
         */
        handleIntent(intent)
        binding.toolbar.setNavigationOnClickListener {
            Log.d("NAVIGTAION", "HERE=====")
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun setToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            /**
             *
             * Popping all back stack and set start destination
             */
            navController.popBackStack( R.id.nav_graph,true)
            navController.navigate( R.id.destination_splash)
            appLinkData?.lastPathSegment?.also { segmentId ->
                Log.d("DEEPLINK", segmentId)
                when {
                    segmentId.endsWith("myLibrary") -> {
                        // PATTERN LIBRARY
                        if (AppState.getIsLogged()) {
                            if (NetworkUtility.isNetworkAvailable(this)) {
                                val userId = appLinkData?.getQueryParameter("userId")
                                Log.d("DEEPLINK", "USER ID :$userId")
                                if (userId.equals(AppState.getCustNO())) {
                                    if (navController.currentDestination?.id == R.id.destination_splash) {
                                        val bundle = bundleOf(
                                            "DEEPLINK" to "LIBRARY"
                                        )
                                        navController.navigate(
                                            R.id.action_splashActivity_to_HomeFragment,
                                            bundle
                                        )
                                    }

                                    return
                                } else {
                                    showAlert(getString(R.string.customer_not_match))
                                }
                            } else {
                                showAlert(getString(R.string.no_internet_available))
                            }
                        }


                    }
                    segmentId.endsWith("mobile-pattern-details") -> {
                        // PATTERN MySubscriptionLibrary
                        if (AppState.getIsLogged()) {
                            if (NetworkUtility.isNetworkAvailable(this)) {
                                val userId = appLinkData?.getQueryParameter("userId")
                                if (userId.equals(AppState.getCustNO())) {
                                    val designId = appLinkData?.getQueryParameter("designId")
                                    val orderId = appLinkData?.getQueryParameter("orderID")
                                    val mannequinId = appLinkData?.getQueryParameter("mannequinId")
                                    Log.d("DEEPLINK", " USER ID=$userId")
                                    Log.d("DEEPLINK", " DESIGN ID=$designId")
                                    Log.d("DEEPLINK", " MANNEQUIN ID=$mannequinId")
                                    Log.d("DEEPLINK", " ORDER ID=$orderId")

                                    if (navController.currentDestination?.id == R.id.destination_splash) {
                                        val bundle = bundleOf(
                                            "DEEPLINK" to "DETAIL",
                                            "clickedID" to designId,
                                            "clickedOrderNumber" to orderId,
                                            "mannequinId" to mannequinId
                                        )
                                        Log.d("PATTERN ID", "$designId")
                                        navController.navigate(
                                            R.id.action_splashActivity_to_HomeFragment,
                                            bundle
                                        )
                                    }

                                } else {
                                    showAlert(getString(R.string.customer_not_match))
                                }
                            } else {
                                showAlert(getString(R.string.no_internet_available))
                            }
                        }

                        return
                    }

                }

            }
        }
    }

    private fun isNumber(s: String?): Boolean {
        return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
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
                navController.navigate(R.id.action_splashActivity_to_LoginFragment)
            }
            is BottomNavViewModel.Event.OnClickSignIn -> {
                Log.d("EVENT", "SIGNIN CLICKED")
                binding.bottomNavViewModel?.visibility?.set(false)
                binding.toolbarViewModel?.isShowActionBar?.set(false)
                binding.toolbarViewModel?.isShowTransparentActionBar?.set(false)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                navController.navigate(R.id.action_splashActivity_to_LoginFragment)
            }

            else -> {}
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
                binding.bottomNavViewModel?.showProgress?.set(false)
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

    }


    private fun populateExpandableList() {
        expandableListAdapter = ExpandableMenuListAdapter(
            this, binding.bottomNavViewModel!!.headerList,
            binding.bottomNavViewModel!!.childList
        )
        expandableListView =
            binding.navSlideView.getHeaderView(0).findViewById(R.id.expandableListView)
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
            if (binding.bottomNavViewModel!!.childList.get(
                    binding.bottomNavViewModel!!.headerList.get(
                        groupPosition
                    )
                ) != null
            ) {
                if (binding.bottomNavViewModel!!.childList.get(
                        binding.bottomNavViewModel!!.headerList.get(
                            groupPosition
                        )
                    )?.get(childPosition)
                        ?.menuName!!.equals(this.getString(R.string.str_menu_ws_pro_settings))
                ) {

                    if (navController.currentDestination?.label?.equals("Home")!! ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                    ) {
                        navController.navigate(
                            if (navController.currentDestination?.label?.equals("Home")!!)
                                R.id.action_homeFragment_to_wssettings_fragment
                            else
                                R.id.action_pattern_description_to_wssettings_fragment
                        )
                    }
                }


                if (binding.bottomNavViewModel!!.childList.get(
                        binding.bottomNavViewModel!!.headerList.get(
                            groupPosition
                        )
                    )?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.str_menu_manage_projector))
                ) {
                    if (navController.currentDestination?.label?.equals("Home")!! ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                    ) {
                        navController.navigate(
                            if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_homeFragment_to_nav_graph_manage
                            else R.id.action_pattern_description_to_nav_graph_manage
                        )
                    }
                }

                if (binding.bottomNavViewModel!!.childList.get(
                        binding.bottomNavViewModel!!.headerList.get(
                            groupPosition
                        )
                    )?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.str_menu_softwareupdate))
                ) {
                    RxBus.publish(RxBusEvent.CheckVersion(true))

                }

                if (binding.bottomNavViewModel!!.childList.get(
                        binding.bottomNavViewModel!!.headerList.get(
                            groupPosition
                        )
                    )?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.account_info))
                ) {

                    if (navController.currentDestination?.label?.equals("Home")!! ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                    ) {
                        navController.navigate(
                            if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_homeFragment_to_accountInfoFragment
                            else R.id.action_pattern_description_to_accountInfoFragment
                        )
                    }
                }

                if (binding.bottomNavViewModel!!.childList.get(
                        binding.bottomNavViewModel!!.headerList.get(
                            groupPosition
                        )
                    )?.get(childPosition)
                        ?.menuName.equals(this.getString(R.string.subscription_info))
                ) {

                    if (navController.currentDestination?.label?.equals("Home")!! ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                        (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
                    ) {
                        navController.navigate(
                            if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_homeFragment_to_subscriptionInfoFragment
                            else R.id.action_patternDescriptionFragment_to_subscriptionInfoFragment
                        )
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
        navViewHeaderBinding = NavDrawerHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.bottomNavViewModel = binding.bottomNavViewModel
    }

    fun setEmaildesc() {
        if (AppState.getIsLogged()) {
            val email = AppState.getEmail()
            navViewHeaderBinding.textEmail.text = "$email"
            navViewHeaderBinding.textName.text =AppState.getFirstName()
//            navViewHeaderBinding.textName.text =AppState.getFirstName()  + AppState.getLastName()
            Log.d("getSubscriptionStatus","getSubscriptionStatus: ${AppState.getSubscriptionStatus()}")
            if (AppState.getSubDate()
                    .isEmpty() || AppState.getSubDate() == null || AppState.isSubscriptionValid() == false
            ) {
                navViewHeaderBinding.subscriptionDays.text ="0 days left"
            } else {
                val days = Utility.getTotalNumberOfDays(AppState.getSubDate())
                navViewHeaderBinding.subscriptionDays.text ="$days days left"
            }
            navViewHeaderBinding.textPhone.text =AppState.getMobile()
        } else {
            setUnderlinestyle(navViewHeaderBinding)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUnderlinestyle(navViewHeaderBinding: NavDrawerHeaderBinding) {

        val text: String? = getString(R.string.sign_in_to_explore_more)
        val spannable = SpannableString(text)

        spannable.setSpan(
            UnderlineSpan(),
            0, 7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        navViewHeaderBinding.textEmail.text = spannable
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
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        var menuItem: MenuItem = menu!!.findItem(R.id.action_menu)
//        menuItem.isVisible = !ishidemenu
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

    fun hideDrawerLayout() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayout.closeDrawer(Gravity.RIGHT)
    }

    fun setToolbarTitle(title: String) {
        binding.toolbarViewModel?.toolbarTitle?.set(title)
    }

    fun setToolbarIcon() {
        binding.toolbar?.setNavigationIcon(R.drawable.ic_back_button)
    }

    override fun onPause() {
        binding.drawerLayout.closeDrawer(Gravity.RIGHT)
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()

        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // All below using to hide navigation bar
            val currentApiVersion = Build.VERSION.SDK_INT
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            // This work only for android 4.4+
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
                window.decorView.systemUiVisibility = flags
                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                val decorView = window.decorView
                decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
                    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                        decorView.systemUiVisibility = flags
                    }
                }
            }
        }
        /*   window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                   or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                   or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                   or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                   or View.SYSTEM_UI_FLAG_FULLSCREEN
                   or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)*/


    }

    @SuppressLint("ResourceType")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            R.id.nav_graph_settings, R.id.nav_graph_software_updates -> {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)

                true
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
            R.id.nav_graph_mainfaq -> {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
                navController.navigate(R.id.action_fragment_to_FAQGlossaryfragment)
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
        //binding.bottomNavViewModel?.refreshMenu(this)
        binding.drawerLayout.closeDrawer(Gravity.RIGHT)
        if (isLogout) {
            binding.bottomNavViewModel?.logout()
        } else {
            binding.bottomNavViewModel?.sigin()
        }
    }

    private fun handlemenuClick(selectedmenu: String) {
        if (selectedmenu.equals(this.getString(R.string.str_menu_customersupport))) {
            if (navController.currentDestination?.label?.equals("Home")!! ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                navController.navigate(
                    if (navController.currentDestination?.label?.equals("Home") == true) R.id.action_fragments_to_customerCareFragment
                    else R.id.action_pattern_description_to_customerCareFragment
                )
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        } else if (selectedmenu.equals(this.getString(R.string.str_menu_faq))) {
            if (navController.currentDestination?.label?.equals("Home") == true ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                navController.navigate(
                    if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_fragment_to_FAQGlossaryfragment
                    else R.id.action_pattern_description_to_FAQGlossaryfragment
                )
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)

            }
        } else if (selectedmenu.equals(this.getString(R.string.str_menu_logout))) {
            if (navController.currentDestination?.label?.equals("Home")!! ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                logoutUser(true)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        } else if (selectedmenu.equals(this.getString(R.string.str_menu_signin))) {
            if (navController.currentDestination?.label?.equals("Home")!! ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                logoutUser(false)
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        } else if (selectedmenu.equals(this.getString(R.string.about_the_app_amp_policies))) {
            if (navController.currentDestination?.label?.equals("Home")!! ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                navController.navigate(
                    if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_homeFragment_to_aboutAppFragment
                    else R.id.action_pattern_description_to_aboutAppFragment
                )
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        } else if (selectedmenu.equals(this.getString(R.string.share_your_craft))) {
            if (navController.currentDestination?.label?.equals("Home")!! ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragment) ||
                (navController.currentDestination?.id == R.id.patternDescriptionFragmentFromHome)
            ) {
                navController.navigate(
                    if (navController.currentDestination?.label?.equals("Home")!!) R.id.action_homeFragment_to_shareYourCraft
                    else R.id.action_pattern_description_to_shareYourCraft
                )
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }
        else {
            Toast.makeText(this, selectedmenu, Toast.LENGTH_LONG).show()
        }
    }

    private fun showAlert(message: String) {
        Utility.getCommonAlertDialogue(
            this,
            "",
            message,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        //TODO("Not yet implemented")
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        // TODO("Not yet implemented")
    }
}

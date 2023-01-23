package com.ditto.menuitems_ui.aboutapp.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FragmentAboutAppBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutAppFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutAppFragment : BaseFragment(),Utility.CustomCallbackDialogListener  {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(AboutAppFragment::class.java.simpleName)
    }
    private val viewModel: AboutAppViewModel by ViewModelDelegate()
    lateinit var binding: FragmentAboutAppBinding

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutAppBinding.inflate(inflater).also {
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        binding.aboutwebview.setBackgroundColor(Color.TRANSPARENT)
        if (Utility.getWifistatus(requireContext())){
            bottomNavViewModel.showProgress.set(true)
            viewModel.fetchUserData()
        }else{
            bottomNavViewModel.showProgress.set(false)
            showAlert()
        }

        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)
                }
        }
    }

    private fun showAlert() {
        val errorMessage = "No Internet connection available !"
        Utility.getCommonAlertDialogue(requireContext(),"",errorMessage,"",getString(R.string.str_ok),this, Utility.AlertType.NETWORK
            ,Utility.Iconype.FAILED)
    }

    private fun handleEvent(event: AboutAppViewModel.Event) {
        when(event){
            AboutAppViewModel.Event.UpdateResponseText->{
                binding.aboutwebview.loadDataWithBaseURL(null,viewModel.getResponseText(),"text/html", "UTF-8",null)
                binding.aboutwebview.requestFocus()
               // binding.aboutwebview.settings.javaScriptEnabled=true
                binding.aboutwebview.webViewClient=object :WebViewClient(){
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                       logger.d("Error,${error?.description}")
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        bottomNavViewModel.showProgress.set(true)

                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        bottomNavViewModel.showProgress.set(false)
                    }
                }


            }

            AboutAppViewModel.Event.OnShowProgress->bottomNavViewModel.showProgress.set(true)
            AboutAppViewModel.Event.OnHideProgress->bottomNavViewModel.showProgress.set(false)


        }
    }


    private fun setuptoolbar(){
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle(getString(R.string.about_the_app_amp_policies))


    }


//    private fun handleEvent(event: AboutAppViewModel.Events) =
//        when (event) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                Html.fromHtml("html", Html.FROM_HTML_MODE_COMPACT)
//             else
//                Html.fromHtml("html").
//
//        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutAppFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutAppFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        //findNavController().popBackStack(R.id.home,true)
    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {
        TODO("Not yet implemented")
    }
}
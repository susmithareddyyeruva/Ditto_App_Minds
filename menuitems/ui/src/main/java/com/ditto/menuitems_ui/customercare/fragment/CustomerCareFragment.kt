package com.ditto.menuitems_ui.customercare.fragment


import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.CustomerCareFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class CustomerCareFragment : BaseFragment() {
    private val viewModel: CustomerCareViewModel by ViewModelDelegate()
    lateinit var binding : CustomerCareFragmentBinding
    lateinit var dynamicEmailData:String
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CustomerCareFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }


        return binding.ccContainer
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setuptoolbar()
        setemailteststyle()
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)   //Observing UI event
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).hideDrawerLayout()
    }
    private fun handleEvent(event: CustomerCareViewModel.Event) =
        when (event) {
            CustomerCareViewModel.Event.OnPhoneClicked ->
                makecall()
            CustomerCareViewModel.Event.OnEmailClicked ->
                sendmail()

        }

    fun makecall(){
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:${viewModel.mobileNumberValue.get().toString()}")
        startActivity(dialIntent)
    }
    fun sendmail(){
        val mailto = viewModel.getEmailId()
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mailto, null
            )
        )
        startActivity(Intent.createChooser(emailIntent, context?.getString(R.string.str_support)))

    }
    private fun setuptoolbar(){
        bottomNavViewModel.visibility.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        toolbarViewModel.isShowActionMenu.set(false)
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).setToolbarTitle("Customer Support")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setemailteststyle(){

        val res: Resources = getResources()
        val text: String = String.format(
            res.getString(
                R.string.str_email_text,
                viewModel.getEmailId()
            )
        )

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.sign_in_blue)),
            6, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            UnderlineSpan(),
            6, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // TDP-35 : added font family for email id
        val font = context?.let { ResourcesCompat.getFont(it, R.font.avenir_next_lt_pro_demi) }
        spannable.setSpan(font?.getStyle()?.let { StyleSpan(it) }, 6, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.emailtext.text = spannable
    }
}
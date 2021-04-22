package com.ditto.login.ui

import android.content.pm.PackageInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.ui.adapter.LoginViewPagerAdapter
import com.ditto.login.ui.databinding.LoginFragment2Binding
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.login_fragment2.*
import javax.inject.Inject


class LoginFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(LoginFragment::class.java.simpleName)
    }

    private val viewModel: LoginViewModel by ViewModelDelegate()
    lateinit var binding: LoginFragment2Binding


    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragment2Binding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.rootLayout
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pinfo: PackageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0)
        viewModel.versionName.set("Version "+pinfo.versionName)
        if (savedInstanceState == null) {
            viewModel.disposable += viewModel.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleEvent(it)   //Observing UI event
                }
        }

        viewModel.fetchViewPagerData()
        Log.d("list123", "${viewModel.viewPagerData.value?.size}")
        setViewpagerImageAdapter()
        setUIEvents()
        setupKeyboardListener(binding.root) // call in OnCreate or similar

    }

    private fun setViewpagerImageAdapter() {
        val adapter = LoginViewPagerAdapter()
        login_view_pager.adapter = adapter
        adapter.viewModel = viewModel
        login_view_pager.adapter?.notifyDataSetChanged()

        viewModel.viewPagerData.value?.let {
            if(it != null){
                adapter.setListData(it)
            }
        }

    }

    private fun setUIEvents() {
        binding.edittextPassword.customSelectionActionModeCallback =
            object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    return false
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    Log.d("actionMode","onDestroy")
                }
            }

        //for samsung keyboard
        binding.edittextPassword.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d("onTextChange","After")

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("onTextChange","Before")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>1)
                    binding.edittextPassword.setText("")
            }

        })
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: LoginViewModel.Event) =
        when (event) {
            is LoginViewModel.Event.OnLoginClicked -> {   //Re directing to On_boarding screen
                val bundle = bundleOf("UserId" to 0)
                if (findNavController().currentDestination?.id == R.id.destination_login) {
                    findNavController().navigate(R.id.action_loginFragment_to_OnboardingFragment, bundle)
                }
                Unit
            }

        }

    private fun setupKeyboardListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            if (Math.abs(view.rootView.height - (r.bottom - r.top)) > 100) { // if more than 100 pixels, its probably a keyboard...
                onKeyboardShow()
            }
        }
    }

    private fun onKeyboardShow() {
        binding.root.scrollToBottomWithoutFocusChange()
    }

    fun NestedScrollView.scrollToBottomWithoutFocusChange() { // Kotlin extension to scrollView
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta) //***/2 *****3/2
    }

}

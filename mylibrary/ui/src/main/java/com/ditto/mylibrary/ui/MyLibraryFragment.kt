package com.ditto.mylibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.ui.adapter.MyLibraryAdapter
import com.ditto.mylibrary.ui.databinding.MyLibraryFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import javax.inject.Inject

class MyLibraryFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(MyLibraryFragment::class.java.simpleName)
    }

    private val viewModel: MyLibraryViewModel by ViewModelDelegate()
    lateinit var binding: MyLibraryFragmentBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = MyLibraryFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getInt("UserId")?.let { viewModel.userId = (it) }
        toolbarViewModel.visibility.set(false)
        bottomNavViewModel.visibility.set(true)
        setTabsAdapter()

        toolbarViewModel.isShowActionBar.set(false)
        toolbarViewModel.isShowTransparentActionBar.set(true)
        //(activity as BottomNavigationActivity).setToolbarTitle("Pattern Description")
        (activity as BottomNavigationActivity).showmenu()

        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("handleEvent", "handle event")
            }

    }

    private fun setTabsAdapter() {
        val cfManager: FragmentManager = childFragmentManager
        val adapter = MyLibraryAdapter(cfManager)
        adapter.addFragment(
            ActiveProjectsFragment(), getString(
                R.string.active_projects
            )
        )
        adapter.addFragment(
            CompletedProjectsFragment(), getString(
                R.string.completed_projects
            )
        )
        adapter.addFragment(
            AllPatternsFragment(), getString(
                R.string.all_patterns
            )
        )
        view_pager.adapter = adapter
        tabLayout.setupWithViewPager(view_pager)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun handleEvent(event: MyLibraryViewModel.Event) =
        when (event) {
            is MyLibraryViewModel.Event.completedProjects -> {
                Toast.makeText(context, "adadjhf", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("MyLibraryViewModel", "MyLibraryViewModel.Event undefined")
            }

        }

}

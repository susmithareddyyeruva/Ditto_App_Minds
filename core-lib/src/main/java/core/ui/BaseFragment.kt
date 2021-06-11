package core.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Base class for Fragment(s) to be used across app. This enables registering fragments with Dagger (DI components).
 */
abstract class BaseFragment : DaggerFragment(), HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

    val bottomNavViewModel: BottomNavViewModel by ViewModelDelegate(ViewModelScope.ACTIVITY)

    val toolbarViewModel: ToolbarViewModel by ViewModelDelegate(ViewModelScope.ACTIVITY)
    val baseViewModel: BaseViewModel by ViewModelDelegate(ViewModelScope.ACTIVITY)
    //val tokenViewModel: TokenViewModel by ViewModelDelegate(ViewModelScope.ACTIVITY)

    override fun androidInjector(): AndroidInjector<Any> = childFragmentInjector

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        toolbarViewModel.toolbarTitle.set("")
        toolbarViewModel.visibility.set(false)
        toolbarViewModel.isShowSkip.set(false)
        toolbarViewModel.isShowMenu.set(false)
    }
}

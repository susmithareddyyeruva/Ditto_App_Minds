
package core.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * Delegate class to handle ViewModel initialization in fragment/activity
 */
class ViewModelDialogDelegate<out T : ViewModel> constructor(
    private val scope: ViewModelDialogScope = ViewModelDialogScope.FRAGMENT
) : ReadOnlyProperty<BaseDialogFragment, T> {

    private var viewModel: T? = null
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: BaseDialogFragment, property: KProperty<*>): T {
        if (viewModel == null) {
            val type: KType = property.getter.returnType
            val javaType = type.javaType
            val javaClass = javaType as? Class<T>
                ?: throw ClassCastException(
                    "Not a subclass of ViewModel"
                )
            viewModel = createViewModel(thisRef, javaClass)
        }
        return viewModel!!
    }

    private fun createViewModel(fragment: BaseDialogFragment, javaClass: Class<T>): T {
        val factory = fragment.viewModelFactory
        return when (scope) {
            ViewModelDialogScope.FRAGMENT -> ViewModelProviders
                .of(fragment, factory)
                .get(javaClass)

            ViewModelDialogScope.PARENT_FRAGMENT -> ViewModelProviders
                .of(fragment.requireParentFragment(), factory)
                .get(javaClass)

            ViewModelDialogScope.ACTIVITY -> ViewModelProviders
                .of(fragment.requireParentActivity(), factory)
                .get(javaClass)
        }
    }

    private fun Fragment.requireParentActivity(): FragmentActivity {
        val activity: FragmentActivity? = this.activity
        return activity ?: throw IllegalStateException(
            "${this.javaClass.simpleName} is not attached to activity but " +
                    "${ViewModelDialogScope.ACTIVITY} scope has been requested"
        )
    }
}

/**
 * A scope to be associated with [ViewModelDelegate]. Controls [ViewModel]'s lifetime.
 *
 * @see ViewModelDelegate
 */
enum class ViewModelDialogScope {
    /**
     * Limit [ViewModel]'s lifetime by its host fragment. Default behaviour.
     */
    FRAGMENT,

    /**
     * Limit [ViewModel]'s lifetime by parent of the current fragment. Make sure that
     * your fragment is attached to host fragment when requesting this scope.
     */
    PARENT_FRAGMENT,

    /**
     * Limit [ViewModel]'s lifetime by activity hosting current fragment. Make sure that
     * your fragment is attached to activity when requesting this scope.
     */
    ACTIVITY
}
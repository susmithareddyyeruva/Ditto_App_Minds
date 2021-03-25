package core.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

/**
 * Common binding adapter definitions
 */

object setRecyclerViewPropertiesBinder {
    @BindingAdapter("data")

    @JvmStatic
    fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T) {
        if (recyclerView.adapter!! is BindableAdapter<*>) {
            data?.let {
                @Suppress("UNCHECKED_CAST")
                (recyclerView.adapter as BindableAdapter<T>).setListData(data)
            }
        }
    }
}

object setViewPagerPropertiesBinder {
    @BindingAdapter("data")

    @JvmStatic
    fun <T> setViewPagerProperties(viewpager: ViewPager, data: T) {
        if (viewpager.adapter!! is BindableAdapter<*>) {
            data?.let {
                @Suppress("UNCHECKED_CAST")
                (viewpager.adapter as BindableAdapter<T>).setListData(data)
            }
        }
    }
}



object setVisibilityBinder {
    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibility(view: View, value: Boolean?) {
        view.visibility = if (value!!) View.VISIBLE else View.GONE
    }
}

object setImageResourceBinder {
    @BindingAdapter("imageResource")
    @JvmStatic
    fun setImageResource(imageView: ImageView, drawable: Drawable) {
        imageView.setImageDrawable(drawable)
    }
}

object onFocusChangeBinder {
    @BindingAdapter("onFocusChange")
    @JvmStatic
    fun onFocusChange(text: EditText, listener: View.OnFocusChangeListener) {
        text.onFocusChangeListener = listener
    }

    object bindErrorBinder {
        @BindingAdapter("error")
        @JvmStatic
        fun bindError(view: EditText, error: String?) {
            view.error = error
        }
    }
}

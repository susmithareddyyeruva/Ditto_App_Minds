package com.ditto.workspace.ui

import android.app.ActionBar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ditto.workspace.ui.databinding.PinchzoomActivityBinding
import com.ditto.workspace.ui.util.Utility
import kotlinx.android.synthetic.main.pinchzoom_activity.*

class PinchAndZoom : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: PinchzoomActivityBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.pinchzoom_activity
            )
        val imagepath = intent.extras?.getString("ImageURL")
        val isReference = intent.extras?.getBoolean("isReference") ?: false
        val patternName = intent.extras?.getString("patternName") ?: " "
        if (isReference) {
            binding.zoomTittle.text = getString(R.string.reference_layout)
        }
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
            val actionBar: ActionBar? = actionBar
            if (actionBar != null) {
                actionBar.hide()
            }
        }
        if (imagepath != null) {
            try {
                if (isReference) {
                    var availableUri: Uri? = null
                    availableUri =
                        core.ui.common.Utility.isImageFileAvailable(imagepath, patternName)
                    Log.d("imageUri123", " pinch and zoom availableUri: $availableUri >>>> ")
                    Glide
                        .with(this)
                        .load(availableUri)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(myZoomageView)
                } else if (!imagepath.contains(".png",true)){
                    val drawable = core.ui.common.Utility.getDrawableFromString(this, imagepath)
                    myZoomageView?.setImageDrawable(drawable)
                }else{
                    Glide.with(this)
                        .load(imagepath)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(myZoomageView)
                }
            } catch (e: Exception) {
                Log.d("Exception", "image path")
            }
        }
        binding.icCloseButton.setOnClickListener(View.OnClickListener {
            this.finish()
            overridePendingTransition(0, 0);
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.isPopupShowing.set(false)
    }
}
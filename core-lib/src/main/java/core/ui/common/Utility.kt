package core.ui.common


import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import core.lib.R
import core.network.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket
import kotlin.math.PI


/**
 * Helper Utility class
 */
class Utility {

    enum class AlertType {
        BLE,
        WIFI,
        CALIBRATION,
        QUICK_CHECK,
        DEFAULT,
        TAB_SWITCH,
        MIRROR,
        CUT_BIN,
        CUT_BIN_ALL,
        PATTERN_RENAME
    }

    companion object {
        val unityTransParmsString =
            "{\"projDist\":15.0,\"projMag\":1.0,\"projPos\":[0.0,0.0,45.0],\"projRot\":0,\"projxyAng\":0,\"projzAng\":$PI,\"unitVec\":[0,0,-1]}"

        fun getAlertDialogue(
            context: Context,
            title: String,
            message: String,
            negativeButton: String,
            positiveButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callbackDialogListener.onPositiveButtonClicked(alertType)
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callbackDialogListener.onNegativeButtonClicked(alertType)
                })

            val alert = dialogBuilder.create()
            alert.setTitle(title)
            alert.show()
        }

        fun getAlertDialogue(
            context: Context,
            title: String,
            message: String,
            negativeButton: String,
            positiveButton: String,
            neutralButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callbackDialogListener.onPositiveButtonClicked(alertType)
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callbackDialogListener.onNegativeButtonClicked(alertType)
                })

                .setNeutralButton(neutralButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callbackDialogListener.onNeutralButtonClicked()
                })

            val alert = dialogBuilder.create()
            alert.setTitle(title)
            alert.show()
        }

        fun showSnackBar(message: String, view: View) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }

        fun getBluetoothstatus(): Boolean {
            val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            val status: Boolean
            if (mBluetoothAdapter == null) {
                status = false
            } else status = mBluetoothAdapter.isEnabled()
            return status
        }

        fun getWifistatus(context: Context): Boolean {
            val status: Boolean
            val connManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi: NetworkInfo? = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            status = mWifi?.isConnected!!
            return status
        }


        fun getDrawableFromString(context: Context?, path: String?): Drawable? {
            val resID: Int? = context?.resources?.getIdentifier(
                path,
                context.getString(R.string.drawable_string),
                context?.getPackageName()
            )
            var drawable =
                context?.let { ContextCompat.getDrawable(it, resID ?: R.drawable.joann_logo) }
            drawable = (drawable?.let { DrawableCompat.wrap(it) })?.mutate()
            return drawable
        }

        fun getBitmap(
            vectorDrawable: VectorDrawable,
            mirrorVertical: Boolean,
            mirrorHorizontal: Boolean
        ): Bitmap? {
//            val resID: Int? = context?.resources?.getIdentifier(
//                path,
//                context.getString(R.string.drawable_string),
//                context?.getPackageName())
//
//            val options = BitmapFactory.Options().apply {
//                inJustDecodeBounds = true
//            }
//            val bitmap = BitmapFactory.decodeResource(context?.resources,
//                resID?: R.drawable.joann_logo,
//                options)
//            val bitmap = BitmapFactory.Options().run {
//                inJustDecodeBounds = true
//                BitmapFactory.decodeResource(context?.resources,  resID?: R.drawable.joann_logo, this)
//                inSampleSize = 1
//                // Decode bitmap with inSampleSize set
//                inJustDecodeBounds = false
//                BitmapFactory.decodeResource(context?.resources, resID?: R.drawable.joann_logo, this)
//            }
//            val canvas = Canvas(bitmap)
//            canvas.scale(
//                if (mirrorHorizontal) -1F
//                else
//                    1F,
//                if (mirrorVertical) -1F
//                else
//                    1F,
//                bitmap.width/2.toFloat(),
//                bitmap.height/2.toFloat()
//            )

            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(
                0, 0, vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight
            )
            canvas.scale(
                if (mirrorHorizontal) -1F
                else
                    1F,
                if (mirrorVertical) -1F
                else
                    1F,
                vectorDrawable.intrinsicWidth / 2.toFloat(),
                vectorDrawable.intrinsicHeight / 2.toFloat()
            )
            vectorDrawable.draw(canvas)
            return bitmap
        }

        fun scaleDownBitmap(
            realImage: Bitmap, maxImageSize: Float,
            filter: Boolean
        ): Bitmap {
            val ratio = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height
            )
            val width = Math.round(ratio * realImage.width)
            val height = Math.round(ratio * realImage.height)
            return Bitmap.createScaledBitmap(
                realImage, width,
                height, filter
            )
        }

        fun setSharedPref(context: Context, id: Int) {
            val sharedPreference =
                context.getSharedPreferences("PATTERN_DETAILS", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putInt("PATTERN_ID", id)
            editor.commit()
        }

        fun getSharedPref(context: Context): Int {
            val sharedPreference =
                context.getSharedPreferences("PATTERN_DETAILS", Context.MODE_PRIVATE)
            return sharedPreference.getInt("PATTERN_ID", 0)
        }

        fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs?.firstOrNull()?.let {
                File(it, "Trace").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.filesDir
        }

        fun galleryAddPic(context: Context, file: String) {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(file)
                mediaScanIntent.data = Uri.fromFile(f)
                context.sendBroadcast(mediaScanIntent)
            }
        }

        fun addBlackBackgroundToBitmap(bitmap: Bitmap): Bitmap {
            val transformedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(transformedBitmap)
            canvas.drawColor(Color.BLACK)
            canvas.drawBitmap(bitmap, Matrix(), null)
            return transformedBitmap
        }

        fun getBitmapFromDrawable(
            patternName: String,
            context: Context
        ): Bitmap {
            val uri = Uri.parse(
                "android.resource://" + context.packageName
                    .toString() + "/drawable/$patternName"
            )
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )
                ).copy(Bitmap.Config.ARGB_8888, true)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    .copy(Bitmap.Config.ARGB_8888, true)
            }
        }

        suspend fun sendDittoImage(context: Context, imageName: String) {
            val uri = Uri.parse(
                "android.resource://" + context.packageName
                    .toString() + "/drawable/$imageName"
            )
            val iStream = context.contentResolver.openInputStream(uri)
            withContext(Dispatchers.IO) {
                var soc: Socket? = null
                try {
                    soc = Socket(Utility.nsdSericeHostName, Utility.nsdSericePortName)
                    if (soc.isConnected) {
                        var dataOutputStream: DataOutputStream =
                            DataOutputStream(soc.getOutputStream())
                        val bytes = iStream?.let { getBytes(it) }
                        dataOutputStream.write(bytes)
                        println("*****bytes.length = " + bytes?.size)
                        dataOutputStream.close()

                    } else {
                        println("Socket Connection Failed")
                    }
                } catch (e: Exception) {
                    println("Socket Connection Failed")
                } finally {
                    soc?.close()
                }
            }
        }

        @Throws(IOException::class)
        fun getBytes(inputStream: InputStream): ByteArray {
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            return byteBuffer.toByteArray()
        }
    }

    interface CallbackDialogListener {
        fun onPositiveButtonClicked(alertType: AlertType)
        fun onNegativeButtonClicked(alertType: AlertType)
        fun onNeutralButtonClicked()
    }

}
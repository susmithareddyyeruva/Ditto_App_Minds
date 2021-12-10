package core.ui.common


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import core.lib.R
import core.models.Nsdservicedata
import core.network.NetworkUtility
import core.ui.TokenViewModel
import core.ui.VersionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.PI


/**
 * Helper Utility class
 */
class Utility @Inject constructor(
    private val tokenViewModel: TokenViewModel,
    private val versionViewModel: VersionViewModel
) {
    fun checkVersion() {
        versionViewModel.checkVersion()
    }

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
        PATTERN_RENAME,
        NETWORK,
        PDF,
        CUT_COMPLETE,
        CONNECTIVITY,
        SOC_CONNECT,
        DELETE,
        UPDATEAPIFAILED,
        DOWNLOADFAILED,
        RUNTIMEPERMISSION,
        SOFTWARE_UPDATE,
        GUEST_MYFOLDER
    }

    enum class Iconype {
        SUCCESS,
        FAILED,
        WARNING,
        NONE
    }

    companion object {

        var searchServieList : ArrayList<Nsdservicedata>? = null

        val unityTransParmsString =
            "{\"projDist\":15.0,\"projMag\":1.0,\"projPos\":[0.0,0.0,45.0],\"projRot\":0,\"projxyAng\":0,\"projzAng\":$PI,\"unitVec\":[0,0,-1]}"

        fun getAlertDialogue(
            context: Context?,
            title: String,
            message: String,
            negativeButton: String,
            positiveButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            if (context != null) {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(
                        positiveButton,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            callbackDialogListener.onPositiveButtonClicked(alertType)
                        })
                    .setNegativeButton(
                        negativeButton,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            callbackDialogListener.onNegativeButtonClicked(alertType)
                        })

                val alert = dialogBuilder.create()
                alert.setTitle(title)
                alert.show()
            }
        }
        fun getAlertDialogue(
            context: Context?,
            title: String,
            message: String,
            negativeButton: String,
            positiveButton: String,
            neutralButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            if (context != null) {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(
                        positiveButton,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            callbackDialogListener.onPositiveButtonClicked(alertType)
                        })
                    .setNegativeButton(
                        negativeButton,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            callbackDialogListener.onNegativeButtonClicked(alertType)
                        })

                    .setNeutralButton(neutralButton, DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                        callbackDialogListener.onNeutralButtonClicked(alertType)
                    })

                val alert = dialogBuilder.create()
                alert.setTitle(title)
                alert.show()
            }
        }
        /*   fun getAlertDialogueForCaliberate(
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
           }*/
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
                context?.let { ContextCompat.getDrawable(it, resID ?: R.drawable.ic_placeholder) }
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

        fun createMirrorBitmap(
            source: Bitmap,
            mirrorVertical: Boolean,
            mirrorHorizontal: Boolean
        ): Bitmap? {
            val matrix = Matrix()
            matrix.postScale(
                if (mirrorHorizontal) -1F else 1F,
                if (mirrorVertical) -1F else 1F,
                source.width / 2f,
                source.height / 2f
            )
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        fun getRotation(
            bitmap: Bitmap,
            mirrorVertical: Boolean,
            mirrorHorizontal: Boolean
        ): Bitmap? {
            val canvas = Canvas(bitmap)
            canvas.scale(
                if (mirrorHorizontal) -1F
                else
                    1F,
                if (mirrorVertical) -1F
                else
                    1F,
                bitmap.width / 2.toFloat(),
                bitmap.height / 2.toFloat()
            )
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

        fun setSharedPref(context: Context, id: String) {
            val sharedPreference =
                context.getSharedPreferences("PATTERN_DETAILS", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("PATTERN_ID", id)
            editor.commit()
        }

        fun getSharedPref(context: Context): String? {
            val sharedPreference =
                context.getSharedPreferences("PATTERN_DETAILS", Context.MODE_PRIVATE)
            return sharedPreference.getString("PATTERN_ID", "")
        }

        fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs?.firstOrNull()?.let {
                File(it, "Trace").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.filesDir
        }

        fun galleryAddPic(context: Context?, file: String) {
            if (context != null) {
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    val f = File(file)
                    mediaScanIntent.data = Uri.fromFile(f)
                    context.sendBroadcast(mediaScanIntent)
                }
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

        suspend fun sendDittoImage(context: Context?, imageName: String) {
            if (context == null) return
            Log.d("TRACE_ Projection :", "Send Ditto start " + Calendar.getInstance().timeInMillis)
            val uri = Uri.parse(
                "android.resource://" + context.packageName
                    .toString() + "/drawable/$imageName"
            )
            val iStream = context.contentResolver.openInputStream(uri)
            withContext(Dispatchers.IO) {
                var soc: Socket? = null
                try {
                    soc = Socket(NetworkUtility.nsdSericeHostName, NetworkUtility.nsdSericePortName)
                    if (soc.isConnected) {
                        var dataOutputStream: DataOutputStream =
                            DataOutputStream(soc.getOutputStream())
                        val bytes = iStream?.let { getBytes(it) }
                        dataOutputStream.write(bytes)
                        println("*****bytes.length = " + bytes?.size)
                        dataOutputStream.close()
                    } else {
                        println("Projector Connection failed")
                        /*withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Socket Connection failed. Try again!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }*/
                    }
                } catch (e: Exception) {
                    println("Projector Connection failed")
                    /* withContext(Dispatchers.Main) {
                         Toast.makeText(
                             context,
                             "Socket Connection failed. Try again!!",
                             Toast.LENGTH_SHORT
                         ).show()
                     }*/
                } finally {
                    soc?.close()
                    Log.d(
                        "TRACE_ Projection :",
                        "Send Ditto Finish " + Calendar.getInstance().timeInMillis
                    )
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

        fun redirectToExternalBrowser(context: Context?, url: String) {
            if (context == null) return
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun isFileAvailable(filename: String, context: Context, patternFolderName: String?): Uri? {


            val directory = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/Ditto"
            )


           /* val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir("DittoPattern", Context.MODE_PRIVATE)
            var p = patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")*/
            Log.d("Utility","${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")+".pdf"}")
//            val pdfFile = File(directory, "${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")+".pdf"}")
            val pdfFile = File(directory, "${patternFolderName.toString()}.pdf")

            var path: Uri? = null
            if (pdfFile.exists()) {
                path = Uri.fromFile(pdfFile)
            } else {
                path = null
            }

            return path
        }

        fun isImageFileAvailable(
            filename: String?,
            patternFolderName: String?,
            context: Context
        ): Uri? {

            /*val directory = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/Ditto/$patternFolderName"
            )*/

            val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir("Ditto", Context.MODE_PRIVATE)
            var p = patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")
            )
            Log.d("DOWNLOAD UTIL", "received is $filename")
           /* val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir("DittoPattern", Context.MODE_PRIVATE)
            var p = patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")*/
            //Log.d("Utility","${patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), "")+".svg"}")
            val patternfolder = File(directory,patternFolderName.toString().replace("[^A-Za-z0-9 ]".toRegex(), ""))
            var path: Uri? = null
            if (patternfolder.exists()) {
                val file = File(patternfolder, filename)
                if (file.exists()) {
                    path = Uri.fromFile(file)
                } else {
                    path = null
                }
            } else {
                path = null
            }
            return path
        }


        @SuppressLint("ResourceType")
        fun getCommonAlertDialogue(
            context: Context?,
            title: String,
            alertmessage: String,
            negativeButton: String,
            positiveButton: String,
            customcallbackDialogListener: CustomCallbackDialogListener,
            alertType: AlertType,
            imgtyp: Iconype
        ) {
            if (context != null) {
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null)
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setView(mDialogView)
                val alert = dialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
                alert.window?.setBackgroundDrawable(null)
                val lay_withimage =
                    mDialogView.findViewById(R.id.layout_withImage) as RelativeLayout
                val lay_withoutimage =
                    mDialogView.findViewById(R.id.layout_withoutImage) as RelativeLayout
                if (alertType == AlertType.BLE || alertType == AlertType.WIFI || alertType == AlertType.CUT_COMPLETE
                    || alertType == AlertType.SOC_CONNECT || alertType == AlertType.MIRROR || alertType == AlertType.CUT_BIN || alertType == AlertType.DELETE
                ) {
                    lay_withimage.visibility = View.GONE
                    lay_withoutimage.visibility = View.VISIBLE

                    val title_common = mDialogView.findViewById(R.id.common_title) as TextView
                    val message_common = mDialogView.findViewById(R.id.common_message) as TextView
                    val neg_text_common = mDialogView.findViewById(R.id.neg_text_common) as TextView
                    val pos_text_common = mDialogView.findViewById(R.id.pos_txt_common) as TextView
                    if (alertType == AlertType.CUT_COMPLETE) {
                        title_common.text = alertmessage
                        title_common.typeface =
                            ResourcesCompat.getFont(context, R.font.avenir_next_lt_pro_regular)
                        message_common.visibility = View.GONE
                    } else {
                        title_common.text = title
                        message_common.text = HtmlCompat.fromHtml(
                            alertmessage,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }

                    if (alertType == AlertType.CUT_BIN) {
                        message_common.gravity = Gravity.START
                    }
                    if (alertType == AlertType.MIRROR) {
                        message_common.gravity = Gravity.START
                    }

                    neg_text_common.text = negativeButton
                    pos_text_common.text = positiveButton

                    neg_text_common.setOnClickListener {
                        alert.dismiss()
                        customcallbackDialogListener.onCustomNegativeButtonClicked(
                            imgtyp,
                            alertType
                        )
                    }
                    pos_text_common.setOnClickListener {
                        alert.dismiss()
                        customcallbackDialogListener.onCustomPositiveButtonClicked(
                            imgtyp,
                            alertType
                        )
                    }

                } else {
                    lay_withimage.visibility = View.VISIBLE
                    lay_withoutimage.visibility = View.GONE

                    val message = mDialogView.findViewById(R.id.alert_message) as TextView
                    message.text = alertmessage
                    val negative = mDialogView.findViewById(R.id.neg_text) as TextView
                    negative.text = negativeButton
                    val positive = mDialogView.findViewById(R.id.pos_txt) as TextView
                    positive.text = positiveButton
                    val icon = mDialogView.findViewById(R.id.img_icon) as ImageView
                    if (imgtyp.equals(Iconype.SUCCESS)) {
                        icon.setImageDrawable(context.getDrawable(R.drawable.ic_success))
                    } else if (imgtyp.equals(Iconype.FAILED)) {
                        icon.setImageDrawable(context.getDrawable(R.drawable.ic_failed))
                    } else if (imgtyp.equals(Iconype.WARNING)) {
                        icon.setImageDrawable(context.getDrawable(R.drawable.ic_warning))
                    } else {
                        icon.setImageDrawable(context.getDrawable(R.drawable.ic_failed))
                    }
                    negative.setOnClickListener {
                        alert.dismiss()
                        customcallbackDialogListener.onCustomNegativeButtonClicked(
                            imgtyp,
                            alertType
                        )
                    }
                    positive.setOnClickListener {
                        alert.dismiss()
                        customcallbackDialogListener.onCustomPositiveButtonClicked(
                            imgtyp,
                            alertType
                        )
                    }
                }
            }
        }

        @SuppressLint("ResourceType")
        fun showAlertDialogue(
            context: Context?,
            resourceDrawable: Int,
            alertmessage: String,
            negativeButton: String,
            positiveButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            if (context != null) {
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.custom_alert_calibration, null)
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setView(mDialogView)
                val alert = dialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
                alert.window?.setBackgroundDrawable(null)
                val image = mDialogView.findViewById(R.id.img_icon) as ImageView
                image.setImageResource(resourceDrawable)
                val message = mDialogView.findViewById(R.id.alert_message) as TextView
                message.text = alertmessage
                val negative = mDialogView.findViewById(R.id.neg_text) as TextView
                negative.text = negativeButton
                val positive = mDialogView.findViewById(R.id.pos_txt) as TextView
                positive.text = positiveButton
                negative.setOnClickListener {
                    alert.dismiss()
                    callbackDialogListener.onNegativeButtonClicked(alertType)
                }
                positive.setOnClickListener {
                    alert.dismiss()
                    callbackDialogListener.onPositiveButtonClicked(alertType)
                }
            }
        }

        @SuppressLint("ResourceType")
        fun showAlertDialogue(
            context: Context?,
            resourceDrawable: Int,
            alertmessage: String,
            nutralButton: String,
            negativeButton: String,
            positiveButton: String,
            callbackDialogListener: CallbackDialogListener,
            alertType: AlertType
        ) {
            if (context != null) {
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.custom_alert_calibration, null)
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setView(mDialogView)
                val alert = dialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
                alert.window?.setBackgroundDrawable(null)
                val image = mDialogView.findViewById(R.id.img_icon) as ImageView
                image.setImageResource(resourceDrawable)
                val message = mDialogView.findViewById(R.id.alert_message) as TextView
                message.text = alertmessage
                val nutral = mDialogView.findViewById(R.id.nutral_text) as TextView
                nutral.text = nutralButton
                nutral.visibility = View.VISIBLE
                val negative = mDialogView.findViewById(R.id.neg_text) as TextView
                negative.text = negativeButton
                val positive = mDialogView.findViewById(R.id.pos_txt) as TextView
                positive.text = positiveButton
                nutral.setOnClickListener {
                    alert.dismiss()
                    callbackDialogListener.onNeutralButtonClicked(alertType)
                }
                negative.setOnClickListener {
                    alert.dismiss()
                    callbackDialogListener.onNegativeButtonClicked(alertType)
                }
                positive.setOnClickListener {
                    alert.dismiss()
                    callbackDialogListener.onPositiveButtonClicked(alertType)
                }
            }
        }

        fun getTotalNumberOfDays(endDate: String?): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = Date()
            println(formatter.format(date))
            val filterData = endDate!!.split(" ")[0]
            val date2 = SimpleDateFormat("yyyy-MM-dd").parse(filterData)
            val longValue=printDifference(date,date2)
             return longValue.toString()
        }

        fun printDifference(startDate: Date, endDate: Date): Long {
            //milliseconds
            var different = endDate.time - startDate.time
            println("startDate : $startDate")
            println("endDate : $endDate")
            println("different : $different")
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val elapsedDays = different / daysInMilli
            different %= daysInMilli
            val elapsedHours = different / hoursInMilli
            different %= hoursInMilli
            val elapsedMinutes = different / minutesInMilli
            different %= minutesInMilli
            val elapsedSeconds = different / secondsInMilli
            println("different : $elapsedDays")
            return elapsedDays
        }

    }

    interface CallbackDialogListener {
        fun onPositiveButtonClicked(alertType: AlertType)
        fun onNegativeButtonClicked(alertType: AlertType)
        fun onNeutralButtonClicked(alertType: AlertType)
    }

    interface CustomCallbackDialogListener {
        fun onCustomPositiveButtonClicked(iconype: Iconype, alertType: AlertType)
        fun onCustomNegativeButtonClicked(iconype: Iconype, alertType: AlertType)
    }

}
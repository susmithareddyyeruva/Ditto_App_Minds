package core.di

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Encrypt {
    fun HMAC_SHA256(secret: String, message: String): String? {
        var hash = ""
        try {
            val sha256_HMAC = Mac.getInstance("HmacSHA256")
            val secret_key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
            sha256_HMAC.init(secret_key)
            hash = Base64.encodeToString(sha256_HMAC.doFinal(message.toByteArray()), Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
        }
        return hash.trim { it <= ' ' }
    }
}
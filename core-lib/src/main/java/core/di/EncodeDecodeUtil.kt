package core.di

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object EncodeDecodeUtil {
    /**
     * Encryption of a given text using the provided secretKey
     *
     * @param text
     * @param secretKey
     * @return the encoded string
     * @throws SignatureException
     */
    fun HMAC_SHA256(secret: String?, message: String): String? {
        var hash = ""
        try {
            val sha256Hmac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(secret?.toByteArray(), "HmacSHA256")
            sha256Hmac.init(secretKey)
            hash = Base64.encodeToString(sha256Hmac.doFinal(message.toByteArray()), Base64.DEFAULT)
            Log.d("=========", "BASE 64 :" + hash)
            Log.d(
                "=========", java.lang.String.format(
                    "Hex: %032x", BigInteger(
                        1, sha256Hmac.doFinal(
                            message.toByteArray()
                        )
                    )
                )
            )
        /*    hash1= String(sha256_HMAC.doFinal(message.toByteArray()))
            val hash3=String(sha256_HMAC.doFinal(message.toByteArray()), StandardCharsets.UTF_8)
            println(hash)        // Kotlin
            println(hash1)        // Kotlin
            println(hash3)        // Kotlin*/
        } catch (e: java.lang.Exception) {
        }
        return hash.trim { it <= ' ' }
    }
    // decode data from base 64
    fun decodeBase64(coded: String?): String? {
        var valueDecoded: ByteArray? = ByteArray(0)
        try {
            valueDecoded = Base64.decode(coded?.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
        }
        return String(valueDecoded!!)
    }

}
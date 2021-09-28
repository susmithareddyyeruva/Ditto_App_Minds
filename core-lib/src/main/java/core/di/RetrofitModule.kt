package core.di

import android.content.Context
import core.MOCK_API_CERT
import core.OCAPI_PASSWORD
import core.OCAPI_USERNAME
import core.TRACKING_ID
import core.appstate.AppState
import core.di.scope.WbApiRetrofit
import core.di.scope.WbBaseUrl
import core.di.scope.WbTokenApiRetrofit
import core.di.scope.WbTokenBaseUrl
import core.lib.BuildConfig
import core.network.RxCallAdapterWrapperFactory
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton
import javax.net.ssl.*


@Module(
    includes = [
        WbBaseUrlModule::class,
        WbTokenBaseUrlModule::class/*,
        WbSocketCertificateModule::class*/
    ]
)
class RetrofitModule {
    @Provides
    @WbApiRetrofit
    fun provideRetrofit(
        @WbBaseUrl baseUrl: String
    ): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)



        httpClient.hostnameVerifier(HostnameVerifier { hostname, session -> //return true;
            val hv: HostnameVerifier =
                HttpsURLConnection.getDefaultHostnameVerifier()
            hv.verify("demandware.net", session)
        })


        // add logging interceptor only for DEBUG builds
        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxCallAdapterWrapperFactory.createAsync())
            .client(httpClient.build())
            .build()
    }
    @Provides
    @WbTokenApiRetrofit
    fun provideTokenRetrofit(
        @WbTokenBaseUrl baseUrl: String
    ): Retrofit {
        val logging = HttpLoggingInterceptor()
        val head_auth = BasicAuthInterceptor(OCAPI_USERNAME, OCAPI_PASSWORD)
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HmacSignatureInterceptor())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        httpClient.hostnameVerifier(HostnameVerifier { hostname, session -> //return true;
            val hv: HostnameVerifier =
                HttpsURLConnection.getDefaultHostnameVerifier()
            hv.verify("handmadewithjoann.com", session)
        })
        // add logging interceptor only for DEBUG builds
        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxCallAdapterWrapperFactory.createAsync())
            .client(httpClient.build())
            .build()


    }
}

@Module
class WbBaseUrlModule {
    @Provides
    @WbBaseUrl
    fun providesBaseUrl(): String {
        return BuildConfig.BASEURL
    }
}

@Module
class WbTokenBaseUrlModule {
    @Provides
    @WbTokenBaseUrl
    fun providesTokenBaseUrl(): String {
        return BuildConfig.TOKEN_BASEURL
    }
}

@Module
class WbSocketCertificateModule {
    @Provides
    fun providesSSLSocketFactory(trustManagerFactory: TrustManagerFactory?): SSLSocketFactory? {

        var sslSocketFactory: SSLSocketFactory? = null

        if (trustManagerFactory != null) {

            // Create an SSLContext that uses our TrustManager
            val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            sslSocketFactory = sslContext.socketFactory

        }

        return sslSocketFactory
    }

    @Provides
    @Singleton
    fun provideTrustManagerFactory(context: Context): TrustManagerFactory? {

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")

        // Get the input stream which will be pointed to certificate file in assets.
        var caInput: InputStream? = null
        try {
            caInput = context.assets.open(MOCK_API_CERT)
        } catch (fnfe: FileNotFoundException) {
            fnfe.printStackTrace()
        }
        var tmf: TrustManagerFactory? = null

        if (caInput != null) {
            val ca: X509Certificate = caInput.use {
                cf.generateCertificate(it) as X509Certificate
            }

            // Create a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

            // Create a TrustManager that trusts the CAs inputStream our KeyStore
            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }
        }
        return tmf
    }

}

class BasicAuthInterceptor(user: String?, password: String?) :
    Interceptor {
    private val credentials: String

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

    init {
        credentials = Credentials.basic(user!!, password!!)
    }
}
class HmacSignatureInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessKeyId =
            java.lang.String.format("ANDROID%s", AppState.getAppVersion())
        val timestamp = "" + System.currentTimeMillis() / 1000
        val signature = generateSignature(chain.request(), accessKeyId, timestamp)
        val userAgent = java.lang.String.format(
            "JOANN/%s %s",
            AppState.getAppVersion(),
            System.getProperty("http.agent")
        )
        var request = chain.request()
        val httpUrl = request.url.newBuilder()
            .addQueryParameter("AccessKeyId", accessKeyId)
            .addQueryParameter("Timestamp", timestamp)
            .addQueryParameter("Signature", signature)
            .build()
        request = request.newBuilder().url(httpUrl)
            .addHeader("User-Agent", userAgent)
            .build()
        return chain.proceed(request)
    }

    private fun generateSignature(
        request: Request,
        accessKeyId: String,
        timestamp: String
    ): String {
        val emptyString = ""
        val projectName = "DittoPatterns"
        val method = request.method
        val domain = request.url.host
        val path = request.url.encodedPath
        val paramsUrl = request.url.newBuilder()
            .addQueryParameter("AccessKeyId", accessKeyId)
            .addQueryParameter("Timestamp", timestamp)
            .build()
        val params = paramsUrl.encodedQuery
        val stringToHash =
            """
            $emptyString
            $projectName
            $method
            $domain
            $path
            $params
            """.trimIndent()
        //Log.d("generateSignature", stringToHash);
        val trackingId: String = TRACKING_ID
        var sha256_HMAC: Mac? = null
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        val secretKey =
            SecretKeySpec(trackingId.toByteArray(), "HmacSHA256")
        try {
            sha256_HMAC?.init(secretKey)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        return Hex.encodeHex(
            if (sha256_HMAC != null) sha256_HMAC.doFinal(stringToHash.toByteArray()) else ByteArray(
                0
            )
        ).toLowerCase()
    }
}

package core.di

import android.content.Context
import android.util.Log
import core.MOCK_API_CERT
import core.TAILORNOVA_API_KEY
import core.TAILORNOVA_API_KEY_VALUE
import core.TRACKING_ID
import core.appstate.AppState
import core.data.model.TokenResult
import core.di.scope.*
import core.lib.BuildConfig
import core.network.RxCallAdapterWrapperFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking
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
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton
import javax.net.ssl.*


@Module(
    includes = [
        WbBaseUrlModule::class,
        WbTokenBaseUrlModule::class,
        WbTailornovaBaseUrlModule::class]
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
            .authenticator(TokenAuthenticator())
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
        //val head_auth = BasicAuthInterceptor(OCAPI_USERNAME, OCAPI_PASSWORD)
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

    @Provides
    @WbTailornovaApiRetrofit
    fun provideTailornovaRetrofit(
        @WbTailornovaBaseUrl baseUrl: String
    ): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val head_auth = AuthInterceptor(TAILORNOVA_API_KEY, TAILORNOVA_API_KEY_VALUE)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(head_auth)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxCallAdapterWrapperFactory.createAsync())
            .client(httpClient.build())
            .build()
    }
    class AuthInterceptor(tailornovaApiKey: String, tailornovaApiKeyValue: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val authRequest: Request = request.newBuilder()
                .addHeader(TAILORNOVA_API_KEY, TAILORNOVA_API_KEY_VALUE)
                .method(request.method, request.body)
                .build()
            return chain.proceed(authRequest)
        }
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
class WbTailornovaBaseUrlModule {
    @Provides
    @WbTailornovaBaseUrl
    fun providesTailornovaBaseUrl(): String {
        return BuildConfig.TAILORNOVA_ENDURL
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

class HmacSignatureInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessKeyId =
            java.lang.String.format("ANDROID%s", BuildConfig.VERSION_NAME)
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
        val response = chain.proceed(request)
        return response
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

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {

            // 1. Refresh your access_token using a synchronous api request
            val responseMain = getUpdatedToken()


            val expCal = Calendar.getInstance()
            expCal.add(Calendar.MINUTE, responseMain.response?.expires_in ?: 0)
            val expirytime = expCal.time.time
            val token = responseMain?.response?.access_token ?: ""
            Log.d("TOKEN==", token)
            token.let {
                AppState.saveToken(
                    it,
                    expirytime
                )
            }

            response.request.newBuilder()
                .header("Authorization", "Bearer ${responseMain.response?.access_token}")
                .build()


        }
    }

    private suspend fun getUpdatedToken(): TokenResult {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HmacSignatureInterceptor())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.TOKEN_BASEURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(ApiService::class.java)
        return service.refreshTokenAuthentication()

    }

}




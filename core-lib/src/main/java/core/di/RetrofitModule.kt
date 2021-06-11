package core.di

import android.content.Context
import core.*
import core.BASE_URL
import core.MOCK_API_CERT
import core.TOKEN_BASE_URL
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
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import kotlin.jvm.Throws


@Module(
    includes = [
        WbBaseUrlModule::class,
        WbSocketCertificateModule::class,
        WbTokenBaseUrlModule :: class
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
            .addInterceptor(head_auth)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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
        return BASE_URL
    }
}

@Module
class WbTokenBaseUrlModule {
    @Provides
    @WbTokenBaseUrl
    fun providesTokenBaseUrl(): String {
        return TOKEN_BASE_URL
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

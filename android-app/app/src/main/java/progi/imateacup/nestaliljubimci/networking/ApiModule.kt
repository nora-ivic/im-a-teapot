package progi.imateacup.nestaliljubimci.networking

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiModule {
    private const val BASE_URL = "https://localhost:3000/"

    lateinit var retrofit: PetsApiService

    private val json = Json { ignoreUnknownKeys = true }

    private  var accessToken: String = ""

    fun setSessionInfo (accessToken: String){
        this.accessToken = accessToken
    }
    fun initRetrofit(context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()

                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", accessToken)
                    .build()

                chain.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(PetsApiService::class.java)
    }
}
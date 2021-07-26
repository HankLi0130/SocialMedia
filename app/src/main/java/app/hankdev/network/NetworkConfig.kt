package app.hankdev.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://asia-east2-iamchief-dev.cloudfunctions.net"

fun getHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

fun getOkHttpClient() = OkHttpClient.Builder()
    .addInterceptor(getHttpLoggingInterceptor())
    .build()

fun getMoshiConverterFactory() = MoshiConverterFactory.create()

fun getRetrofit() = Retrofit.Builder()
    .client(getOkHttpClient())
    .baseUrl(BASE_URL)
    .addConverterFactory(getMoshiConverterFactory())
    .build()
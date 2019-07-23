package com.wildan.moviecatalogue.network

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {

    companion object {
        private var retrofit: Retrofit? = null
        fun getClient(context: Context): Retrofit? {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val clientTest = OkHttpClient.Builder()
                .addInterceptor(ChuckInterceptor(context))
                .addInterceptor(logging)
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(clientTest)
                    .build()
            }
            return retrofit
        }
    }
}
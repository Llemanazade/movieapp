package com.example.movieapplication.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.movieapplication.data.MediaRepository
import com.example.movieapplication.data.network.helper.ApiService
import com.example.movieapplication.utility.Constants.BASE_URL
import com.example.movieapplication.utility.InternetConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideNetworkStatusChecker(
        @ApplicationContext context: Context
    ): InternetConnectivityObserver.NetworkStatusChecker =
        InternetConnectivityObserver.NetworkStatusChecker(context.getSystemService(
            ConnectivityManager::class.java))

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
       Retrofit.Builder().baseUrl(BASE_URL).
       addConverterFactory(GsonConverterFactory
           .create())
           .client(client)
            .build()


    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: ApiService) = MediaRepository(apiService)

}

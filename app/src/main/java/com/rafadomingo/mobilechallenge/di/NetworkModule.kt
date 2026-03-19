package com.rafadomingo.mobilechallenge.di

import android.content.Context
import coil.ImageLoader
import com.rafadomingo.mobilechallenge.BuildConfig
import com.rafadomingo.mobilechallenge.data.remote.DiscogsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                // Replace with your actual Discogs token or key/secret
                // Format: "Discogs token=YOUR_TOKEN" or "Discogs key=KEY, secret=SECRET"
                .addHeader(
                    "Authorization",
                    "Discogs token=${BuildConfig.DISCOGS_TOKEN}"
                )
                .addHeader("User-Agent", "MobileChallengeApp/1.0")
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideDiscogsApi(okHttpClient: OkHttpClient): DiscogsApi {
        return Retrofit.Builder()
            .baseUrl(DiscogsApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(DiscogsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context).okHttpClient(okHttpClient)
            .build()
    }
}

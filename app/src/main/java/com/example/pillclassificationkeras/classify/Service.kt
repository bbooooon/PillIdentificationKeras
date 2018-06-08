package com.example.pillclassificationkeras.classify

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

/**
 * Created by Bajarng on 22/1/2561.
 */
interface Service{
    @Multipart
    @POST("run")
    fun postImage(@Part file: MultipartBody.Part): Call<Prediction>
}
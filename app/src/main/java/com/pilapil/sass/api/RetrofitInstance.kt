package com.pilapil.sass.api

object RetrofitInstance {
    val apiService: ApiService by lazy {
        ApiService.create()
    }
}

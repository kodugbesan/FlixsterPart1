package com.example.flixsterpart1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.themoviedb.org/3/"

// Define the API key
private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"

// Movie API Service
interface ApiService {
    @GET("movie/now_playing?api_key=$API_KEY")
    suspend fun getNowPlayingMovies(): MovieResponse
}

// Movie Response (from the API)
data class MovieResponse(
    val results: List<Movie>
)

// Retrofit instance
object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

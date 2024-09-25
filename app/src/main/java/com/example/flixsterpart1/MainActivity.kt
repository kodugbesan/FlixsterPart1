package com.example.flixsterpart1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.flixsterpart1.ui.theme.FlixsterPart1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlixsterPart1Theme {
                // Fetch movies and display the list
                val movieList = remember { mutableStateListOf<Movie>() }

                // Fetch the movie data from the API
                LaunchedEffect(Unit) {
                    fetchMovies { movies ->
                        movieList.clear()
                        movieList.addAll(movies)
                    }
                }

                // Display the movie list
                MovieList(movies = movieList)
            }
        }
    }

    private fun fetchMovies(onMoviesFetched: (List<Movie>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the network request to the MovieDB API
                val apiUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
                val response = URL(apiUrl).readText()

                // Parse the response
                val jsonResponse = JSONObject(response)
                val moviesArray = jsonResponse.getJSONArray("results")
                val movieList = mutableListOf<Movie>()

                for (i in 0 until moviesArray.length()) {
                    val movieJson = moviesArray.getJSONObject(i)
                    val title = movieJson.getString("title")
                    val description = movieJson.getString("overview")
                    val posterPath = movieJson.getString("poster_path")
                    movieList.add(Movie(title, description, posterPath))
                }

                // Return to the main thread to update UI
                withContext(Dispatchers.Main) {
                    onMoviesFetched(movieList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Column(modifier = Modifier.padding(8.dp)) {
        val posterUrl = "https://image.tmdb.org/t/p/w500/${movie.posterPath}"
        Image(
            painter = rememberAsyncImagePainter(posterUrl),
            contentDescription = movie.title,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = movie.title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = movie.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MovieList(movies: List<Movie>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            MovieItem(movie)
        }
    }
}
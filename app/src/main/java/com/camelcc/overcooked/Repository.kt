package com.camelcc.overcooked

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import com.squareup.moshi.Json
import kotlinx.coroutines.flow.Flow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
{
"id": 2014422,
"width": 3024,
"height": 3024,
"url": "https://www.pexels.com/photo/brown-rocks-during-golden-hour-2014422/",
"photographer": "Joey Farina",
"photographer_url": "https://www.pexels.com/@joey",
"photographer_id": 680589,
"avg_color": "#978E82",
"src": {
"original": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
"large2x": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",
"large": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=650&w=940",
"medium": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=350",
"small": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&h=130",
"portrait": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&fit=crop&h=1200&w=800",
"landscape": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&fit=crop&h=627&w=1200",
"tiny": "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280"
},
"liked": false,
"alt": "Brown Rocks During Golden Hour"
}
 */
data class PhotoSrc(val original: String, val large2x: String)

data class Photo(val id: Long,
                 val width: Int,
                 val height: Int,
                 val title: String = "",
                 val url: String,
                 val src: PhotoSrc,
                 val enabled: Boolean = true,
                 val counter: Int = 0)

fun Photo.toPhotoEntity(): PhotoEntity =
    PhotoEntity(this.id, this.title ?: "", this.width, this.height, this.url, this.src.original, this.src.large2x, this.enabled)

data class NetworkPhoto(val id: Long,
                 val width: Int,
                 val height: Int,
                 @Json(name = "alt") val title: String,
                 val url: String,
                 val src: PhotoSrc)

fun NetworkPhoto.toPhoto(): Photo =
    Photo(id, width, height, title ?: "", url, src, enabled = true, counter = 0)

data class Curated(val page: Int,
                   @Json(name = "per_page") val perPage: Int,
                   val photos: List<NetworkPhoto>,
                   @Json(name = "prev_page") val prevPage: String?,
                   @Json(name = "next_page") val nextPage: String?)

data class Searched(val page: Int,
                    @Json(name = "per_page") val perPage: Int,
                    val photos: List<NetworkPhoto>,
                    @Json(name = "prev_page") val prevPage: String?,
                    @Json(name = "next_page") val nextPage: String?)

interface ApiInterface {
    @GET("curated")
    suspend fun curated(@Query("page") page: Int = 1): Response<Curated>

    @GET("search")
    suspend fun search(@Query("query") query: String): Response<Searched>
}

class AuthenticationInterceptor: Interceptor {
    private val apiKey = "<API-KEY>"

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val request = original.newBuilder().addHeader("Authorization", apiKey).build()
        return chain.proceed(request)
    }
}

class Repository(appContext: Context) {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthenticationInterceptor())
        .build()
    private val apiClient: ApiInterface
    val db: DB

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pexels.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        apiClient = retrofit.create(ApiInterface::class.java)

        db = Room.databaseBuilder(appContext, DB::class.java, "DB").build()
    }

    fun photos(): Flow<List<PhotoEntity>> = db.photoDao().getAllPhotos()

    @WorkerThread
    suspend fun refreshPhotos(): Response<Curated> {
        val curated = apiClient.curated()
        if (curated.isSuccessful) {
            val photos = curated.body()?.photos?.map {
                it.toPhoto().toPhotoEntity()
            }?.toTypedArray()
            photos?.also {
                db.photoDao().insertAll(*it)
            }
        }
        return curated
    }

    @WorkerThread
    suspend fun searchPhotos(search: String): Response<Searched> {
        return apiClient.search(query = search)
    }
}
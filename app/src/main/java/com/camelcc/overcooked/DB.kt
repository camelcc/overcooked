package com.camelcc.overcooked

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import kotlinx.coroutines.flow.Flow

@Entity
data class PhotoEntity(@PrimaryKey val id: Long,
                       val title: String, val width: Int, val height: Int, val url: String,
                       val original: String, val large2x: String,
                       val enabled: Boolean)

fun PhotoEntity.toPhoto(): Photo =
    Photo(this.id, this.width, this.height, this.title, this.url, PhotoSrc(this.original, this.large2x), this.enabled, 0)

@Dao
interface PhotoDAO {
    @Query("SELECT * FROM photoentity ORDER by id")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = IGNORE)
    fun insertAll(vararg photos: PhotoEntity)
}

@Database(entities = [PhotoEntity::class], version = 2)
abstract class DB: RoomDatabase() {
    abstract fun photoDao(): PhotoDAO
}
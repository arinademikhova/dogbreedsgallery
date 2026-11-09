
package com.example.dogbreedsgallery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dogbreedsgallery.data.local.dao.BreedDao
import com.example.dogbreedsgallery.data.local.entity.BreedEntity

@Database(
    entities = [BreedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun breedDao(): BreedDao
}

package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.LandRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface LandRecordDao {
    @Query("SELECT * FROM land_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<LandRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LandRecord): Long

    @Delete
    suspend fun deleteRecord(record: LandRecord)

    @Query("DELETE FROM land_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM land_records")
    suspend fun clearAll()
}

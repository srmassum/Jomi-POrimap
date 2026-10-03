package com.example.data

import com.example.model.LandRecord
import kotlinx.coroutines.flow.Flow

class LandRepository(private val dao: LandRecordDao) {
    val allRecords: Flow<List<LandRecord>> = dao.getAllRecords()

    suspend fun insert(record: LandRecord): Long = dao.insertRecord(record)

    suspend fun delete(record: LandRecord) = dao.deleteRecord(record)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()
}

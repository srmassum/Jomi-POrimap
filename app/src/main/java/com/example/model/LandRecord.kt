package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "land_records")
data class LandRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val shapeId: String,
    val unitId: String,
    val totalSqFt: Double,
    val serializedInputs: String,
    val triangleCount: Int = 2,
    val timestamp: Long = System.currentTimeMillis()
)

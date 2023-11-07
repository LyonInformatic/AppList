package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val title: String,
    val isCompleted: Boolean = false
)


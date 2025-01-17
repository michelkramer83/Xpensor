package com.jxareas.xpensor.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val icon: Int,
    @ColumnInfo(name = "icon_color")
    val iconColor: String,
)
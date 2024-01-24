package com.example.dmfast.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase


@Entity
data class Campaign(
    @PrimaryKey val uid : Int,
    @ColumnInfo(name = "campaign_name") val cmpName : String
)

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaign")
    suspend fun getAll() : List<Campaign>

    @Insert
    suspend fun insertAll(vararg campaigns : Campaign)

    @Delete
    suspend fun delete(campaign: Campaign)
}

@Database(entities = [Campaign::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao() : CampaignDao
}
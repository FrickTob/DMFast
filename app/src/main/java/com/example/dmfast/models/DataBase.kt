package com.example.dmfast.models

import androidx.room.AutoMigration
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
    @PrimaryKey(autoGenerate = true) val uid : Int,
    @ColumnInfo(name = "campaign_name") val cmpName : String
)

@Entity
data class PlayableCharacter(
    @PrimaryKey(autoGenerate = true) val uid : Int,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "level") val level : Int = 1,
    @ColumnInfo(name = "class") val charClass : String,
    @ColumnInfo(name = "subclass") val subClass : String = "None"
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

@Database(entities = [Campaign::class, PlayableCharacter::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao() : CampaignDao
}
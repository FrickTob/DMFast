package com.example.dmfast.models

import androidx.compose.ui.layout.IntermediateMeasureScope
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
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.google.gson.annotations.SerializedName

@Entity
data class Campaign(
    @PrimaryKey(autoGenerate = true) val id : Int,
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

@Entity
data class PlayableCharacter(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "campaign_id") val cmpID : Int,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "level") val level : Int = 1,
    @ColumnInfo(name = "class") val charClass : String,
    @ColumnInfo(name = "subclass") val subClass : String = "None",
    @ColumnInfo(name = "is_alive") val isAlive : Boolean = true
)

@Dao
interface PlayableCharacterDao {
    @Query("SELECT * FROM playablecharacter")
    suspend fun getAll() : List<PlayableCharacter>

    @Query("SELECT * FROM playablecharacter WHERE campaign_id = :id")
    suspend fun getAllForID(id : Int) : List<PlayableCharacter>
    @Insert
    suspend fun insertAll(vararg playableCharacters : PlayableCharacter)

    @Delete
    suspend fun delete(playableCharacter: PlayableCharacter)
}

@Entity
data class Enemy(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "cr") val cr : Double
)
@Dao
interface EnemyDao {
    @Query("SELECT * FROM enemy")
    suspend fun getAll() : List<Enemy>

    @Query("SELECT * FROM enemy WHERE name = :name")
    suspend fun getEnemy(name : String) : List<Enemy>
    @Insert
    suspend fun insertAll(vararg enemies : Enemy)

    @Delete
    suspend fun delete(enemy: Enemy)
}

@Entity
data class Encounter(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "campaign_id") val cmpID : Int,
    @ColumnInfo(name = "party_members") val partyMemberIDsString : String,
    @ColumnInfo(name = "enemies") val enemyIdsString : String
)

@Dao
interface EncounterDao {
    @Query("SELECT * FROM encounter")
    suspend fun getAll() : List<Encounter>
    @Query("SELECT * FROM encounter WHERE campaign_id = :id")
    suspend fun getALlForID(id : Int) : List<Encounter>
    @Insert
    suspend fun insertAll(vararg encounters : Encounter)

    @Delete
    suspend fun delete(encounter: Encounter)
}

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "campaign_id") val cmpID : Int,
    @ColumnInfo(name = "title") var title : String,
    @ColumnInfo(name = "contents") var contents : String
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    suspend fun getAll() : List<Note>
    @Query("SELECT * FROM note WHERE campaign_id = :id")
    suspend fun getALlForID(id : Int) : List<Note>
    @Insert
    suspend fun insertAll(vararg notes : Note)

    @Update
    suspend fun updateNote(note: Note)
    @Delete
    suspend fun delete(note: Note)
}

@Database(entities = [Campaign::class, PlayableCharacter::class, Enemy::class, Encounter::class, Note::class], version = 4, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao() : CampaignDao
    abstract fun playableCharacterDao() : PlayableCharacterDao
    abstract fun enemyDao() : EnemyDao
    abstract fun encounterDao() : EncounterDao
    abstract fun noteDao() : NoteDao
}
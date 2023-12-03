package gov.anzong.androidnga.db.note

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sp.phone.common.NoteInfo

@Dao
interface NoteDao {
    @Query("SELECT * from notes")
    fun loadNotes(): List<NoteInfo?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateNotes(vararg users: NoteInfo?)
}
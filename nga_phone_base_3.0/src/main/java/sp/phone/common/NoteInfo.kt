package sp.phone.common

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class NoteInfo {
    @ColumnInfo(name = "note")
    var note: String? = null

    @ColumnInfo(name = "nickName")
    var nickName: String? = null

    @PrimaryKey
    @ColumnInfo(name = "userId")
    lateinit var userId: String
}
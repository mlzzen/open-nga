package sp.phone.common

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class NoteInfo {
    operator fun component1(): String? {
        return note
    }

    operator fun component2(): String {
        return nickName
    }

    @ColumnInfo(name = "note")
    var note: String? = null

    @ColumnInfo(name = "nickName")
    lateinit var nickName: String

    @PrimaryKey
    @ColumnInfo(name = "userId")
    lateinit var userId: String
}
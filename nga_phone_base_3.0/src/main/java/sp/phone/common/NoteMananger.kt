package sp.phone.common

import android.content.Context

interface NoteMananger {
    fun addToNotesList(note: NoteInfo?)
    fun removeAllNotesList()
    fun removeFromNotesList(uid: String?)
    fun getNotesList(): MutableList<NoteInfo>?

    fun initialize(context: Context)

    fun getNoteFromList(uid: String): String?
}
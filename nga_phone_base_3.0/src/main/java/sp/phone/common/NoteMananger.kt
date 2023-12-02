package sp.phone.common

interface NoteMananger {
    fun addToNotesList(note: NoteInfo?)
    fun addToNotesList(name: String?, uid: String?, note: String?)
    fun removeAllNotesList()
    fun removeFromNotesList(uid: String?)
    fun getNotesList(): MutableList<NoteInfo>?
}
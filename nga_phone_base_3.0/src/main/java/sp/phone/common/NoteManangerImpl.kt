package sp.phone.common

class NoteManangerImpl() : NoteMananger {
    private val notesList: MutableList<NoteInfo>? = null
    override fun addToNotesList(note: NoteInfo?) {
        if (!notesList?.contains(note)!!) {
            if (note != null) {
                notesList.add(note)
            }
        }
//        notesList.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply()
    }

    private object SingletonHolder {
        var sInstance: NoteMananger = NoteManangerImpl()
    }

    companion object {
        fun getInstance(): NoteMananger? {
            return SingletonHolder.sInstance
        }
    }

    override fun getNotesList() : MutableList<NoteInfo>?{
        return notesList
    }


    override fun addToNotesList(name: String?, uid: String?, note: String?) {
        TODO("Not yet implemented")
    }

    override fun removeAllNotesList() {
        TODO("Not yet implemented")
    }

    override fun removeFromNotesList(uid: String?) {
        TODO("Not yet implemented")
    }
}
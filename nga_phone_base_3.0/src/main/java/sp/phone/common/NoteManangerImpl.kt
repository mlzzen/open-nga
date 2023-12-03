package sp.phone.common

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.util.ThreadUtils
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.db.AppDatabase

class NoteManangerImpl() : NoteMananger {
    private var notesList: MutableList<NoteInfo>? = null
    private var mPrefs: SharedPreferences? = null
    private var mContext: Context? = null

    override fun initialize(context: Context) {
        mContext = context.applicationContext
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        val notesListStr = mPrefs?.getString(PreferenceKey.PREF_NOTES_LIST, "")
        if (TextUtils.isEmpty(notesListStr)) {
            notesList = ArrayList()
        } else {
            notesList = JSON.parseArray(notesListStr, NoteInfo::class.java)
            if (notesList == null) {
                notesList = ArrayList()
            }
        }
        notesList = AppDatabase.getInstance().noteDao().loadNotes() as MutableList<NoteInfo>?
    }

    override fun addToNotesList(note: NoteInfo?) {
        for (i in notesList?.indices!!) {
            val localNote: NoteInfo = notesList!![i]
            if (localNote.userId == note?.userId) {
                if (note != null) {
                    notesList!![i].note = note.note
                    commit()
                    return
                }
            }
        }
        if (note != null) {
            notesList!!.add(note)
            commit()
        }
    }

    private object SingletonHolder {
        var sInstance: NoteMananger = NoteManangerImpl()
    }

    private fun commit() {
        mPrefs?.edit()
            ?.putString(PreferenceKey.PREF_NOTES_LIST, JSON.toJSONString(notesList))
            ?.apply()
        saveNotes()
    }

    private fun saveNotes() {
        ThreadUtils.postOnSubThread {
            synchronized(this) {
                notesList?.let {
                    AppDatabase.getInstance().noteDao()
                        .updateNotes(*it.toTypedArray<NoteInfo>())
                }
            }
        }
    }

    companion object {
        fun getInstance(): NoteMananger? {
            return SingletonHolder.sInstance
        }
    }

    override fun getNotesList(): MutableList<NoteInfo>? {
        return notesList
    }

    override fun removeAllNotesList() {
        notesList?.clear()
        commit()
    }

    override fun getNoteFromList(uid: String): String? {
        for (i in notesList?.indices!!) {
            val localNote: NoteInfo = notesList!![i]
            if (localNote.userId == uid) {
                return localNote.note
            }
        }
        return null
    }

    override fun getNoteFromListByName(userName: String): String? {
        for (i in notesList?.indices!!) {
            val localNote: NoteInfo = notesList!![i]
            if (localNote.nickName == userName) {
                return localNote.note
            }
        }
        return null
    }

    override fun removeFromNotesList(uid: String) {
        for (i in notesList?.indices!!) {
            val noteInfo: NoteInfo = notesList!!.get(i)
            if (noteInfo.userId == uid) {
                notesList!!.removeAt(i)
                commit()
                return
            }
        }
    }
}
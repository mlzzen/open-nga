package sp.phone.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.anzong.androidnga.R
import sp.phone.common.NoteInfo

class NotesListAdapter(context: Context?, notesList: MutableList<NoteInfo>) :
    RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>() {
    private var mContext: Context? = null

    private var mNotesList: MutableList<NoteInfo>

    private var mOnClickListener: View.OnClickListener? = null

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        var userNameView: TextView

        init {
            userNameView = itemView.findViewById(R.id.note_user_name)
        }
    }

    init {
        mContext = context
        mNotesList = notesList
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        mOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val convertView: View = LayoutInflater.from(mContext)
            .inflate(R.layout.fragment_settings_notes_list_item, parent, false)
        val holder = NoteViewHolder(convertView)
        holder.itemView.setOnClickListener(mOnClickListener)
        return holder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val (note, nickName) = mNotesList[position]
        holder.userNameView.text = "$nickName($note)"
        holder.itemView.tag = mNotesList[position]
    }

    override fun getItemCount(): Int {
        return mNotesList.size
    }
}
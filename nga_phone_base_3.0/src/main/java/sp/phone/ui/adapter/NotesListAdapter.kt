package sp.phone.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gov.anzong.androidnga.R
import gov.anzong.androidnga.databinding.FragmentSettingsNotesListItemBinding
import sp.phone.common.NoteInfo

class NotesListAdapter(context: Context?, notesList: MutableList<NoteInfo>) :
    RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>() {
    private var mContext: Context? = null

    private var mNotesList: MutableList<NoteInfo>

    private var mOnClickListener: View.OnClickListener? = null

    lateinit var binding: FragmentSettingsNotesListItemBinding


    class NoteViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        var userNameView: TextView? = null
    }

    init {
        mContext = context
        mNotesList = notesList
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        mOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        binding = FragmentSettingsNotesListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val convertView: View = LayoutInflater.from(mContext)
            .inflate(R.layout.fragment_settings_notes_list_item, parent, false)
        val holder = NoteViewHolder(convertView)
        holder.itemView.setOnClickListener(mOnClickListener)
        holder.userNameView = binding.userName
        return holder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        holder.userNameView!!.text = mNotesList!![position].nickName
        holder.userNameView?.setText(mNotesList!![position].nickName)
        Log.d("holder.userNameView.text", holder.userNameView?.text.toString())
        holder.itemView.tag = mNotesList!![position]
    }

    override fun getItemCount(): Int {
        return mNotesList.size
    }
}
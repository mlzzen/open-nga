package sp.phone.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import gov.anzong.androidnga.R
import sp.phone.common.NoteInfo
import sp.phone.common.User
import sp.phone.util.ImageUtils

class NotesListAdapter(context: Context?, notesList: List<NoteInfo>?) : RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>() {
    private var mContext: Context? = null

    private var mNotesList: List<NoteInfo>? = null

    private var mOnClickListener: View.OnClickListener? = null

    private var mDefaultAvatar: Bitmap? = null

    class NoteViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        @BindView(R.id.user_name)
        var userNameView: TextView? = null

        @BindView(R.id.avatar)
        var avatarView: ImageView? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    init {
        mContext = context
        mNotesList = notesList
        mDefaultAvatar = ImageUtils.loadDefaultAvatar()
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        mOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val convertView: View = LayoutInflater.from(mContext)
            .inflate(R.layout.fragment_settings_notes_list_item, parent, false)
        val holder = NoteViewHolder(convertView)
        holder.itemView.setOnClickListener(mOnClickListener)
        holder.avatarView!!.setImageBitmap(mDefaultAvatar)
        return holder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.userNameView!!.text = mNotesList!![position].nickName
        holder.itemView.tag = mNotesList!![position]
    }

    override fun getItemCount(): Int {
        return mNotesList?.size ?: 0
    }
}
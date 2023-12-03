package sp.phone.ui.fragment;

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gov.anzong.androidnga.R
import sp.phone.common.NoteInfo
import sp.phone.common.NoteMananger
import sp.phone.common.NoteManangerImpl
import sp.phone.common.PhoneConfiguration
import sp.phone.common.User
import sp.phone.ui.adapter.NotesListAdapter
import sp.phone.view.RecyclerViewEx


class SettingsNotesListFragment : BaseFragment(), View.OnClickListener {
    private var mListAdapter: NotesListAdapter? = null

    private var mListView: RecyclerViewEx? = null

    private var mNotesList: NoteMananger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.setting_title_notes_list)
        mNotesList = NoteManangerImpl.getInstance()
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mListAdapter = NotesListAdapter(context, mNotesList!!.getNotesList() ?: emptyList<NoteInfo>().toMutableList())
        mListAdapter!!.setOnClickListener(this)
        mListView = view.findViewById(R.id.list)
        mListView?.setLayoutManager(LinearLayoutManager(context))
        mListView?.setAdapter(mListAdapter)

        val touchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val noteInfo = viewHolder.itemView.tag as NoteInfo
                    mNotesList!!.removeFromNotesList(noteInfo.userId)
                    mListAdapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                }
            })
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mListView)
    }

    override fun onClick(v: View) {
        val user = v.tag as NoteInfo
        showUserProfile(user.nickName)
    }

    private fun showUserProfile(userName: String) {
        val intent = Intent(context, PhoneConfiguration.getInstance().profileActivityClass)
        intent.putExtra("mode", "username")
        intent.putExtra("username", userName)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_notes_list_option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_delete_all_notes) {
            mNotesList!!.removeAllNotesList()
            mListAdapter!!.notifyDataSetChanged()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}
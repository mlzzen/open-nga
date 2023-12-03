package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import gov.anzong.androidnga.R
import gov.anzong.androidnga.base.util.ToastUtils
import sp.phone.common.NoteInfo
import sp.phone.common.NoteManangerImpl.Companion.getInstance


class MakeNoteDialogFragment : BaseDialogFragment() {
    override fun onCreateDialog(b: Bundle?): Dialog {
        val view: View = requireActivity().layoutInflater.inflate(R.layout.dialog_make_note, null)
        val builder = AlertDialog.Builder(context)
        val userName = requireArguments().getString("userName")
        val uid = requireArguments().getString("uid")
        val input = view.findViewById<EditText>(R.id.make_note_edit_text)
        val noteMananger = getInstance()
        val noteStr = uid?.let { noteMananger?.getNoteFromList(it) }
        input.setText(noteStr);
        builder.setTitle(R.string.mark_note)
            .setView(view)
            .setPositiveButton(
                "确认"
            ) { dialog: DialogInterface, _: Int ->
                val length = input.text.toString().length
                if (length in 2..19) {

                    val note = input.text ?: ""
                    val noteInfo = NoteInfo()
                    noteInfo.note = note.toString()
                    noteInfo.userId = uid!!
                    noteInfo.nickName = userName
                    noteMananger?.addToNotesList(noteInfo)
                    dialog.dismiss()
                    ToastUtils.success(R.string.add_to_noteslist_success);
                } else {
                    ToastUtils.error("备注内容长度必须在1~20字范围内")
                }
                try {
                    val field = dialog.javaClass.superclass
                        .getDeclaredField("mShowing")
                    field.isAccessible = true
                    field[dialog] = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.setNegativeButton(
                R.string.cancel,
                DialogInterface.OnClickListener { dialog: DialogInterface, whichButton: Int ->
                    dialog.dismiss()
                    try {
                        val field = dialog.javaClass.superclass
                            .getDeclaredField("mShowing")
                        field.isAccessible = true
                        field[dialog] = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
        return builder.create()
    }

}

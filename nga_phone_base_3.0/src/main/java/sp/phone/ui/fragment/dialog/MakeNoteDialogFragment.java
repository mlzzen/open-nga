package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.lang.reflect.Field;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.R;
import sp.phone.util.NLog;

public class MakeNoteDialogFragment extends BaseDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_make_note, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        EditText input = view.findViewById(R.id.make_note_edit_text);
        builder.setTitle(R.string.mark_note)
                .setView(view)
                .setPositiveButton("确认", (dialog, whichButton) -> {
                    int length = input.getText().toString().length();
                    if (length > 1 && length < 20) {
                        String prefix = getArguments().getString("prefix");
                    } else {
                        ToastUtils.error("备注内容长度必须在1~20字范围内");
                    }
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).setNegativeButton(android.R.string.cancel,
                        (dialog, whichButton) -> {
                            dialog.dismiss();
                            try {
                                Field field = dialog.getClass().getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
        return builder.create();
    }
}

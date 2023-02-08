package sp.phone.ui.fragment.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public abstract class NoframeDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getDialog().setCanceledOnTouchOutside(true);
    }

}

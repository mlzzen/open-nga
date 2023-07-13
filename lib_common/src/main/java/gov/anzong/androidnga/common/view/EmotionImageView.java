package gov.anzong.androidnga.common.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class EmotionImageView extends AppCompatImageView {

    private String mEmotionCode;

    public EmotionImageView(Context context) {
        super(context);
    }

    public void setEmotionCode(String code) {
        mEmotionCode = code;
    }

    public String getEmotionCode(){
        return mEmotionCode;
    }
}

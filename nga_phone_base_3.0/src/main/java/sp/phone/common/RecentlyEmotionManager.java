package sp.phone.common;

import android.content.Context;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.PreferenceKey;

public class RecentlyEmotionManager {
    private Context mContext;

    private List<String> mEmotionList;

    private static final int MAX_EMOTION_COUNT = 40;

    private static class SingleTonHolder {

        private static RecentlyEmotionManager sInstance = new RecentlyEmotionManager();
    }

    public static RecentlyEmotionManager getInstance() {
        return RecentlyEmotionManager.SingleTonHolder.sInstance;
    }

    private RecentlyEmotionManager() {
        mContext = ContextUtils.getContext();
        String emotionStr = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PreferenceKey.KEY_RECENTLY_EMOTION, null);
        if (emotionStr != null) {
            mEmotionList = JSON.parseArray(emotionStr, String.class);
        }
        if (mEmotionList == null) {
            mEmotionList = new ArrayList<>();
        }
    }

    public void addEmotion(String emotion) {
        if (mEmotionList.contains(emotion)) {
            mEmotionList.remove(emotion);
        } else if (mEmotionList.size() >= MAX_EMOTION_COUNT) {
            mEmotionList.remove(mEmotionList.size() - 1);
        }
        mEmotionList.add(0, emotion);
        commit();
    }

    public void removeEmotion(String emotion) {
        if (mEmotionList.contains(emotion)) {
            mEmotionList.remove(emotion);
            commit();
        }
    }

    public void removeEmotion(int index) {
        mEmotionList.remove(index);
        commit();
    }

    public List<String> getEmotionList() {
        return mEmotionList;
    }

    public String[][] getEmotionArray() {
        String[][] result = new String[mEmotionList.size()][];
        for (int i = 0; i < mEmotionList.size(); i++) {
            String info = mEmotionList.get(i);
            if (info != null){
                String[] infos = info.split("-");
                result[i] = infos;
            }
        }
        return result;
    }

    public void removeAllEmotionList() {
        mEmotionList.clear();
        commit();
    }

    private void commit() {
        String emotionStr = JSON.toJSONString(mEmotionList);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(PreferenceKey.KEY_RECENTLY_EMOTION, emotionStr)
                .apply();
    }
}

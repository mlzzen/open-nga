package sp.phone.ui.adapter;

import static gov.anzong.androidnga.common.util.EmoticonUtils.EMOTICON_LABEL;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import gov.anzong.androidnga.common.util.EmoticonUtils;
import sp.phone.common.RecentlyEmotionManager;

/**
 * Created by Justwen on 2018/6/8.
 */
public class EmoticonParentAdapter extends PagerAdapter {

    private Context mContext;

    private RecentlyEmotionManager mRecentlyEmotionManager;

    private int mHeight;

    private String[][][] mEmotionList;

    private static final int COLUMN_COUNT = 4;

    public EmoticonParentAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    private void combineEmotionList() {
        mEmotionList = new String[7][70][3];
        for (int i = 0; i < EmoticonUtils.EMOTICON_URL.length; i++) {
            if (i == 0) {
                String[][] recentlyList = mRecentlyEmotionManager.getEmotionArray();
                mEmotionList[0] = recentlyList;
            } else {
                mEmotionList[i] = EmoticonUtils.EMOTICON_URL[i];
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, COLUMN_COUNT));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecentlyEmotionManager = RecentlyEmotionManager.getInstance();

        combineEmotionList();

        EmoticonChildAdapter adapter = new EmoticonChildAdapter(mContext, mHeight);
        adapter.setData(EMOTICON_LABEL[position][0], getEmotionList(mEmotionList[position], 0),
                getEmotionList(mEmotionList[position], 1), getEmotionList(mEmotionList[position], 2)
        );



        recyclerView.setAdapter(adapter);

        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EMOTICON_LABEL[position][1];
    }

    private String[] getEmotionList(String[][] list, int emotionType) {
        String[] result = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = list[i][emotionType];
        }
        return result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return EMOTICON_LABEL.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

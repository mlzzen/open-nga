package sp.phone.mvp.contract;

import android.content.Intent;
import android.os.Bundle;

import java.util.Map;

import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.http.OnSimpleHttpCallBack;
import sp.phone.http.bean.ThreadData;
import sp.phone.http.bean.ThreadRowInfo;
import sp.phone.param.ArticleListParam;

/**
 *
 * @author Justwen
 * @date 2017/11/22
 */

public interface ArticleListContract {

    interface Presenter {

        void loadPage(ArticleListParam param);

        void banThisSB(ThreadRowInfo row);

        void postComment(ArticleListParam param, ThreadRowInfo row);

        void postSupportTask(int tid, int pid, OnSimpleHttpCallBack callBack);

        void postOpposeTask(int tid, int pid, OnSimpleHttpCallBack callBack);

        void quote(ArticleListParam param, ThreadRowInfo row);

        void cachePage();

        void loadCachePage();
    }

    interface View {

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void hideLoadingView();

        void setData(ThreadData data);

        void startPostActivity(Intent intent);

        void showPostCommentDialog(String prefix, Bundle bundle);

    }

    interface Model {

        void loadPage(ArticleListParam param, OnHttpCallBack<ThreadData> callBack);

        void loadPage(ArticleListParam param, Map<String, String> header, OnHttpCallBack<ThreadData> callBack);

        void cachePage(ArticleListParam param, String rawData);

        void loadCachePage(ArticleListParam param, OnHttpCallBack<ThreadData> callBack);
    }
}

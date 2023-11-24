package sp.phone.mvp.model;

import android.text.TextUtils;

import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.bean.ThreadData;
import sp.phone.http.retrofit.RetrofitHelper;
import sp.phone.http.retrofit.RetrofitService;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.model.convert.ArticleConvertFactory;
import sp.phone.mvp.model.convert.ErrorConvertFactory;
import sp.phone.param.ArticleListParam;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.NLog;

/**
 * 加载帖子内容
 * Created by Justwen on 2017/7/10.
 */

class ArticleListModel : BaseModel(), ArticleListContract.Model {

    private val TAG = ArticleListModel::class.simpleName

    private lateinit var mService: RetrofitService

    init {
        mService =
            RetrofitHelper.getInstance().getService(RetrofitService::class.java) as RetrofitService
    }

    fun getUrl(param: ArticleListParam): String {
        var page = param.page;
        var tid = param.tid;
        var pid = param.pid;
        var authorId = param.authorId;
        var url =
            getAvailableDomain() + "/read.php?" + "&page=" + page + "&__output=8&noprefix&v2";
        if (tid != 0) {
            url = url + "&tid=" + tid;
        }
        if (pid != 0) {
            url = url + "&pid=" + pid;
        }

        if (authorId != 0) {
            url = url + "&authorid=" + authorId;
        }

        return url;

    }


    override fun loadPage(param: ArticleListParam, callBack: OnHttpCallBack<ThreadData>) {
        loadPage(param, null, callBack);
    }

    override fun loadPage(
        param: ArticleListParam,
        header: MutableMap<String, String>?,
        callBack: OnHttpCallBack<ThreadData>
    ) {
        val url = getUrl(param)
        mService.get(url, header)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .compose(getLifecycleProvider().bindUntilEvent(FragmentEvent.DETACH))
            .map { s ->
                val time = System.currentTimeMillis()
                val data = ArticleConvertFactory.getArticleInfo(s)
                NLog.e(TAG, "time = ${System.currentTimeMillis() - time}")
                if (data == null) {
                    val errorMsg = ErrorConvertFactory.getErrorMessage(s)
                    if (errorMsg != null) {
                        throw Exception(errorMsg)
                    } else {
                        throw ServerException("NGA后台抽风了，请尝试右上角菜单中的使用内置浏览器打开")
                    }
                } else {
                    data
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(getLifecycleProvider().bindUntilEvent(FragmentEvent.DETACH))
            .subscribe(object : BaseSubscriber<ThreadData>() {
                override fun onNext(threadData: ThreadData) {
                    callBack.onSuccess(threadData)
                    UserManagerImpl.getInstance().putAvatarUrl(threadData)
                }

                override fun onError(throwable: Throwable) {
                    callBack.onError(ErrorConvertFactory.getErrorMessage(throwable), throwable)
                }
            })
    }


    override fun cachePage(param: ArticleListParam, rawData: String) {

        if (TextUtils.isEmpty(param.topicInfo)) {
            ToastUtils.error("缓存失败！");
            return;
        }
        ThreadUtils.postOnSubThread {
            try {
                val path = ContextUtils.getContext().filesDir.absolutePath + "/cache/${param.tid}"
                val describeFile = File(path, "${param.tid}.json")
                FileUtils.write(describeFile, param.topicInfo)
                val rawDataFile = File(path, "${param.page}.json")
                FileUtils.write(rawDataFile, rawData)
                ToastUtils.success("缓存成功！")
            } catch (e: IOException) {
                ToastUtils.error("缓存失败！")
                e.printStackTrace()
            }
        }
    }

    override fun loadCachePage(param: ArticleListParam, callBack: OnHttpCallBack<ThreadData>) {
        Observable.create<ThreadData> { emitter ->
            val cachePath =
                "${ContextUtils.getContext().filesDir.absolutePath}/cache/${param.tid}/${param.page}.json"
            val cacheFile = File(cachePath)
            val rawData = FileUtils.readFileToString(cacheFile)
            val threadData = ArticleConvertFactory.getArticleInfo(rawData)
            if (threadData != null) {
                emitter.onNext(threadData)
            } else {
                emitter.onError(Exception())
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseSubscriber<ThreadData>() {
                override fun onNext(threadData: ThreadData) {
                    callBack.onSuccess(threadData)
                }

                override fun onError(throwable: Throwable) {
                    callBack.onError("读取缓存失败！")
                }
            })
    }

    class ServerException(message: String) : Exception(message)

}

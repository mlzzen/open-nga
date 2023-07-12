package sp.phone.task;

import static sp.phone.util.ActivityUtils.showToast;

import java.util.Calendar;
import java.util.Date;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.http.retrofit.RetrofitHelper;
import sp.phone.http.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.StringUtils;

/**
 * 签到
 * Created by mlzzen on 2023/7/7.
 */
public class CheckInTask {
    private static final String url = Utils.getNGAHost() + "nuke.php?__lib=check_in&__act=check_in&lite=js";

    public static void execute() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        long checkInLastTime = PreferenceUtils.getData(PreferenceKey.CHECK_IN_LAST_TIME, new Date().getTime() - 24 * 3600 * 1000);
        cal1.setTime(new Date(checkInLastTime));
        cal2.setTime(new Date());
        if (cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)) {
            RetrofitService service = RetrofitHelper.getInstance().getService();
            service.post(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<String>() {
                        @Override
                        public void onNext(String result) {
                            String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
                            if (msg.indexOf("签到成功") != -1) {
                                showToast("签到成功");
                            }
                            if (msg.indexOf("签到成功") != -1 || msg.indexOf("今天已经签到") != -1) {
                                PreferenceUtils.putData(PreferenceKey.CHECK_IN_LAST_TIME, new Date().getTime());
                            }
                        }
                    });
        }
    }
}

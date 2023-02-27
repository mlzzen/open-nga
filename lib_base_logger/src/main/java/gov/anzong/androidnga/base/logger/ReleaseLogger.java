package gov.anzong.androidnga.base.logger;

import android.util.Log;

public class ReleaseLogger implements ILogger {
    @Override
    public String d(String tag, String msg) {
        Log.d(tag, msg);
        return msg;
    }

    @Override
    public String printStackTrace(Throwable throwable) {
        return "";
    }
}

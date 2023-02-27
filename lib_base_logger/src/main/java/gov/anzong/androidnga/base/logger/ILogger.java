package gov.anzong.androidnga.base.logger;

public interface ILogger {

    String TAG = "NGAClient";

    String d(String tag, String msg);

    default String d(int msg) {
        return "";
    }

    default String d(float msg) {
        return "";
    }

    default String d(boolean msg) {
        return "";
    }

    default String d(Object msg) {
        return d(TAG, String.valueOf(msg));
    }

    default String d() {
        return "";
    }

    default String printStackTrace(Throwable throwable) {
        return "";
    }
}

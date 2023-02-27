package gov.anzong.androidnga.base.logger;

public class Logger implements ILogger {

    private static final Logger INSTANCE = new Logger();

    private ILogger mLoggerDelegate = new ReleaseLogger();

    public void setLogger(ILogger logger) {
        if (logger != null) {
            mLoggerDelegate = logger;
        }
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    @Override
    public String d(String tag, String msg) {
        return mLoggerDelegate.d(tag, msg);
    }

    @Override
    public String d(int msg) {
        return mLoggerDelegate.d(msg);
    }

    @Override
    public String d(float msg) {
        return mLoggerDelegate.d(msg);
    }

    @Override
    public String d(Object msg) {
        return mLoggerDelegate.d(msg);
    }

    @Override
    public String d(boolean msg) {
        return mLoggerDelegate.d(msg);
    }

    @Override
    public String d() {
        return mLoggerDelegate.d();
    }
}

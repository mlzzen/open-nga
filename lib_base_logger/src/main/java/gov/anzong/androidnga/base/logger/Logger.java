package gov.anzong.androidnga.base.logger;

public class Logger implements ILogger {

    private static final Logger INSTANCE = new Logger();

    private ILogger mLoggerDelegate;

    private boolean mLocalDebug;

    private Logger() {
        updateLogger();
    }

    public boolean isLocalDebug() {
        return mLocalDebug;
    }

    public void setLocalDebug(boolean localDebug) {
        mLocalDebug = localDebug;
    }

    public void setLogger(ILogger logger) {
        if (logger != null) {
            if (mLoggerDelegate != null) {
                mLoggerDelegate.clear();
            }
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

    @Override
    public void clear() {
        mLoggerDelegate.clear();
    }

    public void updateLogger() {
        if (mLocalDebug) {
            setLogger(new LocalDebugLogger());
        } else if (BuildConfig.DEBUG) {
            setLogger(new DebugLogger());
        } else {
            setLogger(new ReleaseLogger());
        }
    }
}

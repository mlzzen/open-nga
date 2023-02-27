package gov.anzong.androidnga.base.logger;

import android.util.Log;

public class DebugLogger implements ILogger {

    private static final int STACK_LENGTH = 6;

    @Override
    public String d(String tag, String msg) {
        String finalMsg = addMethodLine(msg);
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d() {
        String finalMsg = addMethodLine("");
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(Object msg) {
        String finalMsg = addMethodLine(String.valueOf(msg));
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(float msg) {
        String finalMsg = addMethodLine(String.valueOf(msg));
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(boolean msg) {
        String finalMsg = addMethodLine(String.valueOf(msg));
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(int msg) {
        String finalMsg = addMethodLine(String.valueOf(msg));
        Log.d(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String printStackTrace(Throwable throwable) {
        String msg = Log.getStackTraceString(throwable);
        d(TAG, msg);
        return msg;
    }

    protected String addMethodLine(String msg) {
        String finalMsg = "";
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < STACK_LENGTH) {
            finalMsg = "Stack to shallow";
        } else {
            try {
                StackTraceElement element = elements[STACK_LENGTH];
                String fullClassName = element.getClassName();
                String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                String methodName = element.getMethodName();
                int lineNumber = element.getLineNumber();
                finalMsg = className + "." + methodName + "():"
                        + lineNumber + " " + msg;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return finalMsg;
    }

}

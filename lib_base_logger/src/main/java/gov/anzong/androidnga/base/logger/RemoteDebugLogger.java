package gov.anzong.androidnga.base.logger;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RemoteDebugLogger extends DebugLogger {

    private static final String FILE_TAG = "log";

    private final ExecutorService mExecutorService;

    private FileWriter mFileWriter;

    private final DateFormat mDateFormat;

    public RemoteDebugLogger() {
        mExecutorService = new ThreadPoolExecutor(0, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardOldestPolicy());
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss:SSS", Locale.getDefault());
        try {
            mFileWriter = new FileWriter(getOutputFile(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getOutputFile() {
        File logDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/nga_open_source/log/");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        File file = new File(logDir, dateFormat.format(new Date()));
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else if (!file.canWrite()) {
                file = new File(logDir, file.getName() + "_" + System.currentTimeMillis());
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

    }

    @Override
    public String d(String tag, String msg) {
        String finalMsg = super.d(tag, msg);
        output(tag, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(int msg) {
        String finalMsg = super.d(msg);
        output(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(float msg) {
        String finalMsg = super.d(msg);
        output(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(Object msg) {
        String finalMsg = super.d(msg);
        output(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d() {
        String finalMsg = super.d();
        output(TAG, finalMsg);
        return finalMsg;
    }

    @Override
    public String d(boolean msg) {
        String finalMsg = super.d(msg);
        output(TAG, finalMsg);
        return finalMsg;
    }

    private void output(String tag, String msg) {
        String time = mDateFormat.format(new Date());
        mExecutorService.execute(() -> {
            try {
                mFileWriter.write(time + " " + tag + " " + msg);
                mFileWriter.write("\n");
                mFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}

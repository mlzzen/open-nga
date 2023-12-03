package gov.anzong.androidnga.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import gov.anzong.androidnga.db.note.NoteDao;
import gov.anzong.androidnga.db.user.UserDao;
import sp.phone.common.NoteInfo;
import sp.phone.common.User;

/**
 * @author yangyihang
 */
@Database(entities = {User.class, NoteInfo.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String MAIN_DB_NAME = "app_database.db";

    private static AppDatabase sInstance;

    public static void init(Context context) {
        sInstance = Room.databaseBuilder(context, AppDatabase.class, MAIN_DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static AppDatabase getInstance() {
        return sInstance;
    }

    public abstract UserDao userDao();

    public abstract NoteDao noteDao();

}

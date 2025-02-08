package gal.cifpacarballeira.unidad4_tarea7gestordeberes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeworkRepository {

    private final HomeworkDB homeworkDB;

    public HomeworkRepository(Context context) {
        homeworkDB = new HomeworkDB(context);
    }

    public long insertHomework(Homework homework) {
        System.out.println(homework);
        SQLiteDatabase db = homeworkDB.getWritableDatabase();
        ContentValues values = homework.toContentValues();
        long result = db.insert("homeworks", null, homework.toContentValues());
        homework.setId(result);
        db.close();
        return result;
    }

    public List<Homework> getAllHomework() {
        SQLiteDatabase db = homeworkDB.getReadableDatabase();
        List<Homework> homeworkList = new ArrayList<>();
        Cursor cursor = db.query("homeworks", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                homeworkList.add(Homework.fromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return homeworkList;
    }

    public int updateHomework(long id, Homework homework) {
        SQLiteDatabase db = homeworkDB.getWritableDatabase();
        int rowsUpdated = db.update(
               "homeworks",
                homework.toContentValues(),
                "id" + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsUpdated;
    }

    public int deleteHomework(long id) {
        SQLiteDatabase db = homeworkDB.getWritableDatabase();
        int rowsDeleted = db.delete(
                "homeworks",
                "id" + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsDeleted;
    }
}

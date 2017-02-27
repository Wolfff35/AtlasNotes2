package ua.com.wolfff.atlasnotes2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.Date;

/**
 * Created by wolff on 18.06.2016.
 */
public class DatabaseConnector {
    // Имя базы данных
    private static final String DATABASE_NAME = "AtlasNotes";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "_name";

    private static final String TABLE_NOTES = "notes";

    private static final String CREATE_NOTES_TABLE = "CREATE TABLE "+TABLE_NOTES+" ("+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_NAME+
            " TEXT,_isFoget INTEGER,_describe TEXT, _category INTEGER, " +
            "_year_add INTEGER, _month_add INTEGER, _day_add INTEGER," +
            "_year_foget INTEGER, _month_foget INTEGER, _day_foget INTEGER)";

    private SQLiteDatabase database; // Для взаимодействия с базой данных
    private DatabaseOpenHelper databaseOpenHelper;

    // Открытый конструктор DatabaseConnector
    public DatabaseConnector(Context context){
        // Создание нового объекта DatabaseOpenHelper
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }
    // Открытие подключения к базе данных
    public void open() throws SQLException {
        // Создание или открытие базы данных для чтения/записи
        database = databaseOpenHelper.getWritableDatabase();
    }
    // Закрытие подключения к базе данных
    public void close(){
        if (database != null)
            database.close();
    }

    public long insertNotes(String name, String describe, boolean isFoget, int category){
        ContentValues newNotes = new ContentValues();
        newNotes.put("_name", name);
        newNotes.put("_describe", describe);
        newNotes.put("_isFoget", isFoget);

        Date now = new Date();
        if (isFoget) {
            newNotes.put("_year_foget", now.getYear());
            newNotes.put("_month_foget", now.getMonth());
            newNotes.put("_day_foget", now.getDate());
        }else {
            newNotes.put("_year_foget", "");
            newNotes.put("_month_foget", "");
            newNotes.put("_day_foget", "");
        }
        newNotes.put("_year_add", now.toString());
        newNotes.put("_month_add", now.toString());
        newNotes.put("_day_add", now.toString());

        newNotes.put("_category",category);
        open(); // Открытие базы данных
        long rowID = database.insert(TABLE_NOTES, null, newNotes);
        close(); // Закрытие базы данных
        return rowID;
    }

    public void updateNotes(long id,String name, String describe, boolean isFoget,int category){
        ContentValues editNotes = new ContentValues();
        editNotes.put("_name", name);
        editNotes.put("_describe", describe);
        editNotes.put("_isFoget", isFoget);

        Date now = new Date();
        if (isFoget) {
            editNotes.put("_year_foget", now.getYear());
            editNotes.put("_month_foget", now.getMonth());
            editNotes.put("_day_foget", now.getDate());
        }else {
            editNotes.put("_year_foget", "");
            editNotes.put("_month_foget", "");
            editNotes.put("_day_foget", "");
        }
        editNotes.put("_year_add", now.toString());
        editNotes.put("_month_add", now.toString());
        editNotes.put("_day_add", now.toString());

        editNotes.put("_category",category);
        open(); // Открытие базы данных
        database.update(TABLE_NOTES, editNotes,"_id="+id,null);
        close(); // Закрытие базы данных
    }

    // Получение курсора со всеми именами контактов в базе данных
    public Cursor getAllNotes(){
        return database.query(TABLE_NOTES, new String[] {"_id","_name","_describe"},null, null, null, null,"_name");
    }

    public Cursor getOneNote(long id){
        return database.query(TABLE_NOTES, null,"_id=" + id, null, null, null, null);
    }
    public void deleteNote(long id){
        open();
        database.delete(TABLE_NOTES,"_id="+id,null);
        close();
    }
    private class DatabaseOpenHelper extends SQLiteOpenHelper{
        public DatabaseOpenHelper(Context context,String name,CursorFactory factory,int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NOTES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

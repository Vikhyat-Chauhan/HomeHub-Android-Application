package helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.FontsContract;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HomeHubDB.db";
    public static final String TABLE_NAME = "devices";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_CHIPID = "Chipid";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_PASSWORD = "Password";

    //We need to pass database information along to superclass
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CHIPID   + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT " +
                ");";
        db.execSQL(query);
        Log.i("Debug","Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.i("Debug","Table Dropped");
    }

    //Add a new row to the username database
    public void addCredentials(String chipid,String username,String password){
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHIPID, chipid);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
        Log.i("Debug","Credentials added");
    }

    //Delete a Credentials from the database
    public void deleteUsername(String chipid){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_CHIPID + "=\"" + chipid + "\";");
        Log.i("Debug","Dropped the table");
    }

    //Extract username from Database
    public ArrayList<String> getchipid(){
        ArrayList<String> chipid = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_CHIPID + " FROM " + TABLE_NAME + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("Chipid")) != null) {
                chipid.add(chipid.size(),c.getString(c.getColumnIndex("Chipid")));
            }
            c.moveToNext();
        }
        db.close();
       /* for(int i=0;i<usernames.size();i++){
            System.out.println(usernames.get(i));
        }*/
        return chipid;
    }

    //Extract password from Database
    public ArrayList<String> getPasswords(){
        ArrayList<String> passwords = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+ COLUMN_PASSWORD + " FROM " + TABLE_NAME + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("Password")) != null) {
                passwords.add(passwords.size(),c.getString(c.getColumnIndex("Password")));
            }
            c.moveToNext();
        }
        db.close();
        return passwords;
    }

    //Extract username from Database
    public String getUsernamesPasswords(String username){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + "=\"" + username + "\";";
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        String passwrod = c.getString(0);
        Log.i("Debug","Password retrieved.");
        return passwrod;
    }

}

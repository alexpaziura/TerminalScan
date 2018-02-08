package ua.gov.nfp.oleksii.terminalscan;

/**
 * Created by Oleksii on 08.02.2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME="TermAndMon";

    public static final String TABLE_NAME="Products";

    public static final String Table_Column_ID="id";

    public static final String Table_Column_Type="type";

    public static final String Table_Column_ST="st";

    public static final String Table_Column_MAC="MAC";

    public SQLiteHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+Table_Column_ID+" INTEGER PRIMARY KEY, "+Table_Column_Type+" VARCHAR(1), "+Table_Column_ST+" VARCHAR, "+Table_Column_MAC+" VARCHAR)";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

}
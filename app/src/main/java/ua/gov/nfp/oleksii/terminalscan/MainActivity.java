package ua.gov.nfp.oleksii.terminalscan;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    TabHost tabHost;
    SQLiteDatabase sqLiteDatabaseObj;
    ListView lvTerm, lvMon;
    Button btnAddTerm, btnAddMon, btnScnStTerm, btnScnMacTerm, btnSaveTerm, btnScnMon, btnSaveMon;
    EditText evStTerm, evMacTerm, evStMon;
    ListAdapter listTerminalAdapter;

    ArrayList<String> ID_TERM_Array;
    ArrayList<String> ST_TERM_Array;
    ArrayList<String> MAC_TERM_Array;
    ArrayList<String> ID_MON_Array;
    ArrayList<String> ST_MON_Array;
    String SQLiteDataBaseQueryHolder, StTermHolder, MACTermHolder, StMonHolder;
    boolean evTermEmptyHold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        spec.setContent(R.id.terminalTab);
        spec.setIndicator(getResources().getString(R.string.tabTerm));
        tabHost.addTab(spec);
        //Tab 2
        spec = tabHost.newTabSpec("Tab Two");
        spec.setContent(R.id.monitorTab);
        spec.setIndicator(getResources().getString(R.string.tabMon));
        tabHost.addTab(spec);

        btnAddTerm = (Button) findViewById(R.id.btnAddTerm);
        btnAddMon = (Button) findViewById(R.id.btnAddMon);

        lvTerm = (ListView) findViewById(R.id.lvTerm);
        lvMon = (ListView) findViewById(R.id.lvMon);

        btnAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogTerm();
            }
        });

        btnAddMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogMon();
            }
        });

        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+"("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
             ""+SQLiteHelper.Table_Column_Type+" VARCHAR(1), "+SQLiteHelper.Table_Column_ST+" VARCHAR, "+SQLiteHelper.Table_Column_MAC+" VARCHAR);");

        ID_TERM_Array = new ArrayList<String>();
        ST_TERM_Array = new ArrayList<String>();
        MAC_TERM_Array = new ArrayList<String>();

        ID_MON_Array = new ArrayList<String>();
        ST_MON_Array = new ArrayList<String>();


        sqLiteHelper = new SQLiteHelper(this);

        ShowSQLiteDBdata();
    }

    private void ShowSQLiteDBdata() {

        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+"",null);

        ID_TERM_Array.clear();
        ST_TERM_Array.clear();
        MAC_TERM_Array.clear();
        ID_MON_Array.clear();
        ST_MON_Array.clear();

        String tmp_type;
        if (cursor.moveToFirst()) {
            do {
                tmp_type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_Type));
                Log.i("CURSOR", tmp_type);
                if(tmp_type.equals("T")) {
                    ID_TERM_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)));
                    ST_TERM_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ST)));
                    MAC_TERM_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_MAC)));
                } else if (tmp_type.equals("M")) {
                    ID_MON_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)));
                    ST_MON_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ST)));
                }
            } while (cursor.moveToNext());
        }

        listTerminalAdapter = new ListTermAdapter(MainActivity.this,

                ID_TERM_Array,
                ST_TERM_Array,
                MAC_TERM_Array
        );

        lvTerm.setAdapter(listTerminalAdapter);

        cursor.close();
    }

    void showDialogTerm () {
        final Dialog dgTerm = new Dialog(MainActivity.this);
        //create dialog without title
        dgTerm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set the custom dialog's layout to the dialog
        dgTerm.setContentView(R.layout.dialog_term);
        //set the background of dialog box as transparent
        dgTerm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //display the dialog box
        dgTerm.show();

        btnScnStTerm = (Button) dgTerm.findViewById(R.id.btnScnStTerm);
        btnScnMacTerm = (Button) dgTerm.findViewById(R.id.btnScnMacTerm);
        btnSaveTerm = (Button) dgTerm.findViewById(R.id.btnSaveTerm);
        evStTerm = (EditText) dgTerm.findViewById(R.id.evStTerm);
        evMacTerm = (EditText) dgTerm.findViewById(R.id.evMacTerm);


        btnSaveTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEvTerm();
                InsertTerminal();

                dgTerm.dismiss();
            }
        });



    }

    public void CheckEvTerm(){

        StTermHolder = evStTerm.getText().toString() ;
        MACTermHolder = evMacTerm.getText().toString();

        if(TextUtils.isEmpty(StTermHolder) || TextUtils.isEmpty(MACTermHolder)){
            evTermEmptyHold = false ;
        }
        else {
            evTermEmptyHold = true ;
        }
    }


    public void InsertTerminal(){

        if(evTermEmptyHold)
        {

            SQLiteDataBaseQueryHolder = "INSERT INTO "+SQLiteHelper.TABLE_NAME+" (type,st,MAC) " +
                    "VALUES('T','"+StTermHolder+"', '"+MACTermHolder+"');";

            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

            Toast.makeText(MainActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();

        }
        else {

            Toast.makeText(MainActivity.this,"Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();

        }

    }

    void showDialogMon () {
        final Dialog dgMon = new Dialog(MainActivity.this);
        //create dialog without title
        dgMon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set the custom dialog's layout to the dialog
        dgMon.setContentView(R.layout.dialog_mon);
        //set the background of dialog box as transparent
        dgMon.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //display the dialog box
        dgMon.show();

        btnScnMon = (Button) dgMon.findViewById(R.id.btnScnMon);
        btnSaveMon = (Button) dgMon.findViewById(R.id.btnSaveMon);
        evStMon = (EditText) dgMon.findViewById(R.id.evStMon);




    }

}

package ua.gov.nfp.oleksii.terminalscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;
    TabHost tabHost;
    SQLiteDatabase sqLiteDatabaseObj;
    ListView lvTerm, lvMon;
    Button btnAddTerm, btnAddMon;
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

            }
        });

        btnAddMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+"("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
             ""+SQLiteHelper.Table_Column_Type+" VARCHAR(1), "+SQLiteHelper.Table_Column_ST+" VARCHAR, "+SQLiteHelper.Table_Column_MAC+" VARCHAR);");

        //sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();



    }


}

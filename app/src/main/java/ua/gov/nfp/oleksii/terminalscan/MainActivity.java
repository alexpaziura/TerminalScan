package ua.gov.nfp.oleksii.terminalscan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    TabHost tabHost;
    SQLiteDatabase sqLiteDatabaseObj;
    ListView lvTerm, lvMon;
    Button btnAddTerm, btnAddMon, btnScnStTerm, btnScnMacTerm, btnSaveTerm, btnScnMon, btnSaveMon;
    EditText evStTerm, evMacTerm, evStMon;
    ListAdapter listTerminalAdapter, listMonitorAdapter;

    ArrayList<String> ID_TERM_Array;
    ArrayList<String> ST_TERM_Array;
    ArrayList<String> MAC_TERM_Array;
    ArrayList<String> ID_MON_Array;
    ArrayList<String> ST_MON_Array;
    String SQLiteDataBaseQueryHolder, StTermHolder, MACTermHolder, StMonHolder;
    String resScn;
    String scaning = "";
    boolean evTermEmptyHold, evMonEmptyHold;

    private IntentIntegrator qrScan;

    Dialog dgTerm, dgMon;

    Workbook wb;

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

        qrScan = new IntentIntegrator(this);
    }

    protected void ShowSQLiteDBdata() {

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

        listMonitorAdapter = new ListMonAdapter(MainActivity.this,
                ID_MON_Array,
                ST_MON_Array
        );

        lvMon.setAdapter(listMonitorAdapter);

        cursor.close();
    }

    public void deleteTerminal(String idToDel) {
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + SQLiteHelper.TABLE_NAME + " WHERE " + SQLiteHelper.Table_Column_ID
              + "=" + idToDel + "");
        ShowSQLiteDBdata();
    }

    void showDialogTerm () {

        dgTerm = new Dialog(MainActivity.this);
        //create dialog without title
        dgTerm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set the custom dialog's layout to the dialog
        dgTerm.setContentView(R.layout.dialog_term);
        //set the background of dialog box as transparent
        dgTerm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //display the dialog box
        dgTerm.show();
        dgTerm.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                scaning = "";
            }
        });

        btnScnStTerm = (Button) dgTerm.findViewById(R.id.btnScnStTerm);
        btnScnMacTerm = (Button) dgTerm.findViewById(R.id.btnScnMacTerm);
        btnSaveTerm = (Button) dgTerm.findViewById(R.id.btnSaveTerm);
        evStTerm = (EditText) dgTerm.findViewById(R.id.evStTerm);
        evMacTerm = (EditText) dgTerm.findViewById(R.id.evMacTerm);


        btnSaveTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckEvTerm()) {
                   // evStMon.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }
                InsertTerminal();
                dgTerm.dismiss();
                ShowSQLiteDBdata();
            }
        });

        btnScnStTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaning = "Ts";
                resScn = "";
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "CODE_128");
                startActivityForResult(intent, 0);
                //String contents = intent.getStringExtra("SCAN_RESULT");
               // evStTerm.setText(contents);
            }
        });

        btnScnMacTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaning = "Tm";
                resScn = "";
                //qrScan.initiateScan();
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "CODE_39");
                startActivityForResult(intent, 0);
            }
        });

        evStTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(evStTerm.getText().length()>0) {
                    evStTerm.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }
                else {
                    evStTerm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                }
            }
        });
        evMacTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(evMacTerm.getText().length()>0) {
                    evMacTerm.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }
                else {
                    evMacTerm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                }
            }
        });

    }

    public boolean CheckEvTerm(){

        StTermHolder = evStTerm.getText().toString() ;
        MACTermHolder = evMacTerm.getText().toString();
        if(TextUtils.isEmpty(StTermHolder)){
            evStTerm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
        }
        if(TextUtils.isEmpty(MACTermHolder)){
            evMacTerm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
        }
        return !(TextUtils.isEmpty(StTermHolder) || TextUtils.isEmpty(MACTermHolder));
/*        if){
            return false;
        }
        else {
            return true ;
        }*/
    }


    public void InsertTerminal(){

            SQLiteDataBaseQueryHolder = "INSERT INTO "+SQLiteHelper.TABLE_NAME+" (type,st,MAC) " +
                    "VALUES('T','"+StTermHolder+"', '"+MACTermHolder+"');";
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            Toast.makeText(MainActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();

    }

    void showDialogMon () {
        scaning = "M";
        dgMon = new Dialog(MainActivity.this);
        //create dialog without title
        dgMon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set the custom dialog's layout to the dialog
        dgMon.setContentView(R.layout.dialog_mon);
        //set the background of dialog box as transparent
        dgMon.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //display the dialog box
        dgMon.show();
        dgMon.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
                scaning = "";
            }
        });

        btnScnMon = (Button) dgMon.findViewById(R.id.btnScnMon);
        btnSaveMon = (Button) dgMon.findViewById(R.id.btnSaveMon);
        evStMon = (EditText) dgMon.findViewById(R.id.evStMon);

        btnSaveMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckEvMon()) {
                    //evStMon.setHighlightColor(getResources().getColor(R.color.re));
                    evStMon.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    return;
                }
                InsertMonitor();
                dgMon.dismiss();
                ShowSQLiteDBdata();
            }
        });

        btnScnMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaning = "M";
                resScn = "";
                //qrScan.initiateScan();
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "CODE_128");
                startActivityForResult(intent, 0);
            }
        });

        evStMon.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(evStMon.getText().length()>0) {
                        evStMon.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    }
                    else {
                        evStMon.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    }
                }
        });

    }
    public boolean CheckEvMon(){

        StMonHolder = evStMon.getText().toString() ;
        return (!TextUtils.isEmpty(StMonHolder));

    }


    public void InsertMonitor(){

            SQLiteDataBaseQueryHolder = "INSERT INTO "+SQLiteHelper.TABLE_NAME+" (type,st) " +
                    "VALUES('M','"+StMonHolder+"');";
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            Toast.makeText(MainActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();

      }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (scaning) {
            case "Ts":
                dgTerm.show();
                break;
            case "Tm":
                dgTerm.show();
                break;
            case "M":
                dgMon.show();
                break;
            default: break;
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.i("SCAN_FORMAT",data.getStringExtra("SCAN_RESULT_FORMAT"));
                switch (scaning) {
                    case "Ts":
                        evStTerm.setText(data.getStringExtra("SCAN_RESULT"));
                        break;
                    case "Tm":
                        String tmpMac = data.getStringExtra("SCAN_RESULT");
                        try {
                            tmpMac = tmpMac.substring(0,2)+":"+tmpMac.substring(2,4)+":"+
                                    tmpMac.substring(4,6)+":"+tmpMac.substring(6,8)+":"+
                                    tmpMac.substring(8,10)+":"+tmpMac.substring(10, tmpMac.length());
                        } catch (Exception e) {
                            tmpMac = "";
                            e.printStackTrace();
                        }
                        evMacTerm.setText(tmpMac);
                        break;
                    case "M":
                        evStMon.setText(data.getStringExtra("SCAN_RESULT"));
                        break;
                    default: break;
                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }

        /*IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                resScn = result.getContents();
                Log.i("Scan", resScn);
                switch (scaning) {
                    case "Ts":
                        evStTerm.setText(resScn);
                        break;
                    case "Tm":
                        String tmpMac = resScn;
                        tmpMac = tmpMac.substring(0,2)+":"+tmpMac.substring(2,4)+":"+
                                tmpMac.substring(4,6)+":"+tmpMac.substring(6,8)+":"+
                                tmpMac.substring(8,10)+":"+tmpMac.substring(10, tmpMac.length());
                        evMacTerm.setText(tmpMac);
                        break;
                    case "M":
                        evStMon.setText(resScn);
                        break;
                    default: break;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    private boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("ExcelLog", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        wb = new HSSFWorkbook();

        //New Sheet
        Sheet sheet1 = wb.createSheet("Terminals");

        Sheet sheet2 = wb.createSheet("Monitors");

        getTerminalSheet(sheet1, sheet2);

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(this, "List exported to "+file,Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_xls, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.exportXLS:
                saveExcelFile(this,"ExportList.xls");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void getTerminalSheet(Sheet termSheet, Sheet monSheet) {

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // Generate column headings
        Row row = termSheet.createRow(0);

        c = row.createCell(0);
        c.setCellValue("№");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Service Tag");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Mac");
        c.setCellStyle(cs);

        termSheet.setColumnWidth(0, (15 * 500));
        termSheet.setColumnWidth(1, (15 * 500));
        termSheet.setColumnWidth(2, (15 * 500));

        Row row2 = monSheet.createRow(0);

        c = row2.createCell(0);
        c.setCellValue("№");
        c.setCellStyle(cs);

        c = row2.createCell(1);
        c.setCellValue("Service Tag");
        c.setCellStyle(cs);

        monSheet.setColumnWidth(0, (15 * 500));
        monSheet.setColumnWidth(1, (15 * 500));
        monSheet.setColumnWidth(2, (15 * 500));

        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+"",null);

        String tmp_type;
        int i = 1, j = 1;
        if (cursor.moveToFirst()) {
            do {
                tmp_type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_Type));
                if(tmp_type.equals("T")) {
                    row = termSheet.createRow(i);
                    c = row.createCell(0);
                    c.setCellValue(i);

                    c = row.createCell(1);
                    c.setCellValue(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ST)));

                    c = row.createCell(2);
                    c.setCellValue(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_MAC)));
                    i++;
                } else if (tmp_type.equals("M")) {
                    row2 = monSheet.createRow(j);
                    c = row2.createCell(0);
                    c.setCellValue(j);

                    c = row2.createCell(1);
                    c.setCellValue(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ST)));
                    j++;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
    }


}

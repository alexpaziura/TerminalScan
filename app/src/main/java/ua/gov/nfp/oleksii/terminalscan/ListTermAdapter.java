package ua.gov.nfp.oleksii.terminalscan;

/**
 * Created by Oleksii on 09.02.2018.
 */

import java.util.ArrayList;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ListTermAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> ID;
    ArrayList<String> ST;
    ArrayList<String> MAC;

    public ListTermAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> st,
            ArrayList<String> mac
    )
    {

        this.context = context2;
        this.ID = id;
        this.ST = st;
        this.MAC = mac;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ID.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(final int position, View child, ViewGroup parent) {

        Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            child = layoutInflater.inflate(R.layout.item_term, null);

            holder = new Holder();

            holder.tvId = (TextView) child.findViewById(R.id.tvID);
            holder.tvSt = (TextView) child.findViewById(R.id.tvSt);
            holder.tvMac = (TextView) child.findViewById(R.id.tvMac);
           // holder.btnDel = (ImageButton) child.findViewById(R.id.btnDel);
            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }
        holder.tvId.setText(ID.get(position));
        holder.tvSt.setText("Service Tag: "+ST.get(position));
        holder.tvMac.setText("MAC: "+MAC.get(position));
      /*  holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase = sqLiteHelper.getWritableDatabase();

                sqLiteDatabase.execSQL("DELETE FROM " + SQLiteHelper.TABLE_NAME + " WHERE " + SQLiteHelper.Table_Column_ID
                        + "=" + ID.get(position) + "");
                //MainActivity.ShowSQLiteDBdata();
            }
        });*/
        return child;
    }

    public class Holder {

        TextView tvId;
        TextView tvSt;
        TextView tvMac;
        //ImageButton btnDel;
    }

}
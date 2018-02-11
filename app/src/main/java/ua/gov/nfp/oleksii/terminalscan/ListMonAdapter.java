package ua.gov.nfp.oleksii.terminalscan;

/**
 * Created by Oleksii on 09.02.2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListMonAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> IDm;
    ArrayList<String> STm;


    public ListMonAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> st
    )
    {

        this.context = context2;
        this.IDm = id;
        this.STm = st;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return IDm.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View child, ViewGroup parent) {

        Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            child = layoutInflater.inflate(R.layout.item_mon, null);

            holder = new Holder();

            holder.tvId = (TextView) child.findViewById(R.id.tvItIdMon);
            holder.tvSt = (TextView) child.findViewById(R.id.tvItStMon);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }
        holder.tvId.setText(IDm.get(position));
        holder.tvSt.setText("Service Tag: "+STm.get(position));

        return child;
    }

    public class Holder {

        TextView tvId;
        TextView tvSt;

    }

}
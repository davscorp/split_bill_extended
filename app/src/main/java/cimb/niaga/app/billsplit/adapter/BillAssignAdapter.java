package cimb.niaga.app.billsplit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.fragment.CameraFragment;

/**
 * Created by Denny on 1/20/2017.
 */

public class BillAssignAdapter extends BaseAdapter {
    private Activity activity;

    public BillAssignAdapter(Activity act) {
        this.activity = act;
    }

    @Override
    public int getCount() {
        return CameraFragment.scanned_price.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ListHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_new_item, null);
            holder = new ListHolder();

            convertView.setTag(holder);
        } else {
            holder = (ListHolder) convertView.getTag();
        }

        holder.txt_new_item = (TextView) convertView.findViewById(R.id.txt_new_item);

        holder.txt_new_item.setText(CameraFragment.scanned_price.get(position));


        convertView.setTag(holder);

        return convertView;
    }

    class ListHolder {
        TextView txt_new_item;
    }
}

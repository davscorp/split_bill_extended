package cimb.niaga.app.billsplit.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.CameraActivity;

/**
 * Created by 8ldavid on 1/19/2017.
 */

public class AddNewItemBillFragment extends DialogFragment {
    Button btn_additem;
    EditText itemname, price;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_item_bill, container, false);
        getDialog().setTitle("Owed Detail");

        btn_additem = (Button) view.findViewById(R.id.btn_additem);
        itemname = (EditText) view.findViewById(R.id.editItem);
        price = (EditText) view.findViewById(R.id.editPrice);

        btn_additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFragment callingActivity = new CameraFragment();
                getDialog().dismiss();
                callingActivity.getItem(itemname.getText().toString(), price.getText().toString());

            }
        });

        return view;
    }

}

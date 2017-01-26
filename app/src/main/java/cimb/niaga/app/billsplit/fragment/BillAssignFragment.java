package cimb.niaga.app.billsplit.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.HomeActivity;
import cimb.niaga.app.billsplit.activities.LoginActivity;
import cimb.niaga.app.billsplit.adapter.BillAssignAdapter;
import cimb.niaga.app.billsplit.corecycle.FormatCurrency;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import dmax.dialog.SpotsDialog;

/**
 * Created by 8ldavid on 1/19/2017.
 */

public class BillAssignFragment extends Fragment {
    String tax, service, img_url;
    ListView sourceList;
    BillAssignAdapter billAssignAdapter;
    Button addPerson;
    ImageView img;
    LinearLayout listPerson;
    SpotsDialog dialog;

    public static ArrayList<String> list_nick = new ArrayList<String>();
    public static ArrayList<String> list_phone = new ArrayList<String>();
    public static ArrayList<String> list_id = new ArrayList<String>();

    String[] conv_price;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bill_assign, container, false);

        Bundle bundle           = this.getArguments();
        tax                     = bundle.getString("tax");
        service                 = bundle.getString("service");
        img_url                 = bundle.getString("img_url");

        sourceList = (ListView) view.findViewById(R.id.sourcelist);
        addPerson = (Button) view.findViewById(R.id.btn_submit);
        listPerson = (LinearLayout) view.findViewById(R.id.newperson_layout);
        img = (ImageView) view.findViewById(R.id.img_bill);

        conv_price = new String[CameraFragment.scanned_price.size()];
        conv_price = CameraFragment.scanned_price.toArray(conv_price);

        Picasso.with(getActivity()).load(img_url).into(img);

//        billAssignAdapter = new BillAssignAdapter(getActivity());
//        sourceList.setAdapter(billAssignAdapter);

        sourceList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, conv_price));
        sourceList.setOnItemLongClickListener(listSourceItemLongClickListener);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.fragment_add_person);
                dialog.setTitle("Add New Item");

                final EditText nick = (EditText) dialog.findViewById(R.id.edit_nick);
                final EditText phone = (EditText) dialog.findViewById(R.id.edit_phone);

                Button btn_addperson = (Button) dialog.findViewById(R.id.btnDone);
                btn_addperson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        regisUser(nick.getText().toString(), phone.getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();


            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    public void getPerson(String nick, String phone) {
        // TODO add your implementation.

        Log.d("denny", nick + " phone : " + phone);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv = new TextView(getActivity());
//        if(tv.getParent()!=null) {
//            ((ViewGroup) tv.getParent()).removeView(tv);
//        }
        tv.setText(nick + "   Total : " + FormatCurrency.CurrencyIDR("0"));
        tv.setLayoutParams(params);
//        newitem_layout.addView(tv);

        try{
            listPerson.addView(tv);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void regisUser(String nick, String phone)
    {
        Log.d("denny", nick + " phone : " + phone);

        dialog = new SpotsDialog(getActivity(), R.style.LoginDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        params.put("nickname", nick);
        params.put("username", phone);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        Log.d("denny", params.toString());

        client.post(MyAPIClient.LINK_REGISTER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String error_code = object.getString("errorCode");

                    if(error_code.equals("00"))
                    {

                        list_nick.add(object.getString("nickname"));
                        list_phone.add(object.getString("username"));
                        list_id.add(object.getString("id"));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

                        EditText tv = new EditText(getActivity());
                        tv.setText(object.getString("nickname") + "   Total : " + FormatCurrency.CurrencyIDR("0"));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                        tv.setLayoutParams(params);

                        try{
                            listPerson.addView(tv);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                    else
                    {
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");

                dialog.dismiss();
            }
        });
    }

    AdapterView.OnItemLongClickListener listSourceItemLongClickListener
            = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> l, View v,
                                       int position, long id) {

            //Selected item is passed as item in dragData

            ClipData.Item item = new ClipData.Item(conv_price[position]);

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)v.getTag(), clipDescription, item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);

            v.startDrag(dragData, //ClipData
                    myShadow,  //View.DragShadowBuilder
                    conv_price[position],  //Object myLocalState
                    0);    //flags


            return true;
        }};

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public MyDragShadowBuilder(View v) {
            super(v);
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch){
            int width = getView().getWidth();
            int height = getView().getHeight();

            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                Toast.makeText(getActivity(), "Summary Page", Toast.LENGTH_LONG);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

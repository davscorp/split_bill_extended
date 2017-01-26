package cimb.niaga.app.billsplit.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.CameraActivity;
import cimb.niaga.app.billsplit.adapter.OwedAdapter;
import cimb.niaga.app.billsplit.adapter.ScannedPriceAdapter;
import cimb.niaga.app.billsplit.corecycle.FormatCurrency;
import cimb.niaga.app.billsplit.corecycle.GeneralizeImage;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import cimb.niaga.app.billsplit.corecycle.MyPicasso;
import cimb.niaga.app.billsplit.corecycle.RoundImageTransformation;
import dmax.dialog.SpotsDialog;

/**
 * Created by Denny on 1/20/2017.
 */

public class CameraFragment extends Fragment {
    Uri mCapturedImageURI;
    private final int RESULT_CAMERA = 200;
    public static final int RESULT_OK   = -1;
    ImageView img_bill;
    SpotsDialog dialog;
    Button add_items;
    LinearLayout targetLayout;
    ListView listSource, lvitem, lvprice;
    FragmentManager fragmentManager;
    LinearLayout newitem_layout;
    ScannedPriceAdapter scannedPriceAdapter;
    EditText editTax, editservice;
    String img_url;

    String[] month ={
            "50.000",
            "45.000",
            "6.000",
            "7.000"};

    String commentMsg;

    public static ArrayList<String> new_item = new ArrayList<String>();
    public static ArrayList<String> new_price = new ArrayList<String>();
    public static ArrayList<String> scanned_price = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        newitem_layout = (LinearLayout) view.findViewById(R.id.newitem_layout);
        img_bill = (ImageView) view.findViewById(R.id.img_bill);
        listSource = (ListView) view.findViewById(R.id.sourcelist);
        add_items = (Button) view.findViewById(R.id.btn_submit);
        editTax = (EditText) view.findViewById(R.id.editTax);
        editservice = (EditText) view.findViewById(R.id.editservice);

        scannedPriceAdapter = new ScannedPriceAdapter(getActivity());

        String fileName = "SplitBill.jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);

        mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, RESULT_CAMERA);

        add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.fragment_add_new_item_bill);
                dialog.setTitle("Add New Item");

                final EditText itemname = (EditText) dialog.findViewById(R.id.editItem);
                final EditText price = (EditText) dialog.findViewById(R.id.editPrice);

                Button btn_additem = (Button) dialog.findViewById(R.id.btn_additem);
                btn_additem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getItem(itemname.getText().toString(), price.getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        listSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.fragment_edit_scanned_price);
                dialog.setTitle("Edit Price");

                final EditText edit_scanned_price = (EditText) dialog.findViewById(R.id.edit_scanned_price);
                edit_scanned_price.setText(scanned_price.get(position));
                Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scanned_price.set(position, edit_scanned_price.getText().toString());
                        scannedPriceAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RESULT_CAMERA:
                if(resultCode == RESULT_OK && mCapturedImageURI!=null){
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(mCapturedImageURI, projection, null, null, null);
                    cursor.moveToFirst();
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String filePath = cursor.getString(column_index_data);

                    File photoFile = new File(filePath);
                    final GeneralizeImage mGI = new GeneralizeImage(getActivity(),filePath);
                    uploadFile(mGI.Convert());
                    //getOrientationImage();
                    img_bill.setVisibility(View.VISIBLE);
                    setImageProfPic(photoFile);
                }
                else{
                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void setImageProfPic(File filenya){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.user_unknown_menu);
        RoundImageTransformation roundedImage = new RoundImageTransformation(bm);

        Picasso mPic;
        if(MyAPIClient.PROD_FLAG_ADDRESS)
            mPic = MyPicasso.getImageLoader(getActivity());
        else
            mPic= Picasso.with(getActivity());

        if(!filenya.exists()){
            mPic.load(R.mipmap.user_unknown_menu)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(roundedImage)
                    .fit().centerInside()
//                    .placeholder(R.anim.progress_animation)
                    .transform(new RoundImageTransformation(getActivity())).into(img_bill);
            Bitmap myBitmap = BitmapFactory.decodeFile(filenya.getAbsolutePath());
            img_bill.setImageBitmap(myBitmap);
        }
        else {
            Bitmap myBitmap = BitmapFactory.decodeFile(filenya.getAbsolutePath());
            img_bill.setImageBitmap(myBitmap);
        }

    }

    AdapterView.OnItemLongClickListener listSourceItemLongClickListener
            = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> l, View v,
                                       int position, long id) {

            //Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(month[position]);

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)v.getTag(),
                    clipDescription,
                    item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);

            v.startDrag(dragData, //ClipData
                    myShadow,  //View.DragShadowBuilder
                    month[position],  //Object myLocalState
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

                for(int x = 0; x < new_price.size(); x++)
                {
                    scanned_price.add(new_price.get(x));
                }

                Log.d("denny", scanned_price.toString());
                Fragment newFragment = null;
                newFragment = new BillAssignFragment();
                Bundle args = new Bundle();
                args.putString("tax", editTax.getText().toString());
                args.putString("service", editservice.getText().toString());
                args.putString("img_url", img_url);
                newFragment.setArguments(args);
                switchFragment(newFragment);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void uploadFile(File photoFile) {
        dialog = new SpotsDialog(getActivity(), R.style.UploadDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        try {
            params.put("image", photoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("denny", params.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        client.post(MyAPIClient.LINK_UPLOADER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

//                try {
//                    JSONObject object = response;

//                    String img_url = object.getString("imgPublicUrl");
                img_url = "https://storage.googleapis.com/mdenny-bucket123/1484656960352struk1.jpg";


                if(!img_url.equals(""))
                {
                    dialog.dismiss();
                    callVisionAPI(img_url);
                }


//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");
            }
        });
    }

    public void callVisionAPI(String imgurl)
    {
        dialog = new SpotsDialog(getActivity(), R.style.ScanDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        params.put("img", imgurl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        Log.d("denny", params.toString());

        client.get(MyAPIClient.LINK_OCR, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    JSONArray result = object.getJSONArray("textDetectionResult");

                    for(int x = 0; x < result.length(); x++)
                    {
                        scanned_price.add(result.getJSONObject(x).getString("number"));
                    }

                    if (scanned_price.size() > 0) {
                        dialog.dismiss();
//                        txtAlert.setVisibility(View.GONE);

                        listSource.setVisibility(View.VISIBLE);
                        listSource.setAdapter(scannedPriceAdapter);
                    } else {
                        dialog.dismiss();
//                        txtAlert.setVisibility(View.VISIBLE);
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

    public void getItem(String itemname, String price) {
        // TODO add your implementation.

        Log.d("denny", itemname + " harga : " + price);

        new_item.add(itemname);
        new_price.add(price);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv = new TextView(getActivity());
//        if(tv.getParent()!=null) {
//            ((ViewGroup) tv.getParent()).removeView(tv);
//        }
        tv.setText(itemname + "   Price : " + FormatCurrency.CurrencyIDR(price));
        tv.setLayoutParams(params);
//        newitem_layout.addView(tv);

        try{
            newitem_layout.addView(tv);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;
        CameraActivity main = (CameraActivity) getActivity();
        main.switchContent(fragment,"Camera Fragment",true);
    }
}

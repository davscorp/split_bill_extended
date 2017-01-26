package cimb.niaga.app.billsplit.activities;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
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
import cimb.niaga.app.billsplit.adapter.ScannedPriceAdapter;
import cimb.niaga.app.billsplit.corecycle.FormatCurrency;
import cimb.niaga.app.billsplit.corecycle.GeneralizeImage;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import cimb.niaga.app.billsplit.corecycle.MyPicasso;
import cimb.niaga.app.billsplit.corecycle.RoundImageTransformation;
import cimb.niaga.app.billsplit.fragment.AddNewItemBillFragment;
import cimb.niaga.app.billsplit.fragment.BillAssignFragment;
import cimb.niaga.app.billsplit.fragment.CameraFragment;
import cimb.niaga.app.billsplit.fragment.HomeFragment;
import dmax.dialog.SpotsDialog;

public class CameraActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    MaterialSheetFab materialSheetFab;
    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        if (findViewById(R.id.content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            CameraFragment camera = new CameraFragment();
            fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, camera, "camera");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void switchContent(Fragment mFragment, String fragName, Boolean isBackstack) {

        if(isBackstack){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .commit();

        }

    }
}

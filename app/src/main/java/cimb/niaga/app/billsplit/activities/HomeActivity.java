package cimb.niaga.app.billsplit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.corecycle.Fab;
import cimb.niaga.app.billsplit.fragment.HomeFragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Denny on 1/10/2017.
 */

public class HomeActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    MaterialSheetFab materialSheetFab;
    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        if (findViewById(R.id.content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            HomeFragment home = new HomeFragment();
            fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, home, "My_Home");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        setupFab();
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

    private void setupFab()
    {
        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.theme_accent);

        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                setStatusBarColor(getResources().getColor(R.color.fab_color));
            }

            @Override
            public void onHideSheet() {
                setStatusBarColor(getResources().getColor(R.color.fab_color));
            }
        });

        findViewById(R.id.fab_sheet_item_recording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CameraActivity.class);
                Toast.makeText(getApplication(), "Opening Camera..", Toast.LENGTH_LONG).show();
                startActivity(i);
                materialSheetFab.hideSheet();
            }
        });
        findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(HomeActivity.this, OCRActivity.class);
                Toast.makeText(getApplication(), "Creating Bill..", Toast.LENGTH_LONG).show();
//                startActivity(i);
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        if (getApplication() == null)
            return;
        HomeActivity main = this;
        main.switchContent(fragment,"Camera Fragment",true);
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to quit?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setCancelClickListener(null)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();
            }

        return super.onKeyDown(keyCode, event);
    }

}

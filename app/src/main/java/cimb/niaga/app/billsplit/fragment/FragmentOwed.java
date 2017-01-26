package cimb.niaga.app.billsplit.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.HomeActivity;
import cimb.niaga.app.billsplit.activities.LoginActivity;
import cimb.niaga.app.billsplit.adapter.OwedAdapter;
import cimb.niaga.app.billsplit.corecycle.FormatCurrency;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;

/**
 * Created by Denny on 1/11/2017.
 */

public class FragmentOwed extends Fragment {
    private ProgressDialog pDialog;
    ProgressBar prgLoading;
    ListView listEvent;
    TextView txtAlert, txt_total_owed;
    OwedAdapter owedAdapter;
    int total = 0;

    public static ArrayList<String> owed_event = new ArrayList<String>();
    public static ArrayList<String> owed_price = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.owed_fragment, container, false);

        prgLoading = (ProgressBar) view.findViewById(R.id.prgLoading);
        listEvent = (ListView) view.findViewById(R.id.listEvent);
        txtAlert = (TextView) view.findViewById(R.id.txtAlert);
        txt_total_owed = (TextView) view.findViewById(R.id.txt_total_owed);

        owedAdapter = new OwedAdapter(getActivity());

        getList();

        listEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FragmentManager fm = getFragmentManager();
                FragmentOwedDetail dialogFragment = new FragmentOwedDetail ();
                dialogFragment.show(fm, "Owed Detail");

            }
        });

        return view;
    }

    public void getList() {
        clearData();

        RequestParams params = new RequestParams();
        params.put("username", MyAPIClient.username);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        client.post(MyAPIClient.LINK_DASHBOARDOWED, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String error_code = object.getString("errorCode");

                    JSONArray result = object.getJSONArray("bills");

                    if(error_code.equals("00"))
                    {
                        for (int a = 0; a < result.length(); a++) {
                            owed_event.add(result.getJSONObject(a).getString("description"));
                            owed_price.add(result.getJSONObject(a).getString("total"));
                        }

                        total = 0;
                        for(int x = 0; x < owed_price.size(); x++)
                        {
                            total += Integer.parseInt(owed_price.get(x));
                        }

                        txt_total_owed.setText(FormatCurrency.CurrencyIDR(Integer.toString(total)));

                        prgLoading.setVisibility(View.GONE);
                        if (owed_event.size() > 0) {
                            prgLoading.setVisibility(View.GONE);
                            txtAlert.setVisibility(View.GONE);
                            listEvent.setVisibility(View.VISIBLE);
                            listEvent.setAdapter(owedAdapter);
                        } else {
                            prgLoading.setVisibility(View.GONE);
                            txtAlert.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");
                prgLoading.setVisibility(View.GONE);
                txtAlert.setVisibility(View.VISIBLE);
            }
        });
    }

    void clearData(){
        owed_event.clear();
        owed_price.clear();
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;
        HomeActivity main = (HomeActivity) getActivity();
        main.switchContent(fragment,"Owed",true);
    }
}

package com.upplic.androidapp.upplictesttask.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.upplic.androidapp.upplictesttask.R;
import com.upplic.androidapp.upplictesttask.datasets.CityInfo;
import com.upplic.androidapp.upplictesttask.datasets.TestJsonObject;
import com.upplic.androidapp.upplictesttask.utils.AppConstants;
import com.upplic.androidapp.upplictesttask.utils.JsonUtils;
import com.upplic.androidapp.upplictesttask.utils.NetUtils;
import com.upplic.androidapp.upplictesttask.utils.RetainedFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "QM_" + MainActivity.class.getSimpleName();

    private Context mContext;
    private DrawerLayout drawer;

    private RetainedDataObject retainedDataObject;

    private final MainHandler mHandler = new MainHandler(this);
    private static class MainHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MainHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case AppConstants.MESSAGE_HTTP_RESPONSE_SUCCESS:
                        activity.setDataObjectTryingToConnect(false);
                        activity.closeDataObjectProgressDialog();
                        activity.parseResponseOnSuccess((byte[]) msg.obj);
                        break;
                    case AppConstants.MESSAGE_HTTP_RESPONSE_FAILURE:
                        activity.setDataObjectTryingToConnect(false);
                        activity.closeDataObjectProgressDialog();
                        activity.parseResponseOnFailure((Throwable) msg.obj);
                        break;
                }
            }
        }
    }

    private class RetainedDataObject {
        ArrayList<CityInfo> cityInfoList;
        boolean tryingToConnect;
        boolean networkIsOn;
        String error_message;
        ProgressDialog progressDialog;

        public RetainedDataObject(boolean tryingToConnect, boolean networkIsOn, String error_message, ProgressDialog progressDialog) {
            this.cityInfoList = new ArrayList<>();
            this.tryingToConnect = tryingToConnect;
            this.networkIsOn = networkIsOn;
            this.error_message = error_message;
            this.progressDialog = progressDialog;
        }

        @Override
        public String toString() {
            return cityInfoList + ": " + tryingToConnect + ": " + networkIsOn + ": " + error_message;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        FragmentManager fm = getSupportFragmentManager();
        RetainedFragment dataFragment = (RetainedFragment) fm.findFragmentByTag("data");
        if (null == dataFragment) {
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            retainedDataObject = new RetainedDataObject(false, false, "Data not yet received", createProcessDialog());
            dataFragment.setData(retainedDataObject);
            if (checkConnectionStatus()) {
                getDataFromService();
            }
        } else {
            retainedDataObject = (RetainedDataObject) dataFragment.getData();
            retainedDataObject.progressDialog = createProcessDialog();

            if (retainedDataObject.tryingToConnect && !retainedDataObject.progressDialog.isShowing()) {
                retainedDataObject.progressDialog.show();
            }
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (!retainedDataObject.networkIsOn) {
            checkConnectionStatus();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (retainedDataObject.tryingToConnect && !retainedDataObject.progressDialog.isShowing()) {
            retainedDataObject.progressDialog.show();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (null != retainedDataObject.error_message) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Data error")
                    .setMessage(retainedDataObject.error_message)
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getDataFromService();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (id == R.id.nav_sort_by_alphabetical) {
            Intent intent = new Intent(MainActivity.this, ListSortAlphabeticalActivity.class);
            intent.putExtra(AppConstants.DATA_PARAM_NAME, retainedDataObject.cityInfoList);
            startActivity(intent);
        } else if (id == R.id.nav_sort_by_value) {
            Intent intent = new Intent(MainActivity.this, ListSortCountActivity.class);
            intent.putExtra(AppConstants.DATA_PARAM_NAME, retainedDataObject.cityInfoList);
            startActivityForResult(intent, AppConstants.RESULT_CODE_ACTIVITY_LIST_COUNT);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void parseResponseOnSuccess(byte[] response) {
        if (null == response) {
            retainedDataObject.error_message = "Null response";
            return;
        }

        String jsonString;
        boolean IM_NOT_KNOW_VALID_SERVICE_WITH_CORRECT_JSON = true;
        if (IM_NOT_KNOW_VALID_SERVICE_WITH_CORRECT_JSON) {
            //Use json string from resource
            jsonString = getString(R.string.test_json);
        } else {
            //byte array to valid json string. Now stub is on
            jsonString = new String(response);
        }

        boolean strictFieldsValidation = true;
        HashMap<Integer, Object> result = JsonUtils.getJsonArray(jsonString, CityInfo.getFieldsMap(), CityInfo.class.getCanonicalName(), strictFieldsValidation);
        //HashMap<String, Object> result = JsonUtils.getJsonArray(jsonString, TestJsonObject.getFieldsMap(), TestJsonObject.class.getCanonicalName(), strictFieldsValidation);
        //HashMap<String, Object> result = JsonUtils.getJsonItem(jsonString, TestJsonObject.getFieldsMap(), TestJsonObject.class.getCanonicalName(), strictFieldsValidation);

        if (null != result.get(AppConstants.MAP_KEY_DATA)) {
            Log.d(LOG_TAG, "parseResponseOnSuccess result = " + result);
            try {
                retainedDataObject.cityInfoList = (ArrayList<CityInfo>) result.get(AppConstants.MAP_KEY_DATA);
                if (retainedDataObject.cityInfoList.size() == 0) {
                    retainedDataObject.error_message = "Empty city list from response";
                } else {
                    retainedDataObject.error_message = null;
                }
            } catch (Exception e) {
                retainedDataObject.error_message = "Unexpected result! " + e;
            }

        } else if (null != result.get(AppConstants.MAP_KEY_FAILURE)) {
            Throwable throwable = (Throwable) result.get(AppConstants.MAP_KEY_FAILURE);
            retainedDataObject.error_message = throwable + "";
            if (null != result.get(AppConstants.MAP_KEY_FAILURE_ADD)) {
                retainedDataObject.error_message += "\n.Additionally - " + result.get(AppConstants.MAP_KEY_FAILURE_ADD);
            }

        } else {
            retainedDataObject.error_message = "Achtung!!! Result is null!!!";
        }
    }

    public void parseResponseOnFailure(Throwable throwable) {
        retainedDataObject.error_message = "By: " + throwable.getClass() + ". MSG : " + throwable.getLocalizedMessage();

        //TODO comment method(parseResponseOnSuccess) call - must working fine            !!!!!!!!!!!
        //String testJsonArray = "[{\"time\": \"07:32:02 AM\",\"milliseconds_since_epoch\": 1462519922085,\"date\": \"05-06-2016\"},{\"time\": \"11:33 AM\",\"milliseconds_since_epoch\": 85,\"date\": \"01-01-2016\"}]";
        //String testJsonItem = "{\"time\": \"07:32:02 AM\",\"milliseconds_since_epoch\": 1462519922085,\"date\": \"05-06-2016\"}";
        //parseResponseOnSuccess(testJsonArray.getBytes());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.RESULT_CODE_ACTIVITY_LIST_COUNT && resultCode == Activity.RESULT_OK) {
            retainedDataObject.cityInfoList = data.getParcelableArrayListExtra(AppConstants.DATA_PARAM_NAME);
        }
    }

    private boolean checkConnectionStatus() {
        //TODO uncomment block - must working fine            !!!!!!!!!!!
        if (NetUtils.getConnectivityStatus(mContext) == NetUtils.TYPE_NOT_CONNECTED) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Network info")
                    .setMessage("Please turn on network connection")
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkConnectionStatus();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        } else {
            retainedDataObject.networkIsOn = true;
            return true;
        }

    }

    private void getDataFromService() {
        setDataObjectTryingToConnect(true);
        retainedDataObject.progressDialog.show();
        NetUtils.getAsyncHttp(AppConstants.STUB_URL, null, mHandler);
    }

    protected ProgressDialog createProcessDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Retrieving data...");

        return progressDialog;
    }

    private void closeDataObjectProgressDialog() {
        retainedDataObject.progressDialog.dismiss();
    }

    private void setDataObjectTryingToConnect(boolean value) {
        retainedDataObject.tryingToConnect = value;
    }

}

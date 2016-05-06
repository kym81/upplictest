package com.upplic.androidapp.upplictesttask.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.upplic.androidapp.upplictesttask.R;
import com.upplic.androidapp.upplictesttask.adapters.ListByAlphabeticalAndCountAdapter;
import com.upplic.androidapp.upplictesttask.datasets.CityInfo;
import com.upplic.androidapp.upplictesttask.utils.AppConstants;
import com.upplic.androidapp.upplictesttask.utils.CityInfoComparator;
import com.upplic.androidapp.upplictesttask.utils.DisplaySizeGetter;
import com.upplic.androidapp.upplictesttask.utils.NetUtils;
import com.upplic.androidapp.upplictesttask.utils.RetainedFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ListSortCountActivity extends AppCompatActivity {
    private RetainedDataObject retainedDataObject;
    private Context mContext;

    private final MainHandler mHandler = new MainHandler(this);
    private static class MainHandler extends Handler {
        private final WeakReference<ListSortCountActivity> mActivity;

        public MainHandler(ListSortCountActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ListSortCountActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case AppConstants.MESSAGE_HTTP_RESPONSE_SUCCESS:
                        //TODO method on success
                        break;
                    case AppConstants.MESSAGE_HTTP_RESPONSE_FAILURE:
                        //TODO method on failure
                        break;
                }
            }
        }
    }

    private class RetainedDataObject {
        ArrayList<CityInfo> cityInfoList;
        ArrayList<CityInfo> changedList;
        boolean mustSaveResult;

        public RetainedDataObject(ArrayList<CityInfo> cityInfoList, ArrayList<CityInfo> changedList, boolean mustSaveResult) {
            this.cityInfoList = cityInfoList;
            this.changedList = changedList;
            this.mustSaveResult = mustSaveResult;
        }

        @Override
        public String toString() {
            return cityInfoList + ": " + changedList + ": " + mustSaveResult;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_list);

        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        RetainedFragment dataFragment = (RetainedFragment) fm.findFragmentByTag("data");
        if (null == dataFragment) {
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            retainedDataObject = new RetainedDataObject(null, null, false);
            dataFragment.setData(retainedDataObject);
        } else {
            retainedDataObject = (RetainedDataObject) dataFragment.getData();
        }

        if (retainedDataObject.cityInfoList == null) {
            retainedDataObject.cityInfoList = getIntent().getExtras().getParcelableArrayList(AppConstants.DATA_PARAM_NAME);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_list);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("param_key_1", "do_some");
                paramsMap.put("json", "some valid json str");

                retainedDataObject.mustSaveResult = true;

                retainedDataObject.changedList = new ArrayList<>(retainedDataObject.cityInfoList.size());
                for(CityInfo item: retainedDataObject.cityInfoList) {
                    retainedDataObject.changedList.add(new CityInfo(item));
                }

                String msg;
                if (NetUtils.getConnectivityStatus(mContext) == NetUtils.TYPE_NOT_CONNECTED) {
                    Toast.makeText(mContext, "Please turn on network connection", Toast.LENGTH_SHORT).show();
                    msg = "Data saved";
                } else {
                    NetUtils.postAsyncHttp(AppConstants.STUB_URL, paramsMap, mHandler);
                    msg = "Data saved and sent";
                }
                Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        Collections.sort(retainedDataObject.cityInfoList, new CityInfoComparator(CityInfoComparator.Field.COUNT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(DisplaySizeGetter.getWidth(this) / 10);
    }

    private void updateList(int mX) {
        boolean showSelectCB = true;
        ListByAlphabeticalAndCountAdapter listByAlphabeticalAndCountAdapter = new ListByAlphabeticalAndCountAdapter(this, retainedDataObject.cityInfoList, showSelectCB, mX);

        ListView listView = (ListView) findViewById(R.id.sorted_LV);
        listView.setAdapter(listByAlphabeticalAndCountAdapter);
    }

    @Override
    public void onBackPressed() {
        if (retainedDataObject.mustSaveResult) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConstants.DATA_PARAM_NAME, retainedDataObject.changedList);
            setResult(Activity.RESULT_OK, returnIntent);
        }

        super.onBackPressed();
    }
}

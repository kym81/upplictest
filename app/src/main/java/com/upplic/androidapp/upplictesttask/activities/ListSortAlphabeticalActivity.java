package com.upplic.androidapp.upplictesttask.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.upplic.androidapp.upplictesttask.R;
import com.upplic.androidapp.upplictesttask.adapters.ListByAlphabeticalAndCountAdapter;
import com.upplic.androidapp.upplictesttask.datasets.CityInfo;
import com.upplic.androidapp.upplictesttask.utils.AppConstants;
import com.upplic.androidapp.upplictesttask.utils.CityInfoComparator;
import com.upplic.androidapp.upplictesttask.utils.DisplaySizeGetter;

import java.util.ArrayList;
import java.util.Collections;

public class ListSortAlphabeticalActivity extends AppCompatActivity {
    private ArrayList<CityInfo> cityInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityInfoList = getIntent().getExtras().getParcelableArrayList(AppConstants.DATA_PARAM_NAME);
        Collections.sort(cityInfoList, new CityInfoComparator(CityInfoComparator.Field.NAME));
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
        boolean showSelectCB = false;
        ListByAlphabeticalAndCountAdapter listByAlphabeticalAndCountAdapter = new ListByAlphabeticalAndCountAdapter(this, cityInfoList, showSelectCB, mX);

        ListView listView = (ListView) findViewById(R.id.sorted_LV);
        listView.setAdapter(listByAlphabeticalAndCountAdapter);
    }
}

package com.upplic.androidapp.upplictesttask.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upplic.androidapp.upplictesttask.R;
import com.upplic.androidapp.upplictesttask.datasets.CityInfo;

import java.util.List;

public class ListByAlphabeticalAndCountAdapter extends ArrayAdapter<CityInfo> {

    private final boolean is_select_enabled;
    private final int mX;

    private class VHolder {
        private TextView item_name_TV, item_desc_TV, item_count_TV;
        private CheckBox item_select_CB;
    }

    public ListByAlphabeticalAndCountAdapter(Context mContext, List<CityInfo> list, boolean is_select_enabled, int mX) {
        super(mContext, R.layout.list_item, R.id.item_name_TV, list);
        this.is_select_enabled = is_select_enabled;
        this.mX = mX;
    }

    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view != convertView && view != null) {
            VHolder holder = new VHolder();

            holder.item_name_TV = (TextView) view.findViewById(R.id.item_name_TV);
            holder.item_desc_TV = (TextView) view.findViewById(R.id.item_desc_TV);
            holder.item_count_TV = (TextView) view.findViewById(R.id.item_count_TV);

            if (is_select_enabled) {
                holder.item_select_CB = (CheckBox) view.findViewById(R.id.item_select_CB);
                holder.item_select_CB.setTag(position);
                holder.item_select_CB.setVisibility(View.VISIBLE);
                holder.item_select_CB.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        getIsSelected((int) view.getTag()).setSelected(cb.isChecked());
                    }
                });
                ((LinearLayout) holder.item_name_TV.getParent()).getLayoutParams().width = mX * 2;
                holder.item_count_TV.getLayoutParams().width = mX * 3;
            } else {
                ((LinearLayout) holder.item_name_TV.getParent()).getLayoutParams().width = mX * 4;
                holder.item_count_TV.getLayoutParams().width = mX * 4;
            }

            view.setTag(holder);
        } else {
            view = convertView;
            if (is_select_enabled) {
                ((VHolder) view.getTag()).item_select_CB.setTag(position);
            }

        }

        VHolder holder = (VHolder) view.getTag();

        String name = getItem(position).getName();
        String desc = getItem(position).getDescription();
        int count = getItem(position).getCount();

        holder.item_name_TV.setText(name);
        holder.item_desc_TV.setText(desc);
        holder.item_count_TV.setText("Count = " + count);

        if (is_select_enabled) {
            holder.item_select_CB.setChecked(getItem(position).is_selected());
        }

        return view;
    }

    private CityInfo getIsSelected(int position) {
        return getItem(position);
    }
}

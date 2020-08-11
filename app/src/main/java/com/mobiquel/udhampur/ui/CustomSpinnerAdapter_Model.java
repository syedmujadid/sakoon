package com.mobiquel.udhampur.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.LocationListModel;

import java.util.List;

public class CustomSpinnerAdapter_Model extends BaseAdapter {
    Context context;
    List<LocationListModel> data;
    LayoutInflater inflter;

    public CustomSpinnerAdapter_Model(Context applicationContext, List<LocationListModel> data) {
        this.context = applicationContext;
        this.data = data;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_item_spinner, null);

        TextView names = (TextView) view.findViewById(R.id.text);
        names.setText(data.get(i).getLocationName());
        return view;
    }
}

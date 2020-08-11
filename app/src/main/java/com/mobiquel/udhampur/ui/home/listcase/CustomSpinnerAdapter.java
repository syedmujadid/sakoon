package com.mobiquel.udhampur.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.LocationListModel;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> name;
    ArrayList<String> amt;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, ArrayList<String> name,ArrayList<String> amount) {
        this.context = applicationContext;
        this.name = name;
        this.amt=amount;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return name.size();
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
        TextView amount = (TextView) view.findViewById(R.id.amount);
        names.setText(name.get(i));
        amount.setText("Amount allocated: \u20B9 "+amt.get(i));
        return view;
    }
}

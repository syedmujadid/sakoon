package com.mobiquel.udhampur.ui.home.listcase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.OfficialsListModel;

import java.util.List;

public class ListOfOfficialsAdapter extends RecyclerView.Adapter<ListOfOfficialsAdapter.OptionsViewHolder> {

    private Context context;
    private List<OfficialsListModel> collegeList;

    public ListOfOfficialsAdapter(List<OfficialsListModel> collegeList) {
        this.collegeList = collegeList;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_logs_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, final int position) {

        holder.comment2.setText(collegeList.get(position).getName());
        holder.name.setText("Mobile No: " + collegeList.get(position).getMobile());
        holder.mobile.setText("Designation: " + collegeList.get(position).getDesignation());
        holder.level.setText("Village: " + collegeList.get(position).getVillageName());

        if (collegeList.get(position).getVillageName().equals(""))
            holder.level.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            comment.setVisibility(View.GONE);
            comment2.setVisibility(View.VISIBLE);

        }

    }

}

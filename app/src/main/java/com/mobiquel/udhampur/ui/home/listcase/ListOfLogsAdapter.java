package com.mobiquel.udhampur.ui.home.listcase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.LogsListModel;

import java.util.List;

public class ListOfLogsAdapter extends RecyclerView.Adapter<ListOfLogsAdapter.OptionsViewHolder> {

    private Context context;
    private List<LogsListModel> collegeList;

    public ListOfLogsAdapter(List<LogsListModel> collegeList) {
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

        holder.name.setText(collegeList.get(position).getName());
        holder.comment.setText("Last updated by: "+collegeList.get(position).getComment());
        holder.mobile.setText("Mobile No: "+collegeList.get(position).getMobile());
        holder.level.setText("Officer Level: " + collegeList.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            }

    }

}

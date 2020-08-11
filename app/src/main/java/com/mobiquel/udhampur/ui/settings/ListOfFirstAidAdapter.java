package com.mobiquel.udhampur.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListOfFirstAidAdapter extends RecyclerView.Adapter<ListOfFirstAidAdapter.OptionsViewHolder> {


    private Context context;
    private JSONArray collegeList;
    private RecyclerItemClickListener clickListener, clickListener2;

    public ListOfFirstAidAdapter(JSONArray collegeList, RecyclerItemClickListener clickListener, RecyclerItemClickListener clickListener2) {
        this.collegeList = collegeList;
        this.clickListener = clickListener;
        this.clickListener2 = clickListener2;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.view_layout_first_aid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, final int position) {
        try {
            holder.id.setText("Incident Id #" + collegeList.getJSONObject(position).getString("incidentId"));
            if (collegeList.getJSONObject(position).getString("firstAidStatus").equals("PENDING")) {
                holder.status.setBackgroundResource(R.drawable.rectangle_background_orange);
                holder.approveRejectLayout.setVisibility(View.VISIBLE);
            } else if (collegeList.getJSONObject(position).getString("firstAidStatus").equals("REJECTED")) {
                holder.status.setBackgroundResource(R.drawable.rectangle_background_red);
                holder.approveRejectLayout.setVisibility(View.GONE);
            } else {
                holder.approveRejectLayout.setVisibility(View.GONE);
                holder.status.setBackgroundResource(R.drawable.rectangle_background_green);
            }
            holder.status.setText(collegeList.getJSONObject(position).getString("firstAidStatus"));
            holder.name.setText("Relief Claimant: " + collegeList.getJSONObject(position).getString("applicantName"));
            holder.mobile.setText("Relief Claimant Mobile: " + collegeList.getJSONObject(position).getString("applicantMobile"));
            holder.date.setText("Date of Incident: " + collegeList.getJSONObject(position).getString("incidentDate"));
            holder.village.setText("Village: " + collegeList.getJSONObject(position).getString("villageName"));
            holder.firstAid.setText(collegeList.getJSONObject(position).getString("firstAid"));
            holder.name2.setText("Patwari: " + collegeList.getJSONObject(position).getString("createdByName"));
            holder.mobile2.setText("Mobile: " + collegeList.getJSONObject(position).getString("mobile"));
            holder.createdOn.setText("Created On: " + collegeList.getJSONObject(position).getString("createdOn"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.length() : 0;
    }


    class OptionsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id)
        TextView id;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.mobile)
        TextView mobile;
        @BindView(R.id.firstAid)
        TextView firstAid;
        @BindView(R.id.name2)
        TextView name2;
        @BindView(R.id.mobile2)
        TextView mobile2;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.village)
        TextView village;
        @BindView(R.id.createdOn)
        TextView createdOn;
        @BindView(R.id.approveRejectLayout)
        LinearLayout approveRejectLayout;
        @BindView(R.id.reject)
        TextView reject;
        @BindView(R.id.approve)
        TextView approve;

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.approve, R.id.reject})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.approve:
                    if (clickListener != null)
                        clickListener.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.reject:
                    if (clickListener2 != null)
                        clickListener2.onRecyclerItemClicked(getAdapterPosition());
                    break;

            }
        }
    }

}

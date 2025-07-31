package com.mobiquel.udhampur.ui.settings;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;

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

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            }

        R.id.approve, R.id.reject
        private void onViewClicked(View view) {
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

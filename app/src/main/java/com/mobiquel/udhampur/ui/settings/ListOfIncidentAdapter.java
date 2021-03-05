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
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListOfIncidentAdapter extends RecyclerView.Adapter<ListOfIncidentAdapter.OptionsViewHolder> {


    private Context context;
    private JSONArray collegeList;

    public ListOfIncidentAdapter(JSONArray collegeList) {
        this.collegeList = collegeList;

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
            int j=position+1;
            holder.id.setText("Incident Id #" + collegeList.getJSONObject(position).getString("incidentId"));
            if (collegeList.getJSONObject(position).getString("status").equals("PENDING")) {
                holder.status.setBackgroundResource(R.drawable.rectangle_background_orange);
            } else if (collegeList.getJSONObject(position).getString("status").equals("REJECTED")) {
                holder.status.setBackgroundResource(R.drawable.rectangle_background_red);
            } else {
                holder.status.setBackgroundResource(R.drawable.rectangle_background_green);
            }

            if(collegeList.getJSONObject(position).getString("rejectReason").equals("null")){
                holder.firstAid.setVisibility(View.GONE);
                holder.firstAidLabel.setVisibility(View.GONE);
            }
            else
            {
                holder.firstAid.setVisibility(View.VISIBLE);
                holder.firstAidLabel.setVisibility(View.VISIBLE);
                holder.firstAidLabel.setText("Rejected due to: ");
                holder.firstAid.setText(collegeList.getJSONObject(position).getString("rejectReason"));
            }
            holder.status.setText(collegeList.getJSONObject(position).getString("status"));
            holder.name.setText("Reported By: " + collegeList.getJSONObject(position).getString("mobile"));
            holder.mobile.setText("Description: " + collegeList.getJSONObject(position).getString("description"));
            holder.date.setText("Offiicial Name: " + collegeList.getJSONObject(position).getString("officialName"));
            holder.village.setText("Designation: " + collegeList.getJSONObject(position).getString("officialDesignation"));
            holder.name2.setText("Mobile: " + collegeList.getJSONObject(position).getString("officialMobile"));
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
        @BindView(R.id.firstAidLabel)
        TextView firstAidLabel;
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
            approveRejectLayout.setVisibility(View.GONE);
            createdOn.setVisibility(View.GONE);
            mobile2.setVisibility(View.GONE);
        //    firstAid.setVisibility(View.GONE);
        //    firstAidLabel.setVisibility(View.GONE);
            name2.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        }

    }

}

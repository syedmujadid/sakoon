package com.mobiquel.udhampur.ui.home.listcase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.IssueListModel_Online;
import com.mobiquel.udhampur.utils.Preferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListOfCaseAdapter extends RecyclerView.Adapter<ListOfCaseAdapter.OptionsViewHolder> {


    private Context context;
    private List<IssueListModel_Online> collegeList;
    private RecyclerItemClickListener clickListener, clickListenerLogs, clickListenerOfficials, clickListenerDelete;

    public ListOfCaseAdapter(List<IssueListModel_Online> collegeList, RecyclerItemClickListener clickListener, RecyclerItemClickListener logs, RecyclerItemClickListener officials, RecyclerItemClickListener delete) {
        this.collegeList = collegeList;
        this.clickListener = clickListener;
        this.clickListenerLogs = logs;
        this.clickListenerOfficials = officials;
        this.clickListenerDelete = delete;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_case_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, final int position) {

        holder.caseId.setText("Incident Id #" + collegeList.get(position).getCaseId());
        if (collegeList.get(position).getCitizenDescription() != null && collegeList.get(position).getCitizenDescription().equals("")) {
            holder.name.setText("Relief Claimant: " + collegeList.get(position).getApplicantName());
            holder.incidentDate.setText("Date of Incident: " + collegeList.get(position).getIncidentDate());
            holder.reportedByCitizen.setVisibility(View.GONE);
        } else {
            if (Preferences.getInstance().level.equals("1") && collegeList.get(position).getIssueAtLevel().equals("1")) {
                holder.incidentDate.setText("Date of Incident: " + collegeList.get(position).getIncidentDate());
                holder.name.setText("Relief Claimant: " + collegeList.get(position).getApplicantName());
                holder.reportedByCitizen.setVisibility(View.VISIBLE);
            } else {
                holder.name.setText("Relief Claimant: " + collegeList.get(position).getApplicantName());
                holder.incidentDate.setText("Date of Incident: " + collegeList.get(position).getIncidentDate());
                holder.reportedByCitizen.setVisibility(View.GONE);
            }

        }

        holder.createdOn.setText("Created On: " + collegeList.get(position).getCreatedOn());
        if(collegeList.get(position).getIsApprovedEarlierCheck()!=null){
            if(Integer.parseInt(Preferences.getInstance().level)>=4){
                if(collegeList.get(position).getIsApprovedEarlierCheck().equals("T"))
                    holder.isApprovedEarlierCheck.setVisibility(View.VISIBLE);
                else
                    holder.isApprovedEarlierCheck.setVisibility(View.GONE);
            }
        }
        if (collegeList.get(position).getStatus().equals("OTHERPENDING") && Preferences.getInstance().level.equals("7")) {

            if (collegeList.get(position).getPendingDays() != null) {
                holder.daysCount.setVisibility(View.VISIBLE);
                holder.daysCount.setText(collegeList.get(position).getPendingDays());
                holder.daysCount.setTextColor(Color.parseColor("#000000"));

                ((GradientDrawable) holder.daysCount.getBackground()).setColor(Color.parseColor(collegeList.get(position).getPendingCode()));
            } else {
                holder.daysCount.setVisibility(View.GONE);
            }
        } else {

            holder.daysCount.setVisibility(View.GONE);
        }
        if (collegeList.get(position).getStatus().equals("PENDING")) {
            if (collegeList.get(position).getUpdatedBy() != null) {
                holder.levelLayout.setVisibility(View.VISIBLE);
                holder.statusLayout.setVisibility(View.VISIBLE);
                holder.currentLevel.setText(collegeList.get(position).getIssueAtLevel());
                if (collegeList.get(position).getIssueAtLevel().equals(Preferences.getInstance().level)) {
                    if (Preferences.getInstance().level.equals("1")) {
                        holder.delete2.setVisibility(View.VISIBLE);
                        if (collegeList.get(position).getCitizenDescription().equals(""))
                            holder.delete2.setText("DELETE");
                        else
                            holder.delete2.setText("CLICK TO REJECT");
                    } else
                        holder.delete2.setVisibility(View.GONE);

                    holder.status.setText("PENDING");
                    holder.status.setBackgroundResource(R.drawable.rectangle_background_yellow);
                } else {
                    holder.delete2.setVisibility(View.GONE);
                    holder.status.setText("PROCESSED");
                    holder.status.setBackgroundResource(R.drawable.rectangle_background_red);
                }
            }

        }
        /*else {
            if (Preferences.getInstance().level.equals("7")){

            }
        }*/

    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }


    class OptionsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.caseId)
        TextView caseId;
        @BindView(R.id.seeLogs)
        TextView seeLogs;
        @BindView(R.id.reportedByCitizen)
        TextView reportedByCitizen;
        @BindView(R.id.officialList)
        TextView officialList;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.incidentDate)
        TextView incidentDate;
        @BindView(R.id.createdOn)
        TextView createdOn;
        @BindView(R.id.isApprovedEarlierCheck)
        TextView isApprovedEarlierCheck;

        @BindView(R.id.delete2)
        TextView delete2;

        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.currentLevel)
        TextView currentLevel;

        @BindView(R.id.statusLayout)
        LinearLayout statusLayout;
        @BindView(R.id.levelLayout)
        LinearLayout levelLayout;

        @BindView(R.id.daysCount)
        TextView daysCount;
        @BindView(R.id.rl_main)
        RelativeLayout rlMain;

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Preferences.getInstance().loadPreferences(context);

        }

        @OnClick({R.id.rl_main, R.id.seeLogs, R.id.officialList, R.id.delete2})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_main:
                    if (clickListener != null)
                        clickListener.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.seeLogs:
                    if (clickListenerLogs != null)
                        clickListenerLogs.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.officialList:
                    if (clickListenerOfficials != null)
                        clickListenerOfficials.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.delete2:
                    if (clickListenerDelete != null)
                        clickListenerDelete.onRecyclerItemClicked(getAdapterPosition());
                    break;
            }
        }
    }

}

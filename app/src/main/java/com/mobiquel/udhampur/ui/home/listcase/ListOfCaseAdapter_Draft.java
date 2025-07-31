package com.mobiquel.udhampur.ui.home.listcase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.pojo.IssueListModel_Online;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListOfCaseAdapter_Draft extends RecyclerView.Adapter<ListOfCaseAdapter_Draft.OptionsViewHolder> {

    private Context context;
    private List<IssueListModel> collegeList;
    private RecyclerItemClickListener clickListener,clickListener_delete;

    public ListOfCaseAdapter_Draft(List<IssueListModel> collegeList, RecyclerItemClickListener clickListener,RecyclerItemClickListener clickListener_delete) {
        this.collegeList = collegeList;
        this.clickListener = clickListener;
        this.clickListener_delete = clickListener_delete;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_case_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, final int position) {

        holder.caseId.setText("Incident Id #"+collegeList.get(position).getCaseId());
        holder.name.setText("Relief Claimant: "+collegeList.get(position).getName());
        try {
            JSONObject issueDetail=new JSONObject(collegeList.get(position).getIssueDetails());
            holder.incidentDate.setText("Incident Date: "+issueDetail.getString("incidentDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.createdOn.setText("Created On: "+collegeList.get(position).getCreatedOn());
    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            seeLogs.setVisibility(View.GONE);
            officialList.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }

        R.id.rl_main,R.id.delete
        private void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_main:
                    if (clickListener != null)
                        clickListener.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.delete:
                    if (clickListener_delete != null)
                        clickListener_delete.onRecyclerItemClicked(getAdapterPosition());
                    break;
            }
        }
    }

}

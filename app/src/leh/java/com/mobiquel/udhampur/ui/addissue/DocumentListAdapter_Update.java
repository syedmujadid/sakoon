package com.mobiquel.udhampur.ui.addissue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.DocListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentListAdapter_Update extends RecyclerView.Adapter<DocumentListAdapter_Update.OptionsViewHolder> {


    private Context context;
    private List<DocListModel> collegeList;

    public DocumentListAdapter_Update(List<DocListModel> collegeList) {
        this.collegeList = collegeList;

    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_document_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, final int position) {

        holder.fileName.setText(collegeList.get(position).getName());
        if (collegeList.get(position).isOptionalStatus())
            holder.optionalStatus.setText("Optional");
        else
            holder.optionalStatus.setText("* Required");

        if (collegeList.get(position).isUploadStatus()) {
            holder.view.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
            holder.upload.setVisibility(View.GONE);
        } else {
            if(collegeList.get(position).getFileURL().equals("")){
                holder.view.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
                holder.upload.setVisibility(View.VISIBLE);
            }
            else {
                holder.view.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
                holder.upload.setVisibility(View.GONE);
            }
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    ((UpdateIssue) context).showImage(collegeList.get(position).getFileURL(), collegeList.get(position).getName());
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    ((UpdateIssue) context).showUploadDialog(collegeList.get(position).getName(),"EDIT",position);
            }
        });
        holder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   ((UpdateIssue) context).showUploadDialog(collegeList.get(position).getName(),"UPLOAD",position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }


    class OptionsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileName)
        TextView fileName;
        @BindView(R.id.optionalStatus)
        TextView optionalStatus;
        @BindView(R.id.upload)
        TextView upload;
        @BindView(R.id.view)
        TextView view;
        @BindView(R.id.edit)
        TextView edit;

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

}

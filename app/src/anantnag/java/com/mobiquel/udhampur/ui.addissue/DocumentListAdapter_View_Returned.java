package com.mobiquel.udhampur.ui.addissue;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.DocListModel;
import com.mobiquel.udhampur.utils.Preferences;

import java.util.List;

public class DocumentListAdapter_View_Returned extends RecyclerView.Adapter<DocumentListAdapter_View_Returned.OptionsViewHolder> {

    private Context context;
    private List<DocListModel> collegeList;

    public DocumentListAdapter_View_Returned(List<DocListModel> collegeList) {
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

        if (collegeList.get(position).getFileURL().equals("")) {
            holder.view.setVisibility(View.GONE);
            holder.na.setVisibility(View.VISIBLE);

        } else {
            holder.view.setVisibility(View.VISIBLE);
            holder.na.setVisibility(View.GONE);

        }
        /*if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("4") || Preferences.getInstance().level.equals("5")) {
            if (collegeList.get(position).isOptionalStatus())
                holder.optionalStatus.setText("Optional");
            else
                holder.optionalStatus.setText("* Required");

            if (collegeList.get(position).isUploadStatus()) {
                holder.view.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
                holder.upload.setVisibility(View.GONE);
            } else {
                if (collegeList.get(position).getFileURL().equals("")) {
                    holder.view.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);
                    holder.upload.setVisibility(View.VISIBLE);
                } else {
                    holder.view.setVisibility(View.VISIBLE);
                    holder.edit.setVisibility(View.VISIBLE);
                    holder.upload.setVisibility(View.GONE);
                }
            }*/

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ViewIssue) context).showUploadDialog(collegeList.get(position).getName(), "EDIT", position);
                }
            });
            holder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ViewIssue) context).showUploadDialog(collegeList.get(position).getName(), "UPLOAD", position);
                }
            });
        /*} else {

        }*/

        /*holder.edit.setVisibility(View.GONE);
        holder.upload.setVisibility(View.GONE);
        if (collegeList.get(position).getFileURL().equals("")&&!collegeList.get(position).isOptionalStatus()) {
            holder.view.setVisibility(View.GONE);
            holder.na.setVisibility(View.VISIBLE);

        } else {
            holder.view.setVisibility(View.VISIBLE);
            holder.na.setVisibility(View.GONE);

        }*/
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewIssue) context).showImage(collegeList.get(position).getFileURL(), collegeList.get(position).getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("4") || Preferences.getInstance().level.equals("5")) {
                upload.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                optionalStatus.setVisibility(View.VISIBLE);
            } else {
                upload.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                optionalStatus.setVisibility(View.GONE);
            }
        }

    }

}

package com.mobiquel.udhampur.ui.addissue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mobiquel.udhampur.pojo.DocListModel;

import java.util.List;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.OptionsViewHolder> {

    private Context context;
    private List<DocListModel> collegeList;
    private RecyclerItemClickListener clickListener, clickListener2, clickListener3;

    public DocumentListAdapter(List<DocListModel> collegeList, RecyclerItemClickListener clickListener, RecyclerItemClickListener clickListener2, RecyclerItemClickListener clickListener3) {
        this.collegeList = collegeList;
        this.clickListener = clickListener;
        this.clickListener2 = clickListener2;
        this.clickListener3 = clickListener3;
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
            if (collegeList.get(position).getFileURL().equals("")) {
                holder.view.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
                holder.upload.setVisibility(View.VISIBLE);
            } else {
                holder.view.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
                holder.upload.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            }

        R.id.upload, R.id.edit, R.id.view
        private void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.view:
                    if (clickListener != null)
                        clickListener.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.edit:
                    if (clickListener2 != null)
                        clickListener2.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.upload:
                    if (clickListener3 != null)
                        clickListener3.onRecyclerItemClicked(getAdapterPosition());
                    break;

            }
        }

    }

}

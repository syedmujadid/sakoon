package com.mobiquel.udhampur.ui.addissue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.BeneficiaryModel;
import com.mobiquel.udhampur.pojo.BeneficiaryTitleModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeneficiaryListAdapter extends RecyclerView.Adapter<BeneficiaryListAdapter.OptionsViewHolder> {



    private Context context;
    private List<BeneficiaryModel> collegeList;
    private RecyclerItemClickListener clickListener,clickListener2,clickListener3;
    private String status;

    public BeneficiaryListAdapter(List<BeneficiaryModel> collegeList,String status, RecyclerItemClickListener clickListener,RecyclerItemClickListener clickListener2,RecyclerItemClickListener clickListener3) {
        this.collegeList = collegeList;
        this.clickListener = clickListener;
        this.clickListener2 = clickListener2;
        this.clickListener3=clickListener3;
        this.status=status;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_beneficiary_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionsViewHolder holder, final int position) {
        int i=position+1;
        holder.title.setText("Beneficiary #"+i);
        holder.name.setText("Name :"+collegeList.get(position).getName());
        holder.adharNumber.setText("Adhar Number :"+collegeList.get(position).getAdharNumber());
        holder.adharAddress.setText("Address :"+collegeList.get(position).getAddress());
        holder.contactNumber.setText("Mobile :"+collegeList.get(position).getContactNumber());
        if(status.equals("VIEW")){}
        else
        {
            if(position==0)
                holder.delete.setVisibility(View.INVISIBLE);
            else
                holder.delete.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }


    class OptionsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.edit)
        ImageView edit;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.adharNumber)
        TextView adharNumber;
        @BindView(R.id.adharAddress)
        TextView adharAddress;
        @BindView(R.id.contactNumber)
        TextView contactNumber;
        @BindView(R.id.scanQRCode)
        TextView scanQRCode;
        @BindView(R.id.view)
        TextView view;

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if(status.equals("VIEW")){
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                scanQRCode.setVisibility(View.GONE);
                //view_layout_damageview.mar
            }
            else
            {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                scanQRCode.setVisibility(View.VISIBLE);
            }
        }

        @OnClick({R.id.scanQRCode,R.id.edit,R.id.view,R.id.delete})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.scanQRCode:
                    if (clickListener != null)
                        clickListener.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.view:
                    if (clickListener2 != null)
                        clickListener2.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.edit:
                    if (clickListener2 != null)
                        clickListener2.onRecyclerItemClicked(getAdapterPosition());
                    break;
                case R.id.delete:
                    if (clickListener3 != null)
                        clickListener3.onRecyclerItemClicked(getAdapterPosition());
                    break;
            }
        }
    }

}

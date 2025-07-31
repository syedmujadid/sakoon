package com.mobiquel.udhampur.ui.addissue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mobiquel.udhampur.pojo.DamageDetailModel;
import com.mobiquel.udhampur.utils.Preferences;

import java.util.List;

public class DamageListAdapter extends RecyclerView.Adapter<DamageListAdapter.OptionsViewHolder> {

    private Context context;
    private List<DamageDetailModel> collegeList;
    private String status;
    private RecyclerItemClickListener clickListener,clickListener_delete;

    public DamageListAdapter(List<DamageDetailModel> collegeList,String status,RecyclerItemClickListener clickListener,RecyclerItemClickListener clickListener_delete) {
        this.collegeList = collegeList;
        this.status=status;
        this.clickListener=clickListener;
        this.clickListener_delete=clickListener_delete;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OptionsViewHolder(LayoutInflater.from(context).inflate(R.layout.view_layout_damage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionsViewHolder holder, final int position) {
        int j = position + 1;
        holder.title.setText("Damage Item " + j);
        holder.propertyType.setText("Type: " + collegeList.get(position).getPropertyType());
        holder.damageDescription.setText(collegeList.get(position).getDamageDetail());
        holder.quantity.setText("Quantity: " + collegeList.get(position).getQuantity());
        if(collegeList.get(position).getDamageDetail().equals("")){
            holder.damageDescription.setVisibility(View.GONE);
            holder.desLabel.setVisibility(View.GONE);
        }
        else{
            holder.damageDescription.setVisibility(View.VISIBLE);
            holder.desLabel.setVisibility(View.VISIBLE);
        }
        holder.baseAmnt.setText("Base Amount: Rs "+collegeList.get(position).getBaseAmnt());
        holder.totalAmnt.setText("Total Amount: Rs " + collegeList.get(position).getTotalAmnt());

    }

    @Override
    public int getItemCount() {
        return collegeList != null ? collegeList.size() : 0;
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status.equals("ADD_ISSUE")){
                editDamage.setVisibility(View.VISIBLE);
                deleteDamage.setVisibility(View.VISIBLE);
            }
            else
            {
                editDamage.setVisibility(View.GONE);
                deleteDamage.setVisibility(View.GONE);

            }

        }
        R.id.edit,R.id.delete
        private void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.edit:
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

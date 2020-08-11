package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.pojo.DamageDetailModel;
import com.mobiquel.udhampur.ui.CustomSpinnerAdapter;
import com.mobiquel.udhampur.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateDamageDialog extends Dialog {


    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.updateDamage)
    Button updateDamage;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.propertyType)
    Spinner propertyType;
    @BindView(R.id.categoryType)
    Spinner categoryType;
    @BindView(R.id.damageDescription)
    EditText damageDescription;
    @BindView(R.id.quantity)
    Spinner quantity;

    private Context mContext;
    private DialogListener dialogListener;
    private DamageDetailModel model;
    private String selectedDamage,selectedCategory,selectedAmnt;

    ArrayList<String> damage = new ArrayList<>();
    ArrayList<String> damageCatArray = new ArrayList<>();
    ArrayList<String> damageAmount = new ArrayList<>();
    ArrayList<String> unit = new ArrayList<>();
    JSONArray damaArray;
    JSONArray damaCategoryJSONArray;
    int unitLength = 0;

    public UpdateDamageDialog(Context context, DialogListener dialogListener) {
        super(context);
        mContext = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.form_damage);
        ButterKnife.bind(this);
        getWindow().setDimAmount(0.5f);
        getWindow().setBackgroundDrawable(null);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        //setCancelable(false);
        try {
            JSONObject damaJSON = new JSONObject(Preferences.getInstance().damageList);
            damaArray = damaJSON.getJSONArray("data");
            JSONObject damaCategoryJSON = new JSONObject(Preferences.getInstance().damageCategList);
            damaCategoryJSONArray = damaCategoryJSON.getJSONArray("data");

            for (int i = 0; i < damaCategoryJSONArray.length(); i++) {
                damageCatArray.add(damaCategoryJSONArray.getJSONObject(i).getString("name"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateDamage.setVisibility(View.VISIBLE);
        close.setVisibility(View.VISIBLE);
        setCanceledOnTouchOutside(true);
    }

    @OnClick({R.id.updateDamage, R.id.close})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.updateDamage:
                dialogListener.onPositiveButtonClick();
                break;

            case R.id.close:
                dialogListener.onNegativeButtonClick();
                break;

        }
    }

    public void setData(final DamageDetailModel model) {
        try {
            this.model = model;
            String id = "";
            if(model.getQuantity().equals("")){
                for (int i = 0; i < damaArray.length(); i++) {
                    if (damaArray.getJSONObject(i).getString("isActive").equals("T") && damaArray.getJSONObject(i).getString("categoryId").equals("1")) {
                        damage.add(damaArray.getJSONObject(i).getString("type"));
                        damageAmount.add(damaArray.getJSONObject(i).getString("amount"));
                        unit.add(damaArray.getJSONObject(i).getString("unit"));
                    }
                }

            }
            else{
                damageDescription.setText(model.getDamageDetail());
                for (int i = 0; i < damaArray.length(); i++) {
                    try {
                        if (damaArray.getJSONObject(i).getString("type").equals(model.getPropertyType())) {
                            id = damaArray.getJSONObject(i).getString("categoryId");
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
          //      categoryType.setSelection(Integer.parseInt(id) - 1);
                final String finalId = id;
                categoryType.post(new Runnable() {
                    @Override
                    public void run() {
                        categoryType.setSelection(Integer.parseInt(finalId) - 1);
                    }
                });
                for (int i = 0; i < damaArray.length(); i++) {
                    if (damaArray.getJSONObject(i).getString("isActive").equals("T") && damaArray.getJSONObject(i).getString("categoryId").equals(id)) {
                        damage.add(damaArray.getJSONObject(i).getString("type"));
                        damageAmount.add(damaArray.getJSONObject(i).getString("amount"));
                        unit.add(damaArray.getJSONObject(i).getString("unit"));
                    }
                }

                for (int i = 0; i < damage.size(); i++) {
                    if (damage.get(i).equals(model.getPropertyType())) {
                        selectedDamage = damage.get(i);
                        selectedAmnt=damageAmount.get(i);
                        unitLength = Integer.parseInt(unit.get(i));
                     //   propertyType.setSelection(i);
                        final int finalI = i;
                        propertyType.post(new Runnable() {
                            @Override
                            public void run() {
                                propertyType.setSelection(finalI);
                            }
                        });
                        break;
                    }
                }
                try {
                    quantity.post(new Runnable() {
                        @Override
                        public void run() {
                            quantity.setSelection(Integer.parseInt(model.getQuantity()) - 1);
                        }
                    });
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }




            ArrayAdapter cityAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_list_item_1, damageCatArray);
            cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            categoryType.setAdapter(cityAdapter);


            final CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mContext, damage, damageAmount);
            propertyType.setAdapter(adapter);
            unitLength = Integer.parseInt(unit.get(0));
            if (unitLength == 0) {
                ArrayAdapter unitAdapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_list_item_1, R.array.noArray);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                quantity.setAdapter(unitAdapter);
            } else {
                String[] unitArray = new String[unitLength];
                for (int i = 0; i <= unitLength - 1; i++) {
                    unitArray[i] = String.valueOf(i + 1);
                }
                ArrayAdapter unitAdapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_list_item_1, unitArray);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                quantity.setAdapter(unitAdapter);
            }


            categoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    selectedCategory=damageCatArray.get(pos);

                    damage.clear();
                    damageAmount.clear();
                    unit.clear();
                    for (int i = 0; i < damaArray.length(); i++) {
                        try {
                            if (damaArray.getJSONObject(i).getString("categoryId").equals(damaCategoryJSONArray.getJSONObject(pos).getString("id"))) {
                                damage.add(damaArray.getJSONObject(i).getString("type"));
                                damageAmount.add(damaArray.getJSONObject(i).getString("amount"));
                                unit.add(damaArray.getJSONObject(i).getString("unit"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            propertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    selectedDamage = damage.get(pos);
                    selectedAmnt=damageAmount.get(pos);
                    unitLength = Integer.parseInt(unit.get(pos));
                    if (unitLength == 0) {
                        String[] unitArray = new String[10];
                        for (int i = 0; i <= 9; i++) {
                            unitArray[i] = String.valueOf(i + 1);
                        }
                        ArrayAdapter unitAdapter = new ArrayAdapter<String>(mContext,
                                android.R.layout.simple_list_item_1, unitArray);
                        unitAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        quantity.setAdapter(unitAdapter);
                    } else {
                        String[] unitArray = new String[unitLength];
                        for (int i = 0; i <= unitLength - 1; i++) {
                            unitArray[i] = String.valueOf(i + 1);
                        }
                        ArrayAdapter unitAdapter = new ArrayAdapter<String>(mContext,
                                android.R.layout.simple_list_item_1, unitArray);
                        unitAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        quantity.setAdapter(unitAdapter);
                    }
                    ((View) view.findViewById(R.id.view)).setVisibility(View.GONE);

                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public DamageDetailModel getData() {
        model.setDamageDetail(damageDescription.getText().toString());
        model.setPropertyType(selectedDamage);
        model.setCategory(selectedCategory);
        model.setQuantity(quantity.getSelectedItem().toString());
        int total=Integer.parseInt(selectedAmnt)*Integer.parseInt(quantity.getSelectedItem().toString());
        model.setTotalAmnt(String.valueOf(total));
        model.setBaseAmnt(selectedAmnt);
        return model;
    }

    public void setDataEmpty() {
      damageDescription.setText("");
    }

}

package com.mobiquel.udhampur.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.DamageDetailModel;
import com.mobiquel.udhampur.ui.addissue.DamageListAdapter;
import com.mobiquel.udhampur.utils.AppConstants;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FinaceListActivity extends AppCompatActivity {



    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.tab_name)
    TextView tabName;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.noResult)
    TextView noResult;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.totalAmntLabel)
    TextView totalAmntLabel;
    @BindView(R.id.header)
    RelativeLayout header;
    private List<DamageDetailModel> damageDetailModelList=new ArrayList<>();

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        tabName.setText("Finance Detail for Incident #" + getIntent().getExtras().getString("ID"));
        Preferences.getInstance().loadPreferences(FinaceListActivity.this);
       list.setPadding(15,15,15,15);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.right_out, R.anim.left_in);

            }
        });

        getDamageDetails();

    }

    private void getDamageDetails() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(FinaceListActivity.this).getRequestQueue();
        String url = "";
        Preferences.getInstance().loadPreferences(FinaceListActivity.this);

        url = AppConstants.BASE_URL + "getDamageDetailsForIncident";

        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        JSONArray damageArray = responseObject.getJSONArray("responseObject");
                        int total = 0;
                        for (int i = 0; i < damageArray.length(); i++) {
                            total = total + Integer.parseInt(damageArray.getJSONObject(i).getString("totalAmount"));
                            DamageDetailModel model = new DamageDetailModel();
                            model.setQuantity(damageArray.getJSONObject(i).getString("quantity"));
                            model.setPropertyType(damageArray.getJSONObject(i).getString("type"));
                            model.setDamageDetail(damageArray.getJSONObject(i).getString("description"));
                            model.setBaseAmnt(damageArray.getJSONObject(i).getString("baseAmount"));
                            model.setTotalAmnt(damageArray.getJSONObject(i).getString("totalAmount"));
                            damageDetailModelList.add(model);
                        }
                        totalAmntLabel.setVisibility(View.VISIBLE);
                        totalAmntLabel.setText("Grand Total: Rs " + total);
                        DamageListAdapter damageListAapter = new DamageListAdapter(damageDetailModelList, "FINANCE", new RecyclerItemClickListener() {
                            @Override
                            public void onRecyclerItemClicked(int position) {

                            }
                        }, new RecyclerItemClickListener() {
                            @Override
                            public void onRecyclerItemClicked(int position) {

                            }
                        });
                        list.setLayoutManager(new LinearLayoutManager(FinaceListActivity.this));
                        list.setAdapter(damageListAapter);
                        noResult.setVisibility(View.GONE);
                        header.setVisibility(View.VISIBLE);

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(FinaceListActivity.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progress_bar.isShown()) {
                    progress_bar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FinaceListActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                if (progress_bar.isShown()) {
                    progress_bar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(FinaceListActivity.this);
                params.put("incidentId", getIntent().getExtras().getString("ID"));
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(FinaceListActivity.this)) {
            queue.add(requestObject);
        } else {
            if (progress_bar != null && progress_bar.isShown()) {
                progress_bar.setVisibility(View.GONE);
            }
            Utils.showToast(FinaceListActivity.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.right_out, R.anim.left_in);

    }


}
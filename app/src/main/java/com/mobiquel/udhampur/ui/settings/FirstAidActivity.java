package com.mobiquel.udhampur.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.dialogs.EnterRemarksDialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.utils.AppConstants;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstAidActivity extends AppCompatActivity {


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
    private EnterRemarksDialog enterRemarksDialog;
    private String clickSource = "", remarkValue = "";
    private String incidentId = "";

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        tabName.setText("Notification");

        Preferences.getInstance().loadPreferences(FirstAidActivity.this);
           menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.right_out, R.anim.left_in);

            }
        });

        getAid();

        enterRemarksDialog = new EnterRemarksDialog(FirstAidActivity.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                String answer = enterRemarksDialog.getSolution();
                // mQuestionList.get(selectedQuestionId).setMarkedAnswer(answer);
                if (answer.equals(""))
                    Utils.showToast(FirstAidActivity.this, "Please enter remarks!");
                else {
                    enterRemarksDialog.cancel();
                    Utils.hideKeyboard(FirstAidActivity.this);
                    remarkValue = answer;

                    approveRejectCase();
                }

            }

            @Override
            public void onNegativeButtonClick() {
                enterRemarksDialog.cancel();
            }
        });

    }

    private void getAid() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(FirstAidActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getFirstAidNoticeForApp";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        if (responseObject.getJSONArray("responseObject").length() > 0) {
                            ListOfFirstAidAdapter mAdapter = new ListOfFirstAidAdapter(responseObject.getJSONArray("responseObject"), new RecyclerItemClickListener() {
                                @Override
                                public void onRecyclerItemClicked(int position) {
                                    try {
                                        incidentId = responseObject.getJSONArray("responseObject").getJSONObject(position).getString("incidentId");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    clickSource = "APPROVED";
                                    enterRemarksDialog.show();
                                }
                            }, new RecyclerItemClickListener() {
                                @Override
                                public void onRecyclerItemClicked(int position) {
                                    try {
                                        incidentId = responseObject.getJSONArray("responseObject").getJSONObject(position).getString("incidentId");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    clickSource = "REJECTED";
                                    enterRemarksDialog.show();
                                }
                            });
                            list.setAdapter(mAdapter);
                            list.setLayoutManager(new LinearLayoutManager(FirstAidActivity.this));
                            noResult.setVisibility(View.GONE);
                        }

                        //finish();

                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progress_bar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Preferences.getInstance().loadPreferences(FirstAidActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);

                return params;
            }
        };
        queue.add(requestObject);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.right_out, R.anim.left_in);

    }

    private void approveRejectCase() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(FirstAidActivity.this).getRequestQueue();
        String url = "";

        url = AppConstants.BASE_URL + "approveRejectFirstAidCase";

        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getAid();
                if (progress_bar.isShown()) {
                    progress_bar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FirstAidActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();

                if (progress_bar.isShown()) {
                    progress_bar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", clickSource);
                params.put("incidentId", incidentId);
                params.put("comment", remarkValue);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(FirstAidActivity.this)) {
            queue.add(requestObject);
        } else {
            if (progress_bar != null && progress_bar.isShown()) {
                progress_bar.setVisibility(View.GONE);
            }
            Utils.showToast(FirstAidActivity.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
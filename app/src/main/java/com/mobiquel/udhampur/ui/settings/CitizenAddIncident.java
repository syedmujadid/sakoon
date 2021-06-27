package com.mobiquel.udhampur.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.dialogs.UpdateAppDialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CitizenAddIncident extends AppCompatActivity {


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

    @BindView(R.id.addIncident)
    Button addIncident;
    @BindView(R.id.districtList)
    Spinner districtList;
    @BindView(R.id.tehsilList)
    Spinner tehsilList;
    @BindView(R.id.naibatList)
    Spinner naibatList;
    @BindView(R.id.patwariHalqaList)
    Spinner patwariHalqaList;
    @BindView(R.id.villageList)
    Spinner villageList;
    @BindView(R.id.details)
    EditText details;
    @BindView(R.id.addIssueLayout)
    RelativeLayout addIssueLayout;
    @BindView(R.id.im1)
    ImageView im1;
    @BindView(R.id.im2)
    ImageView im2;
    @BindView(R.id.l2)
    RelativeLayout l2;
    @BindView(R.id.l1)
    LinearLayout l1;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.english)
    RadioButton english;
    @BindView(R.id.hindi)
    RadioButton hindi;
    @BindView(R.id.lanGroup)
    RadioGroup lanGroup;
    @BindView(R.id.t1)
    TextView t1;
    @BindView(R.id.t2)
    TextView t2;
    @BindView(R.id.t3)
    TextView t3;
    @BindView(R.id.t4)
    TextView t4;
    @BindView(R.id.t5)
    TextView t5;
    @BindView(R.id.t6)
    TextView t6;
    private UpdateAppDialog mFeedbackDialog;
    private ArrayList<String> districtIds = new ArrayList<>();
    private ArrayList<String> tehsilIds = new ArrayList<>();
    private ArrayList<String> naibatIds = new ArrayList<>();
    private ArrayList<String> patwariHalqaIds = new ArrayList<>();
    private ArrayList<String> villageIds = new ArrayList<>();
    private String lang = "";

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_add_issue);
        ButterKnife.bind(this);

        tabName.setText("Register Incident");

        Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
        if (getIntent().getExtras().getString("SOURCE").equals("ADD")) {

            addIssueLayout.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            getDistrictList();
            l2.setVisibility(View.VISIBLE);
            im2.setVisibility(View.VISIBLE);
            im1.setVisibility(View.GONE);
            lanGroup.setVisibility(View.VISIBLE);
        } else {
            addIssueLayout.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            noResult.setVisibility(View.VISIBLE);
            getAllIncidentsByMobile();
            l2.setVisibility(View.VISIBLE);
            im1.setVisibility(View.VISIBLE);
            im2.setVisibility(View.GONE);
            lanGroup.setVisibility(View.VISIBLE);
        }
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.right_out, R.anim.left_in);

            }
        });

        districtList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (districtIds.size() > 0)
                    getTehsilList(districtIds.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tehsilList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (tehsilIds.size() > 0)
                    getNaibatList(tehsilIds.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        naibatList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (naibatIds.size() > 0)
                    getPatwariHalqaList(naibatIds.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        patwariHalqaList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (patwariHalqaIds.size() > 0)
                    getVillageList(patwariHalqaIds.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        addIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (details.getText().toString().equals(""))
                    Utils.showSnackBar(view, "Please enter Incident details!");
                else {
                    addIncidentByCitizen();
                }
            }
        });

        mFeedbackDialog = new UpdateAppDialog(CitizenAddIncident.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                mFeedbackDialog.cancel();
            }

            @Override
            public void onNegativeButtonClick() {

            }
        });

        lanGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton r1 = (RadioButton) findViewById(i);
                if (r1.getText().toString().equals("हिंदीं"))
                    lang = "Hindi";
                else
                    lang = "English";

                setUpTextLan();
            }
        });
    }

    private void getDistrictList() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getDistrictList";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());

                        JSONArray villArray = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[villArray.length()];

                        for (int i = 0; i < villArray.length(); i++) {
                            cityArray[i] = villArray.getJSONObject(i).getString("name");
                            districtIds.add(villArray.getJSONObject(i).getString("districtId"));
                        }
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(CitizenAddIncident.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        districtList.setAdapter(cityAdapter);
                        if (districtIds.size() > 0)
                            getTehsilList(districtIds.get(0));

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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);

                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getTehsilList(final String id) {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getTehsilListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        tehsilIds.clear();
                        JSONArray villArray = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[villArray.length()];

                        for (int i = 0; i < villArray.length(); i++) {
                            cityArray[i] = villArray.getJSONObject(i).getString("name");
                            tehsilIds.add(villArray.getJSONObject(i).getString("tehsilId"));
                        }
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(CitizenAddIncident.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        tehsilList.setAdapter(cityAdapter);

                        if (tehsilIds.size() > 0)
                            getNaibatList(tehsilIds.get(0));

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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();

                params.put("districtId", id);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);
                params.put("startIndex", "0");
                params.put("length", "100");
                params.put("searchString", "");
                params.put("sortBy", "CREATION_DATE");
                params.put("order", "D");

                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getNaibatList(final String id) {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getNaibatListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        naibatIds.clear();
                        JSONArray villArray = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[villArray.length()];

                        for (int i = 0; i < villArray.length(); i++) {
                            cityArray[i] = villArray.getJSONObject(i).getString("name");
                            naibatIds.add(villArray.getJSONObject(i).getString("naibatId"));
                        }
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(CitizenAddIncident.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        naibatList.setAdapter(cityAdapter);

                        if (naibatIds.size() > 0)
                            getPatwariHalqaList(naibatIds.get(0));


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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();

                params.put("tehsilId", id);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);
                params.put("startIndex", "0");
                params.put("length", "100");
                params.put("searchString", "");
                params.put("sortBy", "CREATION_DATE");
                params.put("order", "D");
                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getPatwariHalqaList(final String id) {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getPatwariHalqaListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        patwariHalqaIds.clear();
                        JSONArray villArray = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[villArray.length()];

                        for (int i = 0; i < villArray.length(); i++) {
                            cityArray[i] = villArray.getJSONObject(i).getString("name");
                            patwariHalqaIds.add(villArray.getJSONObject(i).getString("patwariHalqaId"));
                        }
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(CitizenAddIncident.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        patwariHalqaList.setAdapter(cityAdapter);

                        if (patwariHalqaIds.size() > 0)
                            getVillageList(patwariHalqaIds.get(0));


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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();

                params.put("naibatId", id);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);
                params.put("startIndex", "0");
                params.put("length", "100");
                params.put("searchString", "");
                params.put("sortBy", "CREATION_DATE");
                params.put("order", "D");
                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getVillageList(final String id) {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getVillageListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        villageIds.clear();
                        JSONArray villArray = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[villArray.length()];

                        for (int i = 0; i < villArray.length(); i++) {
                            cityArray[i] = villArray.getJSONObject(i).getString("name");
                            villageIds.add(villArray.getJSONObject(i).getString("villageId"));
                        }
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(CitizenAddIncident.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        villageList.setAdapter(cityAdapter);


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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();

                params.put("patwariHalqaId", id);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);
                params.put("startIndex", "0");
                params.put("length", "100");
                params.put("searchString", "");
                params.put("sortBy", "CREATION_DATE");
                params.put("order", "D");
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

    private void setUpTextLan() {
        if (lang.equals("Hindi")) {
            t1.setText("जिला चुनें");
            t2.setText("तहसील का चयन करें");
            t4.setText("पटवारी हलका का चयन करें");
            t5.setText("गाँव का चयन करें");
            t3.setText("naibat का चयन करें");
            t6.setText("विवरण दर्ज करें");
            addIncident.setText("सत्यापन के लिए प्रस्तुत करें");
            details.setHint("विवरण दर्ज करें");
            tabName.setText("हादसा दर्ज करें");
            noResult.setText("कोई डेटा नहीं मिला");
        } else {
            noResult.setText("No Result found!");
            t1.setText("Select district");
            t2.setText("Select tehsil");
            t4.setText("Select patwari");
            t5.setText("Select village");
            t3.setText("Select naibat");
            t6.setText("Enter details");
            addIncident.setText("Submit for vrification");
            details.setHint("Enter details");
            tabName.setText("Register Incident");
        }
    }

    private void addIncidentByCitizen() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "addIncidentByCitizen";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        mFeedbackDialog.show();
                        mFeedbackDialog.setData("Report Incident", "Incident reported successfully");
                        details.setText("");
                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 1) {
                        Log.e("RESPO", responseObject.toString());
                        mFeedbackDialog.show();
                        mFeedbackDialog.setData("Error Reporting Incident", responseObject.getString("errorMessage"));

                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // progress_bar.setVisibility(View.GONE);
                mFeedbackDialog.show();
                mFeedbackDialog.setData("Error Reporting Incident", "There is error in reporting Incident. Please check!");
                progress_bar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("districtId", districtIds.get(districtList.getSelectedItemPosition()));
                params.put("tehsilId", tehsilIds.get(tehsilList.getSelectedItemPosition()));
                params.put("naibatId", naibatIds.get(naibatList.getSelectedItemPosition()));
                params.put("patwarHalqaId", patwariHalqaIds.get(patwariHalqaList.getSelectedItemPosition()));
                params.put("villageId", villageIds.get(villageList.getSelectedItemPosition()));
                params.put("description", details.getText().toString());
                params.put("mobile", getIntent().getExtras().getString("MOBILE"));


                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getAllIncidentsByMobile() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CitizenAddIncident.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getAllIncidentsByMobile";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                try {
                    final JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        JSONArray array = responseObject.getJSONArray("responseObject");
                        if (array.length() > 0) {
                            ListOfIncidentAdapter mAdapter = new ListOfIncidentAdapter(array);
                            list.setLayoutManager(new LinearLayoutManager(CitizenAddIncident.this));
                            list.setAdapter(mAdapter);
                            noResult.setVisibility(View.GONE);
                        } else
                            noResult.setVisibility(View.VISIBLE);


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
                Preferences.getInstance().loadPreferences(CitizenAddIncident.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", getIntent().getExtras().getString("MOBILE"));
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);

                return params;
            }
        };
        queue.add(requestObject);
    }

    @OnClick({R.id.im1, R.id.im2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.im1:
                im2.setVisibility(View.VISIBLE);
                im1.setVisibility(View.GONE);
                noResult.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                addIssueLayout.setVisibility(View.VISIBLE);
                details.setText("");
                getDistrictList();
                break;
            case R.id.im2:

                im2.setVisibility(View.GONE);
                im1.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                getAllIncidentsByMobile();
                addIssueLayout.setVisibility(View.GONE);
                break;
        }
    }
}
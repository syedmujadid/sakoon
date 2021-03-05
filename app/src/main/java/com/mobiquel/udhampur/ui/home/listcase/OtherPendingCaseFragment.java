package com.mobiquel.udhampur.ui.home.listcase;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseFragment;
import com.mobiquel.udhampur.dialogs.View_Logs_Dialog;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.pojo.IssueListModel_Online;
import com.mobiquel.udhampur.pojo.LogsListModel;
import com.mobiquel.udhampur.pojo.OfficialsListModel;
import com.mobiquel.udhampur.ui.addissue.ViewIssue;
import com.mobiquel.udhampur.ui.home.HomeActivity;
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
import butterknife.Unbinder;

public class OtherPendingCaseFragment extends BaseFragment implements ListDataView, Animation.AnimationListener {

    @BindView(R.id.noResult)
    TextView noResult;
    @BindView(R.id.recyclerView)
    RecyclerView listOfItem;
    @BindView(R.id.levelType)
    Spinner levelType;
    @BindView(R.id.totalCount)
    TextView totalCount;
    @BindView(R.id.levelLayout)
    LinearLayout levelLayout;
    private Unbinder unbinder;

    private ListDataPresenter mPresenter;
    private ListOfCaseAdapter mAdapter;
    private LinearLayoutManager manager;
    private List<IssueListModel_Online> issueListModel_onlines = new ArrayList<>();
    private JSONArray issueArray, logArray;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    private List<IssueListModel> mCaseList = new ArrayList<>();
    private View_Logs_Dialog mLogDialog;
    private String level;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new ListDataPresenter(this);
        level = "1";
        levelLayout.setVisibility(View.VISIBLE);
        mAdapter = new ListOfCaseAdapter(issueListModel_onlines, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                if (Preferences.getInstance().level.equals("1")) {
                    Intent intent = new Intent(getActivity(), ViewIssue.class);
                    try {
                        intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ViewIssue.class);
                    try {
                        intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                        intent.putExtra("SOURCE", "PENDING");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }

            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                getListOfLogs(issueListModel_onlines.get(position).getCaseId());
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                getListOfOfficials(issueListModel_onlines.get(position).getCaseId());
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {

            }
        });

        manager = new LinearLayoutManager(getActivity());
        listOfItem.setLayoutManager(manager);
        listOfItem.setAdapter(mAdapter);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getUsers();
                ((HomeActivity) getActivity()).updatePendingCase(String.valueOf(issueListModel_onlines.size()));
                pullToRefresh.setRefreshing(false);
            }
        });

        levelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int j = i + 1;
                level = String.valueOf(j);
                getUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //  prepareData();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void initVariables() {
        mLogDialog = new View_Logs_Dialog(getActivity());
    }


    @Override
    public void setListeners() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void getListOfUsers() {
        getUsers();
    }

    private void getUsers() {
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getPendingIncidentListForLevel/";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    Log.e("RESPO_PENDING", "=== " + responseData.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        if (isFragmentAdded()) {
                            issueListModel_onlines.clear();
                            totalCount.setText("Total Incident: 0");
                            try {
                                JSONObject response = responseObject.getJSONObject("responseObject");
                                if (response.isNull("incidentList")) {
                                    noResult.setVisibility(View.VISIBLE);
                                } else {
                                    issueArray = response.getJSONArray("incidentList");
                                    for (int i = 0; i < response.getJSONArray("incidentList").length(); i++) {
                                        IssueListModel_Online model = new IssueListModel_Online();
                                        model.setCaseId(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentId"));
                                        model.setApplicantName(response.getJSONArray("incidentList").getJSONObject(i).getString("applicantName"));
                                        //model.setAssignedTo(response.getJSONArray("incidentList").getJSONObject(i).getString("role"));
                                        model.setIncidentDate(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentDate"));
                                        model.setCreatedOn(response.getJSONArray("incidentList").getJSONObject(i).getString("createdOn"));
                                        model.setStatus("OTHERPENDING");
                                        model.setIssueAtLevel(response.getJSONArray("incidentList").getJSONObject(i).getString("issueAtLevel"));

                                        if(response.getJSONArray("incidentList").getJSONObject(i).has("pendingDays"))
                                        {
                                            model.setPendingDays(response.getJSONArray("incidentList").getJSONObject(i).getString("pendingDays"));
                                            model.setPendingCode(response.getJSONArray("incidentList").getJSONObject(i).getString("pendingCode"));

                                        }

                                        issueListModel_onlines.add(model);
                                    }
                                    if (issueListModel_onlines.size() > 0)
                                        noResult.setVisibility(View.GONE);
                                    else
                                        noResult.setVisibility(View.VISIBLE);


                                    totalCount.setText("Total incident: " + issueListModel_onlines.size());
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(getActivity());
                data.put("officialId", Preferences.getInstance().officialId);
                data.put("startIndex", "0");
                data.put("length", "100");
                data.put("searchString", "");
                data.put("sortBy", "CREATION_DATE");
                data.put("order", "D");
                data.put("level", level);
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "official");
                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        queue.add(requestObject);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getListOfUsers();
    }

    private void getListOfLogs(final String id) {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getActionLogForIncident/";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    Log.e("RESPO_LOGS", "=== " + responseData.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        try {
                            logArray = responseObject.getJSONArray("responseObject");
                            List<LogsListModel> logList = new ArrayList<>();
                            for (int i = 0; i < logArray.length(); i++) {
                                LogsListModel model = new LogsListModel();
                                model.setName(logArray.getJSONObject(i).getString("createdByName"));
                                model.setComment(logArray.getJSONObject(i).getString("remark"));
                                model.setMobile(logArray.getJSONObject(i).getString("createdByMobile"));
                                model.setLevel(logArray.getJSONObject(i).getString("createdByDesignation"));
                                logList.add(model);
                            }
                            mLogDialog.show();
                            mLogDialog.setData(logList, id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(getActivity());
                data.put("incidentId", id);
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "official");
                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        queue.add(requestObject);

    }

    private void getListOfOfficials(final String id) {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getOfficialsMappedToIncident/";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    Log.e("RESPO_LOGS", "=== " + responseData.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        try {
                            logArray = responseObject.getJSONArray("responseObject");
                            List<OfficialsListModel> logList = new ArrayList<>();
                            for (int i = 0; i < logArray.length(); i++) {
                                OfficialsListModel model = new OfficialsListModel();
                                model.setName(logArray.getJSONObject(i).getString("name"));
                                model.setMobile(logArray.getJSONObject(i).getString("mobile"));
                                model.setDesignation(logArray.getJSONObject(i).getString("designation"));
                                model.setVillageName(logArray.getJSONObject(i).getString("villageNames"));
                                logList.add(model);
                            }
                            mLogDialog.show();
                            mLogDialog.setOfficialData(logList, id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(getActivity());
                data.put("incidentId", id);
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "official");
                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        queue.add(requestObject);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

package com.mobiquel.udhampur.ui.home.listcase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseFragment;
import com.mobiquel.udhampur.dialogs.EnterRemarksDialog;
import com.mobiquel.udhampur.dialogs.View_Logs_Dialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.pojo.IssueListModel_Online;
import com.mobiquel.udhampur.pojo.LogsListModel;
import com.mobiquel.udhampur.pojo.OfficialsListModel;
import com.mobiquel.udhampur.ui.addissue.AddIssue_Pend;
import com.mobiquel.udhampur.ui.addissue.ViewIssue;
import com.mobiquel.udhampur.ui.home.HomeActivity;
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
import butterknife.Unbinder;

public class PendingCaseFragment extends BaseFragment implements ListDataView, Animation.AnimationListener {

    @BindView(R.id.noResult)
    TextView noResult;
    @BindView(R.id.totalLayout)
    LinearLayout totalLayout;
    @BindView(R.id.t1)
    TextView t1;
    @BindView(R.id.t2)
    TextView t2;
    @BindView(R.id.recyclerView)
    RecyclerView listOfItem;
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
    private EnterRemarksDialog enterRemarksDialog;
    private String incidentId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new ListDataPresenter(this);

        mAdapter = new ListOfCaseAdapter(issueListModel_onlines, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                try {
                    if (!issueListModel_onlines.get(position).getCitizenDescription().equals("")) {

                        if (Preferences.getInstance().level.equals("1") && issueArray.getJSONObject(position).getString("issueAtLevel").equals("1")) {
                            Intent intent = new Intent(getActivity(), AddIssue_Pend.class);
                            intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                            intent.putExtra("SOURCE", "UPDATE_CITIZEN");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), ViewIssue.class);
                            intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                            intent.putExtra("SOURCE", "UPDATE");
                            startActivity(intent);
                        }
                    } else {
                        if (Preferences.getInstance().level.equals("1") && issueArray.getJSONObject(position).getString("issueAtLevel").equals("1")) {
                            Intent intent = new Intent(getActivity(), AddIssue_Pend.class);
                            intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                            intent.putExtra("SOURCE", "UPDATE");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), ViewIssue.class);
                            intent.putExtra("DATA_JSON", issueArray.getJSONObject(position).toString());
                            intent.putExtra("SOURCE", "UPDATE");
                            startActivity(intent);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
            public void onRecyclerItemClicked(final int position) {
                incidentId = issueListModel_onlines.get(position).getCaseId();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete/reject this case?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!issueListModel_onlines.get(position).getCitizenDescription().equals(""))
                                    enterRemarksDialog.show();
                                else
                                    deleteCase(issueListModel_onlines.get(position).getCaseId(), position);

                                dialog.cancel();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button

                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Delete Case");
                alert.show();
                //
            }
        });

        manager = new LinearLayoutManager(getActivity());
        listOfItem.setLayoutManager(manager);
        listOfItem.setAdapter(mAdapter);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (Preferences.getInstance().level.equals("7"))
                    getLevel7();
                else
                    getIncidentListForOfficial();
                ((HomeActivity) getActivity()).updatePendingCase(String.valueOf(issueListModel_onlines.size()));
                pullToRefresh.setRefreshing(false);
            }
        });
        totalLayout.setVisibility(View.VISIBLE);
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
        enterRemarksDialog = new EnterRemarksDialog(getActivity(), new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                String answer = enterRemarksDialog.getSolution();
                // mQuestionList.get(selectedQuestionId).setMarkedAnswer(answer);
                if (answer.equals(""))
                    Utils.showToast(getActivity(), "Please enter remarks!");
                else {
                    enterRemarksDialog.cancel();
                    Utils.hideKeyboard(getActivity());
                    approveRejectCase(answer);
                }

            }

            @Override
            public void onNegativeButtonClick() {
                enterRemarksDialog.cancel();
            }
        });

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
        if (Preferences.getInstance().level.equals("7"))
            getLevel7();
        else
            getIncidentListForOfficial();
    }

    private void getIncidentListForOfficial() {
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        // R.string.API
        url = NetworkConstants.BASE_URL + "getIncidentListForOfficial/";
        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    Log.e("RESPO_PENDING", "=== " + responseData.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        if (isFragmentAdded()) {
                            issueListModel_onlines.clear();
                            try {
                                JSONObject response = responseObject.getJSONObject("responseObject");
                                if (response.isNull("incidentList")) {
                                    noResult.setVisibility(View.VISIBLE);
                                    totalLayout.setVisibility(View.GONE);
                                } else {
                                    totalLayout.setVisibility(View.VISIBLE);
                                    issueArray = response.getJSONArray("incidentList");
                                    int pen = 0, pro = 0;

                                    for (int i = 0; i < response.getJSONArray("incidentList").length(); i++) {
                                        if (response.getJSONArray("incidentList").getJSONObject(i).getString("issueAtLevel").equals(Preferences.getInstance().level))
                                            pen++;
                                        else
                                            pro++;

                                        IssueListModel_Online model = new IssueListModel_Online();
                                        model.setCaseId(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentId"));
                                        model.setApplicantName(response.getJSONArray("incidentList").getJSONObject(i).getString("applicantName"));
                                        model.setIncidentDate(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentDate"));
                                        model.setCreatedOn(response.getJSONArray("incidentList").getJSONObject(i).getString("createdOn"));
                                        model.setUpdatedBy(response.getJSONArray("incidentList").getJSONObject(i).getString("updatedBy"));
                                        model.setIssueAtLevel(response.getJSONArray("incidentList").getJSONObject(i).getString("issueAtLevel"));
                                        model.setStatus("PENDING");
                                        model.setCitizenDescription(response.getJSONArray("incidentList").getJSONObject(i).getString("citizenDescription"));
                                        model.setVillName(response.getJSONArray("incidentList").getJSONObject(i).getString("villageName"));
                                        model.setApplicantMobile(response.getJSONArray("incidentList").getJSONObject(i).getString("mobile"));
                                        if (response.getJSONArray("incidentList").getJSONObject(i).has("isApprovedEarlierCheck"))
                                            model.setIsApprovedEarlierCheck(response.getJSONArray("incidentList").getJSONObject(i).getString("isApprovedEarlierCheck"));

                                        issueListModel_onlines.add(model);
                                    }
                                    if (issueListModel_onlines.size() > 0)
                                        noResult.setVisibility(View.GONE);
                                    else
                                        noResult.setVisibility(View.VISIBLE);

                                    t1.setText("Total Pending: " + pen);
                                    t2.setText("Total Processed: " + pro);
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
                data.put("status", "PENDING");
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "official");
                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        queue.add(requestObject);

    }

    private void getLevel7() {
        totalLayout.setVisibility(View.GONE);
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
                            try {
                                JSONObject response = responseObject.getJSONObject("responseObject");
                                if (response.isNull("incidentList")) {
                                    noResult.setVisibility(View.VISIBLE);
                                    totalLayout.setVisibility(View.GONE);
                                } else {
                                    totalLayout.setVisibility(View.VISIBLE);
                                    issueArray = response.getJSONArray("incidentList");
                                    int pen = 0, pro = 0;

                                    for (int i = 0; i < response.getJSONArray("incidentList").length(); i++) {
                                        if (response.getJSONArray("incidentList").getJSONObject(i).getString("issueAtLevel").equals(Preferences.getInstance().level))
                                            pen++;
                                        else
                                            pro++;
                                        IssueListModel_Online model = new IssueListModel_Online();
                                        model.setCaseId(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentId"));
                                        model.setApplicantName(response.getJSONArray("incidentList").getJSONObject(i).getString("applicantName"));
                                        //model.setAssignedTo(response.getJSONArray("incidentList").getJSONObject(i).getString("role"));
                                        model.setIncidentDate(response.getJSONArray("incidentList").getJSONObject(i).getString("incidentDate"));
                                        model.setCreatedOn(response.getJSONArray("incidentList").getJSONObject(i).getString("createdOn"));
                                        model.setStatus("PENDING");

                                        if (response.getJSONArray("incidentList").getJSONObject(i).has("pendingDays")) {
                                            model.setPendingDays(response.getJSONArray("incidentList").getJSONObject(i).getString("pendingDays"));
                                            model.setPendingCode(response.getJSONArray("incidentList").getJSONObject(i).getString("pendingCode"));

                                        }

                                        issueListModel_onlines.add(model);
                                    }
                                    if (issueListModel_onlines.size() > 0)
                                        noResult.setVisibility(View.GONE);
                                    else
                                        noResult.setVisibility(View.VISIBLE);

                                    t1.setText("Total Pending: " + pen);
                                    t2.setText("Total Processed: " + pro);
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
                data.put("level", "7");
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


    private void deleteCase(final String id, final int pos) {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "deleteIncident/";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    Log.e("RESPO_LOGS", "=== " + responseData.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        showSnackBar("Incident deleted successfully!");
                        issueListModel_onlines.remove(pos);
                        mAdapter.notifyDataSetChanged();

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

    private void approveRejectCase(final String remark) {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "";
        Preferences.getInstance().loadPreferences(getActivity());

        url = AppConstants.BASE_URL + "rejectIncidentAddedByCitizen";
        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(getActivity());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(getActivity());

                params.put("status", "CANCELLED");

                params.put("officialId", Preferences.getInstance().officialId);
                params.put("incidentId", incidentId);
                params.put("remark", remark);
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(getActivity())) {
            queue.add(requestObject);
        } else {
            hideProgressBar();
            Utils.showToast(getActivity(), AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

}

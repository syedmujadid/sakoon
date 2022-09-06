package com.mobiquel.udhampur.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobiquel.udhampur.dao.DAO;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.utils.AppConstants;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadOfflineDraftedIncidents extends BroadcastReceiver {
    private Context mContext;
    private DAO dao;
    private List<IssueListModel> draftedList;
    private String id;
    private JSONObject primaryDetails;
    private JSONObject damageDetails;
    private JSONObject beneficiaryDetails;
    private JSONObject documentDetails;
    /*private HomeActivity mActivity;
    public UploadOfflineDraftedIncidents(HomeActivity con) {
        this.mActivity = con;
    }
*/

    @SuppressLint("ShowToast")
    @Override
    public void onReceive(Context context, Intent intent) {
       // HomeActivity activity = (HomeActivity) context;
        mContext = context;
        dao = new DAO(mContext);
        Preferences.getInstance().loadPreferences(mContext);
        draftedList = dao.getAllDraftIssues();
        for (int i = 0; i < draftedList.size(); i++) {
            try {
                id = draftedList.get(i).getCaseId();
                primaryDetails = new JSONObject(draftedList.get(i).getIssueDetails());
                damageDetails = new JSONObject(draftedList.get(i).getDamageDetails());
                beneficiaryDetails = new JSONObject(draftedList.get(i).getBenefeciaryDetails());
                documentDetails = new JSONObject(draftedList.get(i).getDocDetails());
                if (documentDetails.has("data")) {
                    JSONArray docArray = documentDetails.getJSONArray("data");
                    for (int j = 0; j < docArray.length(); j++) {
                        if (docArray.getJSONObject(j).getString("status").equals("F"))
                            uploadContent(docArray.getJSONObject(j).getString("docUrl"), j);
                    }
                    //for(int i=0;)
                }
                submitIssueAPI(id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(i==draftedList.size()-1)
            {
                context.sendBroadcast(new Intent("UPLOAD"));
            }
        }
    }

    protected void uploadContent(String path, final int pos) {
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        try {
            Log.e("FILE", "FILE====" + path);
            params.put("file", new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.BASE_URL + "uploadIncidentDoc", params,
                new AsyncHttpResponseHandler(Looper.getMainLooper()) {
                    @Override
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        try {
                            String uploadedFilePath = new String(bytes);
                            documentDetails.getJSONArray("data").getJSONObject(pos).put("docUrl", uploadedFilePath);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {

                    }


                });

    }

    private void submitIssueAPI(final String issueId) {
        RequestQueue queue = VolleySingleton.getInstance(mContext).getRequestQueue();
        String url = AppConstants.BASE_URL + "reportIncident";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        DAO dao = new DAO(mContext);
                        dao.deleteCase(issueId);

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(mContext);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(mContext, "Could not connect to server", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(mContext);
                params.put("officialId", Preferences.getInstance().officialId);
                params.put("primaryDetailsJSON", primaryDetails.toString());
                params.put("damageDetailsJSON", damageDetails.toString());
                params.put("beneficiaryDetailsJSON", beneficiaryDetails.toString());
                params.put("docUploadJSON", documentDetails.toString());
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(mContext)) {
            queue.add(requestObject);
        } else {

           // Utils.showToast(mContext, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
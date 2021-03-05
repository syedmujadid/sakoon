package com.mobiquel.udhampur.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseActivity;
import com.mobiquel.udhampur.data.DataManager;
import com.mobiquel.udhampur.data.preferences.PrefKeys;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.ui.home.HomeActivity;
import com.mobiquel.udhampur.utils.GPSTracker;
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
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView {


    @BindView(R.id.et_user_email)
    EditText etUserEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.showPwd)
    ImageView showPwd;
    @BindView(R.id.forgotPasswordLabel)
    TextView forgotPasswordLabel;
    @BindView(R.id.retrievedPassword)
    TextView retrievedPassword;
    @BindView(R.id.sosLayout)
    LinearLayout SOSLayout;
    private final int PERMISSION_REQUEST = 0;


    @BindView(R.id.rememberMe)
    CheckBox rememberMe;
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;
    @BindView(R.id.pwdLayout)
    RelativeLayout pwdLayout;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.forgotUsername)
    EditText forgotUsername;
    @BindView(R.id.btn_get_pwd)
    Button btnGetPwd;
    @BindView(R.id.forgotPwdLayout)
    LinearLayout forgotPwdLayout;
    @BindView(R.id.verifyOTPLabel)
    TextView verifyOTPLabel;
    @BindView(R.id.otpMobile)
    EditText otpMobile;
    @BindView(R.id.otp)
    EditText otp;
    @BindView(R.id.btn_get_otp)
    Button btnGetOtp;
    @BindView(R.id.gotBackToLogin2)
    TextView gotBackToLogin2;
    @BindView(R.id.verifyOTPLayout)
    LinearLayout verifyOTPLayout;
    @BindView(R.id.citiSearCase)
    Button citiSearCase;
    @BindView(R.id.citiAddCase)
    Button citiAddCase;
    private boolean isPwdHidden = true;
    private LoginPresenter mPresenter;
    private String mode = "";
    private boolean isOTPAvailable = false;
    private String districtId = "", tehsilId = "", naibatId = "", patwariId = "", villageId = "";
    private ArrayList<String> districtIds = new ArrayList<>();
    private ArrayList<String> tehsilIds = new ArrayList<>();
    private ArrayList<String> naibatIds = new ArrayList<>();
    private ArrayList<String> patwariHalqaIds = new ArrayList<>();
    private ArrayList<String> villageIds = new ArrayList<>();
    private Spinner districtList;
    private Spinner tehsilList;
    private Spinner naibatList;
    private Spinner patwariHalqaList;
    private ProgressBar progress_bar;
    private Spinner villageList;
    private EditText address;
    private GPSTracker gpsTracker;
    private String lat = "", lon = "", reverseAddress = "";
    private BottomSheetDialog dialog;
    private TextView applySOS,response;
    private LinearLayout spinnerLayouts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter(this);
        gpsTracker = new GPSTracker(LoginActivity.this);

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.MOBILE_NUMBER, etUserEmail.getText().toString());
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.USER_PASSWORD, etPassword.getText().toString());
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.REMEMBER_CRED, "1");
                } else {
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.MOBILE_NUMBER, "");
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.USER_PASSWORD, "");
                    new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.REMEMBER_CRED, "0");
                }
            }
        });
        if (new DataManager(LoginActivity.this).getStringFromPreference(PrefKeys.REMEMBER_CRED).equals("1")) {
            //  etUserEmail.setText(new DataManager(LoginActivity.this).getStringFromPreference(PrefKeys.MOBILE_NUMBER));
            //  etPassword.setText(new DataManager(LoginActivity.this).getStringFromPreference(PrefKeys.USER_PASSWORD));
            rememberMe.setChecked(true);
        }

    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void setListeners() {

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void loginMethod() {
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + NetworkConstants.END_POINT_LOGIN;

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        Utils.showToast(LoginActivity.this, responseObject.getString("errorMessage"));
                        if (responseObject.getJSONObject("responseObject").isNull("villageList") || responseObject.getJSONObject("responseObject").getJSONArray("villageList").length() == 0) {
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", responseObject.getJSONObject("responseObject").getJSONArray("villageList"));
                            Preferences.getInstance().villageName = jsonObject.toString();

                        }
                        if (responseObject.getJSONObject("responseObject").isNull("damageList") || responseObject.getJSONObject("responseObject").getJSONArray("damageList").length() == 0) {
                        } else {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject catJsonObject = new JSONObject();
                            jsonObject.put("data", responseObject.getJSONObject("responseObject").getJSONArray("damageList"));
                            catJsonObject.put("data", responseObject.getJSONObject("responseObject").getJSONArray("damageCategoryList"));

                            Preferences.getInstance().damageList = jsonObject.toString();
                            Preferences.getInstance().damageCategList = catJsonObject.toString();

                        }

                        new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.MOBILE_NUMBER, etUserEmail.getText().toString());
                        new DataManager(LoginActivity.this).saveStringInPreference(PrefKeys.USER_PASSWORD, etPassword.getText().toString());

                        Preferences.getInstance().officialId = responseObject.getJSONObject("responseObject").getString("officialId");
                        Preferences.getInstance().name = responseObject.getJSONObject("responseObject").getString("name");
                        Preferences.getInstance().mobile = responseObject.getJSONObject("responseObject").getString("mobile");
                        Preferences.getInstance().designation = responseObject.getJSONObject("responseObject").getString("designation");
                        Preferences.getInstance().districtId = responseObject.getJSONObject("responseObject").getString("districtId");
                        Preferences.getInstance().districtName = responseObject.getJSONObject("responseObject").getString("districtName");
                        Preferences.getInstance().villageId = responseObject.getJSONObject("responseObject").getString("villageIds");
                        Preferences.getInstance().tehsilId = responseObject.getJSONObject("responseObject").getString("tehsilIds");
                        //       Preferences.getInstance().tehsilName = responseObject.getJSONObject("responseObject").getString("tehsilName");
                        //    Preferences.getInstance().area = responseObject.getJSONObject("responseObject").getString("area");
                        Preferences.getInstance().level = responseObject.getJSONObject("responseObject").getString("level");
                        Preferences.getInstance().token = responseObject.getJSONObject("responseObject").getString("token");
                        Preferences.getInstance().savePreferences(LoginActivity.this);
                        new DataManager(LoginActivity.this).saveBooleanInPreference(PrefKeys.IS_LOGGED_IN, true);
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();


                        //finish();

                    } else {
                        showSnackBar("Invalid Credentials!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> data = new HashMap<String, String>();
                data.put("username", etUserEmail.getText().toString());
                data.put("password", etPassword.getText().toString());

                return data;
            }
        };
        queue.add(requestObject);
    }


    private void getPwdMethod() {
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "forgotOfficialPassword";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());
                        retrievedPassword.setVisibility(View.VISIBLE);
                        retrievedPassword.setText("Your Password: " + responseObject.getString("responseObject"));

                    } else {
                        showSnackBar("Sorry! this mobile number isn't present! Please enter correct number.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> data = new HashMap<String, String>();
                data.put("username", forgotUsername.getText().toString());
                return data;
            }
        };
        queue.add(requestObject);
    }

    @Override
    public void callLoginAPI() {
        showProgressBar();
        loginMethod();

    }

    @Override
    public void callGetPwdAPI() {
        showProgressBar();
        getPwdMethod();
    }

    @OnClick({R.id.showPwd, R.id.sosLayout, R.id.resendOTPLabel, R.id.citiSearCase, R.id.citiAddCase, R.id.btn_get_otp, R.id.forgotPasswordLabel, R.id.btn_sign_in, R.id.btn_get_pwd, R.id.gotBackToLogin, R.id.gotBackToLogin2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showPwd:
                if (isPwdHidden) {
                    isPwdHidden = false;
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPwd.setImageResource(R.drawable.showpassword_hide);
                } else {
                    isPwdHidden = true;
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPwd.setImageResource(R.drawable.showpassword);
                }
                break;
            case R.id.resendOTPLabel:
                resendOTPMethod();
                break;
            case R.id.sosLayout:
                if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        || (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    Activity activity = (Activity) LoginActivity.this;
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST);
                } else {
                    if (gpsTracker != null) {
                        lat = String.valueOf(gpsTracker.getLatitude());
                        lon = String.valueOf(gpsTracker.getLongitude());
                    }
                    getReverseGioCoding();
                    mode = "SOS";
                    verifyOTPLabel.setText("SOS (Emergency)");
                    loginLayout.setVisibility(View.GONE);
                    verifyOTPLayout.setVisibility(View.VISIBLE);
                }


                break;

            case R.id.forgotPasswordLabel:
                loginLayout.setVisibility(View.GONE);
                forgotPwdLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.citiSearCase:
                mode = "SEARCH";
                verifyOTPLabel.setText("SEARCH CASE");
                loginLayout.setVisibility(View.GONE);
                verifyOTPLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.citiAddCase:
                mode = "ADD";
                verifyOTPLabel.setText("ADD CASE");
                loginLayout.setVisibility(View.GONE);
                verifyOTPLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_get_otp:
                if (isOTPAvailable) {
                    if (otp.getText().toString().equals("") || otp.getText().toString().length() != 4)
                        showSnackBar("Please enter correct OTP");
                    else {
                        verifyOTPMethod();
                    }
                } else {
                    if (otpMobile.getText().toString().equals(""))
                        showSnackBar("Please enter mobile number");
                    else if (!Utils.validatePhoneNumber(otpMobile.getText().toString()))
                        showSnackBar("Please enter valid mobile number");
                    else {
                        getOTPMethod();
                    }
                }
                break;
            case R.id.btn_sign_in:
                if (!Utils.validatePhoneNumber(etUserEmail.getText().toString()))
                    showSnackBar("Please enter valid phone numer");
                else if (etPassword.getText().toString().equals(""))
                    showSnackBar("Please enter password");
                else
                    mPresenter.signIn();
                break;
            case R.id.btn_get_pwd:
                if (!Utils.validatePhoneNumber(forgotUsername.getText().toString()))
                    showSnackBar("Please enter valid phone numer");
                else
                    mPresenter.getPwd();
                break;
            case R.id.gotBackToLogin:
                forgotPwdLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            case R.id.gotBackToLogin2:
                verifyOTPLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                otpMobile.setText("");
                otp.setText("");
                otp.setEnabled(false);
                btnGetOtp.setText("GET OTP");
                isOTPAvailable = false;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (forgotPwdLayout.getVisibility() == View.VISIBLE) {
            forgotPwdLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        } else
            finish();

    }

    private void getOTPMethod() {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "generateOtp";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        showSnackBar("OTP sent successfully.");
                        Log.e("RESPO", responseObject.toString());
                        otp.setEnabled(true);
                        isOTPAvailable = true;
                        otp.requestFocus();
                        btnGetOtp.setText("Verify OTP");
                    } else {
                        //showSnackBar("Sorry! this mobile number isn't present! Please enter correct number.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", otpMobile.getText().toString());
                return data;
            }
        };
        queue.add(requestObject);
    }

    private void verifyOTPMethod() {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "verifyOtp";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        if (mode.equals("SOS")){
                            lat=String.valueOf(gpsTracker.getLatitude());
                            lon=String.valueOf(gpsTracker.getLongitude());
                            getReverseGioCoding();
                            openBottomSheet();
                        }
                        else {
                            Log.e("RESPO", responseObject.toString());
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            intent.putExtra("SOURCE", mode);
                            intent.putExtra("MOBILE", otpMobile.getText().toString());
                            startActivity(intent);
                        }

                    } else {
                        showSnackBar("Please enter correct OTP.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> data = new HashMap<String, String>();
                data.put("otp", otp.getText().toString());
                data.put("mobile", otpMobile.getText().toString());
                return data;
            }
        };
        queue.add(requestObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyOTPLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
        otpMobile.setText("");
        otp.setText("");
        otp.setEnabled(false);
        btnGetOtp.setText("GET OTP");
        isOTPAvailable = false;
    }

    private void resendOTPMethod() {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "resendOtp";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Log.e("RESPO", responseObject.toString());


                    } else {
                        showSnackBar("Please enter correct OTP.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", otpMobile.getText().toString());
                return data;
            }
        };
        queue.add(requestObject);
    }

    private void openBottomSheet() {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.layout_sos_sheet, null);
            dialog = new BottomSheetDialog(LoginActivity.this);
            districtList = (Spinner) dialogView.findViewById(R.id.districtList);
            tehsilList = (Spinner) dialogView.findViewById(R.id.tehsilList);
            naibatList = (Spinner) dialogView.findViewById(R.id.naibatList);
            patwariHalqaList = (Spinner) dialogView.findViewById(R.id.patwariHalqaList);
            villageList = (Spinner) dialogView.findViewById(R.id.villageList);
            address = (EditText) dialogView.findViewById(R.id.address);
            progress_bar = (ProgressBar) dialogView.findViewById(R.id.progress_bar);
            applySOS = (TextView) dialogView.findViewById(R.id.applySOS);
            response = (TextView) dialogView.findViewById(R.id.response);
            spinnerLayouts = (LinearLayout) dialogView.findViewById(R.id.l2);
            address.setText(reverseAddress);
            getDistrictList();
            districtList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (districtIds.size() > 0)
                        districtId = districtIds.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            tehsilList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (tehsilIds.size() > 0) {
                        tehsilId = tehsilIds.get(i);
                        getNaibatList(tehsilId);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            naibatList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (naibatIds.size() > 0) {
                        naibatId = naibatIds.get(i);
                        getPatwariHalqaList(naibatId);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            patwariHalqaList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (patwariHalqaIds.size() > 0) {
                        patwariId = patwariHalqaIds.get(i);
                        getVillageList(patwariId);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            villageList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (villageIds.size() > 0) {
                        villageId = villageIds.get(i);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            applySOS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(applySOS.getText().toString().equals("CANCEL")){
                        dialog.cancel();
                        verifyOTPLayout.setVisibility(View.GONE);
                        loginLayout.setVisibility(View.VISIBLE);
                        otpMobile.setText("");
                        otp.setText("");
                        otp.setEnabled(false);
                        btnGetOtp.setText("GET OTP");
                        isOTPAvailable = false;

                    }
                    else {
                        lat=String.valueOf(gpsTracker.getLatitude());
                        lon=String.valueOf(gpsTracker.getLongitude());
                        getReverseGioCoding();
                        reportSOS();
                    }

                }
            });

            dialog.setContentView(dialogView);
            dialog.getWindow()
                    .findViewById(R.id.design_bottom_sheet)
                    .setBackgroundResource(android.R.color.transparent);

            dialog.show();
        } catch (Exception e) {
        }
    }


    private void getDistrictList() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
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
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(LoginActivity.this,
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
                Preferences.getInstance().loadPreferences(LoginActivity.this);
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
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
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
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(LoginActivity.this,
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
                Preferences.getInstance().loadPreferences(LoginActivity.this);
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
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getNaibatListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
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
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(LoginActivity.this,
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Preferences.getInstance().loadPreferences(LoginActivity.this);
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


    private void getVillageList(final String id) {
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getVillageListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
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
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(LoginActivity.this,
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Preferences.getInstance().loadPreferences(LoginActivity.this);
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


    private void reportSOS() {
        progress_bar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "reportSOS";

        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                progress_bar.setVisibility(View.GONE);
                spinnerLayouts.setVisibility(View.GONE);
                response.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject=new JSONObject(responseData);
                    response.setText(jsonObject.getString("responseObject"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                applySOS.setText("CANCEL");
                Utils.showToast(LoginActivity.this, "SOS Posted successfully!");
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", otpMobile.getText().toString());
                params.put("lat", lat);
                params.put("lon", lon);
                params.put("districtId", districtId);
                params.put("tehsilId", tehsilId);
                params.put("naibatId", naibatId);
                params.put("patwarHalqaId", patwariId);
                params.put("villageId", villageId);
                params.put("description", address.getText().toString());
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "citizen");
                params.put("token", Preferences.getInstance().token);

                return params;
            }
        };
        queue.add(requestObject);
    }

    private void getPatwariHalqaList(final String id) {
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "getPatwariHalqaListWithoutToken";

        final StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
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
                        ArrayAdapter cityAdapter = new ArrayAdapter<String>(LoginActivity.this,
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Preferences.getInstance().loadPreferences(LoginActivity.this);
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

    private void getReverseGioCoding() {
        RequestQueue queue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key=AIzaSyCBWLM36fNxznbt237OhpqZ6kr2AMW-CTI";
        //String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=28.579134694708394, 77.26639331065368&key=AIzaSyCBWLM36fNxznbt237OhpqZ6kr2AMW-CTI";

        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject response = new JSONObject(responseData);
                    JSONArray addressArray = response.getJSONArray("results");
                    reverseAddress = addressArray.getJSONObject(1).getString("formatted_address");

                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        queue.add(requestObject);
    }
}

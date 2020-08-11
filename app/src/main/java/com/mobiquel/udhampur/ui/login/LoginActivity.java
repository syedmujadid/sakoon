package com.mobiquel.udhampur.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseActivity;
import com.mobiquel.udhampur.data.DataManager;
import com.mobiquel.udhampur.data.preferences.PrefKeys;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.ui.home.HomeActivity;
import com.mobiquel.udhampur.ui.settings.CitizenAddIncident;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter(this);
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
            etUserEmail.setText(new DataManager(LoginActivity.this).getStringFromPreference(PrefKeys.MOBILE_NUMBER));
            etPassword.setText(new DataManager(LoginActivity.this).getStringFromPreference(PrefKeys.USER_PASSWORD));
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

    @OnClick({R.id.showPwd, R.id.resendOTPLabel, R.id.citiSearCase, R.id.citiAddCase, R.id.btn_get_otp, R.id.forgotPasswordLabel, R.id.btn_sign_in, R.id.btn_get_pwd, R.id.gotBackToLogin, R.id.gotBackToLogin2})
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
                isOTPAvailable=false;
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
                        Log.e("RESPO", responseObject.toString());
                        Intent intent = new Intent(LoginActivity.this,CitizenAddIncident.class);
                        intent.putExtra("SOURCE",mode);
                        intent.putExtra("MOBILE", otpMobile.getText().toString());
                        startActivity(intent);

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
        isOTPAvailable=false;
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
}

package com.mobiquel.udhampur.ui.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseActivity;
import com.mobiquel.udhampur.dao.DAO;
import com.mobiquel.udhampur.data.DataManager;
import com.mobiquel.udhampur.data.preferences.PrefKeys;
import com.mobiquel.udhampur.dialogs.UpdateAppDialog;
import com.mobiquel.udhampur.dialogs.UpdatePasswordDialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.network.NetworkConstants;
import com.mobiquel.udhampur.ui.addissue.AddIssue;
import com.mobiquel.udhampur.ui.home.listcase.CompleteFragment;
import com.mobiquel.udhampur.ui.home.listcase.DraftCaseFragment;
import com.mobiquel.udhampur.ui.home.listcase.OtherPendingCaseFragment;
import com.mobiquel.udhampur.ui.home.listcase.PendingCaseFragment;
import com.mobiquel.udhampur.ui.home.listcase.ReturnedFragment;
import com.mobiquel.udhampur.ui.login.LoginActivity;
import com.mobiquel.udhampur.ui.settings.FirstAidActivity;
import com.mobiquel.udhampur.utils.CustomTypeFaceSpan;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements HomeView, BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.fl_container_home)
    FrameLayout flContainerHome;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    @BindView(R.id.tab_name)
    TextView tabName;
    @BindView(R.id.appVersion)
    TextView appVersion;
    @BindView(R.id.addIssue)
    FloatingActionButton addIssue;
    @BindView(R.id.notfication)
    FloatingActionButton notfication;

    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.nav_view)
    NavigationView  navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.menu)
    ImageView menu;

    private UpdatePasswordDialog mUpdatePwdDialog;
    private UpdateAppDialog mUpdateDialog;
    private HomePresenter mPresenter;
    private List<Fragment> mFragmentList;
    private Fragment selectedFragment;
    private DAO dao;
    private String pendingCount = "-", draftCount = "-", returnCount = "-", complCount = "-", otherPendingCount = "-";

    private String regid;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private GoogleCloudMessaging gcm;
    private Context context;


    @Override
    protected int getResourceId() {
        return R.layout.activity_home;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabName.setText(getResources().getString(R.string.app_name));
        mPresenter.setUpBottomNavigationView();
        if (bottomNavigation != null) {
            ViewCompat.setElevation(bottomNavigation, 30);
        }
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.closeDrawer(Gravity.START);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        Menu m = navView.getMenu();

        String version = getAppVersion(HomeActivity.this);
        appVersion.setText("App Ver. " + version);
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }

        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, "0");
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.name);
        TextView mobile = (TextView) headerView.findViewById(R.id.mobile);
        TextView designatio = (TextView) headerView.findViewById(R.id.designation);
        Preferences.getInstance().loadPreferences(HomeActivity.this);
        name.setText(Preferences.getInstance().name);
        mobile.setText("+91-" + Preferences.getInstance().mobile);
        designatio.setText(Preferences.getInstance().designation);

        Menu menu = bottomNavigation.getMenu();
        MenuItem nav_draft = menu.findItem(R.id.nav_draft);
        MenuItem nav_returned = menu.findItem(R.id.nav_returned);

        if (Preferences.getInstance().level.equals("1")) {
            addIssue.setVisibility(View.VISIBLE);
            nav_draft.setTitle("Draft");
        } else {
            addIssue.setVisibility(View.GONE);
            nav_draft.setTitle("Othr. Pending");
        }
        if (Preferences.getInstance().level.equals("7")) {
            notfication.setVisibility(View.VISIBLE);
            nav_returned.setTitle("Cancelled");
        } else {
            nav_returned.setTitle("Returned");
        }

        if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("7")) {

        } else {
            bottomNavigation.getMenu().removeItem(R.id.nav_draft);
        }

        showBadge();
        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, pendingCount);

        registerReceiver(broadcastReceiver, new IntentFilter("UPLOAD"));

    }


    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void initVariables() {
        context = HomeActivity.this;
        mPresenter = new HomePresenter(this);
        mFragmentList = new ArrayList<>();
        dao = new DAO(HomeActivity.this);
        mUpdateDialog = new UpdateAppDialog(HomeActivity.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                Utils.showToast(HomeActivity.this, "Taking you to Google playstore!");
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                mUpdateDialog.cancel();
            }

            @Override
            public void onNegativeButtonClick() {

            }
        });

        mUpdatePwdDialog = new UpdatePasswordDialog(HomeActivity.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                List<String> data = mUpdatePwdDialog.checkSolution();
                if (data.get(1).equals("0")) {
                    Utils.showToast(HomeActivity.this, data.get(0));
                } else {
                    String newPassword = data.get(0);
                    changePwd(newPassword);
                    mUpdatePwdDialog.cancel();
                }
            }

            @Override
            public void onNegativeButtonClick() {
                mUpdatePwdDialog.cancel();
            }
        });
    }

    private void showBadge() {
        Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, pendingCount);
        Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, complCount);
        if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("7")) {
            if (Preferences.getInstance().level.equals("1")) {
                Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, "" + dao.getAllDraftIssues().size());
                Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, returnCount);
            } else {
                Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, otherPendingCount);
                Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, returnCount);
            }

        } else {

            Utils.showBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, returnCount);
        }


    }

    @Override
    public void setListeners() {
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    public void setUpBottomNavigationView() {
        Preferences.getInstance().loadPreferences(HomeActivity.this);
        mFragmentList.clear();
        selectedFragment = new PendingCaseFragment();
        if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("7")) {
            mFragmentList.add(selectedFragment);
            if (Preferences.getInstance().level.equals("1"))
                mFragmentList.add(new DraftCaseFragment());
            else
                mFragmentList.add(new OtherPendingCaseFragment());
            mFragmentList.add(new CompleteFragment());
            mFragmentList.add(new ReturnedFragment());
        } else {
            mFragmentList.add(selectedFragment);
            mFragmentList.add(new CompleteFragment());
            mFragmentList.add(new ReturnedFragment());
        }


        for (Fragment fragment : mFragmentList) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fl_container_home, fragment, fragment.getClass().getName());
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commit();
        }
        bottomNavigation.setSelectedItemId(R.id.nav_pending);


    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            FragmentTransaction fragmentTransaction;

            switch (menuItem.getItemId()) {


                case R.id.nav_pending:
                    fragment = mFragmentList.get(0);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(selectedFragment);
                    fragmentTransaction.show(fragment);
                    fragmentTransaction.commit();
                    selectedFragment = fragment;
                    showBadge();
                    Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, pendingCount);
                    return true;
                case R.id.nav_draft:
                    fragment = mFragmentList.get(1);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(selectedFragment);
                    fragmentTransaction.show(fragment);
                    fragmentTransaction.commit();
                    selectedFragment = fragment;
                    showBadge();
                    if (Preferences.getInstance().level.equals("1")) {
                        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, "" + dao.getAllDraftIssues().size());
                    } else {
                        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, otherPendingCount);
                    }

                    return true;

                case R.id.nav_returned:
                    if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("7")) {
                        fragment = mFragmentList.get(3);
                    } else {
                        fragment = mFragmentList.get(2);
                    }

                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(selectedFragment);
                    fragmentTransaction.show(fragment);
                    fragmentTransaction.commit();
                    selectedFragment = fragment;

                    showBadge();
                    Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, returnCount);
                    return true;
                case R.id.nav_completed:
                    if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("7")) {
                        fragment = mFragmentList.get(2);
                    } else {
                        fragment = mFragmentList.get(1);
                    }
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(selectedFragment);
                    fragmentTransaction.show(fragment);
                    fragmentTransaction.commit();
                    selectedFragment = fragment;

                    showBadge();
                    Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, complCount);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent in;
        switch (menuItem.getItemId()) {
            case R.id.nav_about:

                drawerLayout.closeDrawers();
                menuItem.setCheckable(false);

                return true;
            case R.id.nav_settings:
                mUpdatePwdDialog.show();
                mUpdatePwdDialog.setData(new DataManager(HomeActivity.this).getStringFromPreference(PrefKeys.USER_PASSWORD));
                drawerLayout.closeDrawers();
                return true;

            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logoutMethod();

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
                alert.setTitle("Log out");
                alert.show();

                drawerLayout.closeDrawers();
                menuItem.setCheckable(false);
                return true;


            default:
                return false;
        }

    }


    @OnClick({R.id.menu, R.id.addIssue, R.id.notfication})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addIssue:
                intent = new Intent(HomeActivity.this, AddIssue.class);
                intent.putExtra("SOURCE", "ADD");
                startActivity(intent);
                overridePendingTransition(R.anim.left_out, R.anim.right_in);

                break;
            case R.id.notfication:
                intent = new Intent(HomeActivity.this, FirstAidActivity.class);
                intent.putExtra("SOURCE", "ADD");
                startActivity(intent);
                overridePendingTransition(R.anim.left_out, R.anim.right_in);

                break;
        }
    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForGCM();
    }

    private void logoutMethod() {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(HomeActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + NetworkConstants.END_POINT_LOGOUT;

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        //Utils.showToast(HomeActivity.this, "Note added successfully!");
                        new DataManager(HomeActivity.this).saveBooleanInPreference(PrefKeys.IS_LOGGED_IN, false);
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_out, R.anim.left_in);
                        finish();
                        //finish();

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
                data.put("officialId", Preferences.getInstance().officialId);
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "Official");

                return data;
            }
        };
        queue.add(requestObject);
    }

    private void registerForGCM() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);
            registerInBackground();
        } else {
            Log.e("", "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Activity activity = (Activity) context;
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("", "This device is not supported.");

            }
            return false;
        }
        return true;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersionCode(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.trim().equals("")) {
            Log.e("", "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersionCode(context);
        if (registeredVersion != currentVersion) {
            Log.e("", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(NetworkConstants.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    registerPushNotificationid();
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {

        return context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
    }


    private void registerPushNotificationid() {
        RequestQueue queue = VolleySingleton.getInstance(HomeActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "registerOfficialPushNotificationId/";
        Log.e("URL_VErSION", url);

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {

                Log.e("RESPO_NOTI", responseData);
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        //Utils.showToast(HomeActivity.this, "Note added successfully!");
                        if (responseObject.getJSONObject("responseObject").getString("appVersion").equals(getAppVersion(HomeActivity.this))) {
                        } else {
                            mUpdateDialog.show();
                        }
                        pendingCount = responseObject.getJSONObject("responseObject").getString("pendingCount");
                        complCount = responseObject.getJSONObject("responseObject").getString("approvedCount");
                        if (Preferences.getInstance().level.equals("7")) {
                            returnCount = responseObject.getJSONObject("responseObject").getString("cancelledCount");
                            otherPendingCount = responseObject.getJSONObject("responseObject").getString("pendingOtherCount");

                        } else {
                            returnCount = responseObject.getJSONObject("responseObject").getString("returnedCount");
                        }

                        showBadge();

                        if (selectedFragment == mFragmentList.get(0))
                            Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, pendingCount);

                        if (Preferences.getInstance().level.equals("1")) {

                        } else if (Preferences.getInstance().level.equals("7")) {
                            if (selectedFragment == mFragmentList.get(1))
                                Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, otherPendingCount);
                            if (selectedFragment == mFragmentList.get(2))
                                Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, complCount);

                            if (selectedFragment == mFragmentList.get(3))
                                Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, returnCount);

                        } else {
                            if (selectedFragment == mFragmentList.get(1))
                                Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, complCount);
                            if (selectedFragment == mFragmentList.get(2))
                                Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, returnCount);

                        }

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(HomeActivity.this);
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

                String android_id = android.provider.Settings.Secure.getString(HomeActivity.this.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
                Preferences.getInstance().loadPreferences(HomeActivity.this);
                Map<String, String> data = new HashMap<String, String>();
                data.put("officialId", Preferences.getInstance().officialId);
                data.put("notificationId", regid);
                data.put("deviceOS", "Android");
                data.put("deviceId", android_id);
                data.put("model", "" + Build.MODEL);
                data.put("make", "" + Build.MANUFACTURER);
                data.put("appVersion", "" + getAppVersion(HomeActivity.this));
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "Official");

                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(HomeActivity.this)) {
            queue.add(requestObject);
        } else {

            Toast.makeText(HomeActivity.this, "Please check internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDraftCase() {
        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_draft, "" + dao.getAllDraftIssues().size());

    }

    public void updatePendingCase(String cnt) {
        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_pending, cnt);

    }

    public void updateCompletedCase(String cnt) {
        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_completed, cnt);
    }

    public void updatereturnedCase(String cnt) {

        Utils.changeColorBadge(HomeActivity.this, bottomNavigation, R.id.nav_returned, cnt);

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
            refreshList();
        }
    };

    public void refreshList() {
        Fragment fragment;
        // getting ViewPerformanceFragment from adapter at position 0
        fragment = mFragmentList.get(1);
        ((DraftCaseFragment) fragment).refrehData();

    }

    private void changePwd(final String pwd) {
        showProgressBar();
        RequestQueue queue = VolleySingleton.getInstance(HomeActivity.this).getRequestQueue();
        String url = "";
        url = NetworkConstants.BASE_URL + "changeOfficialPassword/";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                Log.e("RESPO_NOTI", responseData);
                hideProgressBar();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        new DataManager(HomeActivity.this).saveStringInPreference(PrefKeys.USER_PASSWORD, pwd);
                        Utils.showToast(HomeActivity.this, "Password updated successfully!");

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(HomeActivity.this);
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

                String android_id = android.provider.Settings.Secure.getString(HomeActivity.this.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
                Preferences.getInstance().loadPreferences(HomeActivity.this);
                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", new DataManager(HomeActivity.this).getStringFromPreference(PrefKeys.MOBILE_NUMBER));
                data.put("password", new DataManager(HomeActivity.this).getStringFromPreference(PrefKeys.USER_PASSWORD));
                data.put("newPassword", pwd);
                data.put("token", Preferences.getInstance().token);
                data.put("tokenUserId", Preferences.getInstance().officialId);
                data.put("tokenUserType", "Official");

                Log.e("PARAMS", data.toString());
                return data;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(HomeActivity.this)) {
            queue.add(requestObject);
        } else {

            Toast.makeText(HomeActivity.this, "Please check internet connection!", Toast.LENGTH_SHORT).show();
        }
    }
}

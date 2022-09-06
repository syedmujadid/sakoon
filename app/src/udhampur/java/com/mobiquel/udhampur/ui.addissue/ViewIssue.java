package com.mobiquel.udhampur.ui.addissue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.dialogs.EnterRemarksDialog;
import com.mobiquel.udhampur.dialogs.UpdateDamageDialog;
import com.mobiquel.udhampur.dialogs.UploadImageDialog;
import com.mobiquel.udhampur.dialogs.View_Beneficiary_Dialog;
import com.mobiquel.udhampur.dialogs.View_Image_Dialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.BeneficiaryModel;
import com.mobiquel.udhampur.pojo.DamageDetailModel;
import com.mobiquel.udhampur.pojo.DocListModel;
import com.mobiquel.udhampur.ui.settings.FinaceListActivity;
import com.mobiquel.udhampur.utils.AppConstants;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.utils.ContentUriUtils;

public class ViewIssue extends AppCompatActivity {


    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.tab_name)
    TextView tabName;

    @BindView(R.id.step1Label)
    TextView step1Label;
    @BindView(R.id.step2Label)
    TextView step2Label;
    @BindView(R.id.step3Label)
    TextView step3Label;
    @BindView(R.id.step4Label)
    TextView step4Label;

    @BindView(R.id.mainFormlayout)
    NestedScrollView parentLayout;
    @BindView(R.id.incidentDate)
    TextView incidentDate;
    @BindView(R.id.applicantName)
    TextView applicantName;
    @BindView(R.id.parentName)
    TextView parentName;
    @BindView(R.id.applicantMobile)
    TextView applicantMobile;
    @BindView(R.id.naturalCalamity)
    TextView naturalCalamity;
    @BindView(R.id.firstAid)
    TextView firstAid;
    @BindView(R.id.villageName)
    TextView villageName;
    @BindView(R.id.beneficiaryCount)
    TextView beneficiaryCount;

    private String clickSource = "";
    @BindView(R.id.docList)
    RecyclerView docRecylerView;
    @BindView(R.id.beneficiaryList)
    RecyclerView beneficiaryList;
    @BindView(R.id.damageDetailList)
    RecyclerView damageDetailList;

    @BindView(R.id.financelabel)
    TextView financelabel;
    @BindView(R.id.totalAmntLabel)
    TextView totalAmntLabel;

    @BindView(R.id.footer)
    LinearLayout footerLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
/*
    @BindView(R.id.prev)
    FloatingActionButton prev;
    @BindView(R.id.next)
    FloatingActionButton next;
*/
    private static final int MY_PERMISSIONS_CAMERA = 120;
    private final int PERMISSION_REQUEST = 0;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private Uri imageUri;
    private String uploadedFilePath;
    private int REQUEST_CAMERA = 0;
    @BindView(R.id.approve)
    Button approve;
    @BindView(R.id.reject)
    Button reject;
    @BindView(R.id.cancel)
    Button cancel;
    private int curretBeneficiaryPos = 0;

    private DocumentListAdapter_View mAdapter;
    private BeneficiaryListAdapter beneficaryAapter;
    private DamageListAdapter damageListAapter;

    private View_Image_Dialog mDialog;
    private int selePos = -1;

    private List<BeneficiaryModel> listOfBeneficiary = new ArrayList<>();
    private List<DamageDetailModel> damageDetailModelList = new ArrayList<>();
    private List<DocListModel> documentList = new ArrayList<>();
    private EnterRemarksDialog enterRemarksDialog;
    private JSONObject dataJSON;
    private String approRejecType = "";
    private String incidentId = "";
    private String fileUploadType = "";
    private String uploadMode = "";
    private UploadImageDialog uploadImageDialog;
    private JSONObject docUploadJSON = new JSONObject();
    private JSONObject damageDetailsJSON = new JSONObject();
    private UpdateDamageDialog mUpdateDamageDialog;
    private int selectedDamagePos = -1;
    private boolean updateDone = false;
    private String remarkValue = "";
    private List<String> damaeType = new ArrayList<>();
    private  File outputDirectory;

    private View_Beneficiary_Dialog mDialogViewBeneficiary;
    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_issue);
        ButterKnife.bind(this);
        mAdapter = new DocumentListAdapter_View(documentList);
        docRecylerView.setLayoutManager(new LinearLayoutManager(ViewIssue.this));
        docRecylerView.setAdapter(mAdapter);
        mDialog = new View_Image_Dialog(ViewIssue.this);
        beneficaryAapter = new BeneficiaryListAdapter(listOfBeneficiary,"VIEW", new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {

            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                int i=position+1;
                mDialogViewBeneficiary.show();
                mDialogViewBeneficiary.setData(listOfBeneficiary.get(position), "VIEW",i);
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {

            }
        });

        beneficiaryList.setLayoutManager(new LinearLayoutManager(ViewIssue.this, LinearLayoutManager.HORIZONTAL, false));
        beneficiaryList.setAdapter(beneficaryAapter);

        financelabel.setVisibility(View.VISIBLE);
        mAdapter = new DocumentListAdapter_View(documentList);
        docRecylerView.setLayoutManager(new LinearLayoutManager(ViewIssue.this));
        docRecylerView.setAdapter(mAdapter);
        damageListAapter = new DamageListAdapter(damageDetailModelList, "STATUS", new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                selectedDamagePos = position;
                DamageDetailModel model = damageDetailModelList.get(position);
                mUpdateDamageDialog.show();
                mUpdateDamageDialog.setData(model);

            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewIssue.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to remove damage?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                damageDetailModelList.remove(position);

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
                alert.show();
                //
            }
        });
        damageDetailList.setLayoutManager(new LinearLayoutManager(ViewIssue.this));
        damageDetailList.setAdapter(damageListAapter);
        try {
            Preferences.getInstance().loadPreferences(ViewIssue.this);
            dataJSON = new JSONObject(getIntent().getExtras().getString("DATA_JSON"));
            if (getIntent().getExtras().getString("SOURCE").equals("COMPLETE")) {
                footerLayout.setVisibility(View.GONE);
            } else {
                if (dataJSON.getString("issueAtLevel").equals("null") && Preferences.getInstance().level.equals("7"))
                    footerLayout.setVisibility(View.VISIBLE);
                else if (Preferences.getInstance().level.equals(dataJSON.getString("issueAtLevel")))
                    footerLayout.setVisibility(View.VISIBLE);
                else
                    footerLayout.setVisibility(View.GONE);
            }

            incidentId = dataJSON.getString("incidentId");
            tabName.setText("View Incident #" + incidentId);
            incidentDate.setText("Incident Date: " + dataJSON.getString("incidentDate"));
            applicantName.setText("Relief Claimant: " + dataJSON.getString("applicantName"));
            parentName.setText("Parent Name: " + dataJSON.getString("parentName"));
            naturalCalamity.setText("Natural Calamity: " + dataJSON.getString("calamityType"));
            beneficiaryCount.setText("Total Beneficiaries: " + dataJSON.getString("beneficiaryCount"));
            applicantMobile.setText("Mobile: " + dataJSON.getString("mobile"));
            firstAid.setText("First Aid: " + dataJSON.getString("firstAid"));
            villageName.setText("Village: " + dataJSON.getString("villageName"));

            JSONArray damageArray = dataJSON.getJSONArray("damageItemList");
            JSONArray docArray = dataJSON.getJSONArray("indicentDocList");
            JSONArray beneficiaryArray = dataJSON.getJSONArray("beneficiaryList");

            for (int i = 0; i < beneficiaryArray.length(); i++) {
                JSONObject jsonObject = beneficiaryArray.getJSONObject(i);
                BeneficiaryModel model = new BeneficiaryModel();

                model.setTitle(jsonObject.getString("name"));
                model.setName(jsonObject.getString("name"));
                model.setGender(jsonObject.getString("gender"));
                model.setAdharNumber(jsonObject.getString("aadharNumber"));
                model.setAddress(jsonObject.getString("address"));
                model.setPinCode(jsonObject.getString("pincode"));
                model.setContactNumber(jsonObject.getString("contactNumber"));
                model.setShare(jsonObject.getString("percentageShare"));
                model.setAccountHolderName(jsonObject.getString("accountHolder"));
                model.setRelation(jsonObject.getString("relation"));
                model.setAccountNumber(jsonObject.getString("accountNo"));
                model.setBankName(jsonObject.getString("bankName"));
                model.setBranchName(jsonObject.getString("branchName"));
                model.setIfscCode(jsonObject.getString("ifscCode"));
                listOfBeneficiary.add(model);
            }
            beneficaryAapter.notifyDataSetChanged();
            JSONArray damaTypeArray;
            JSONObject damaJSON = new JSONObject(Preferences.getInstance().damageList);
            damaTypeArray = damaJSON.getJSONArray("data");

            for (int i = 0; i < damageArray.length(); i++) {
                JSONObject jsonObject = damageArray.getJSONObject(i);
                DamageDetailModel model = new DamageDetailModel();
                model.setPropertyType(jsonObject.getString("type"));
                model.setDamageDetail(jsonObject.getString("description"));
                model.setQuantity(jsonObject.getString("quantity"));
                model.setBaseAmnt(jsonObject.getString("baseAmount"));
                model.setTotalAmnt(jsonObject.getString("totalAmount"));
                damageDetailModelList.add(model);
                for (int j = 0; j < damaTypeArray.length(); j++) {
                    try {
                        if (damaTypeArray.getJSONObject(j).getString("type").equals(model.getPropertyType())) {
                            damaeType.add(damaTypeArray.getJSONObject(j).getString("categoryId"));
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            damageListAapter.notifyDataSetChanged();

            if (Preferences.getInstance().level.equals("1") || Preferences.getInstance().level.equals("4") || Preferences.getInstance().level.equals("5")) {
                prepareDocData();
                for (int i = 0; i < docArray.length(); i++) {
                    JSONObject jsonObject = docArray.getJSONObject(i);
                    for (int j = 0; j < documentList.size(); j++) {
                        if (jsonObject.getString("type").equals(documentList.get(j).getName())) {
                            documentList.get(j).setFileURL(jsonObject.getString("docUrl"));
                            documentList.get(j).setUploadStatus(true);
                            documentList.get(j).setName(documentList.get(j).getName());
                            documentList.get(j).setLat(jsonObject.getString("lat"));
                            documentList.get(j).setLon(jsonObject.getString("lon"));
                            break;
                        }

                    }
                }
                mAdapter.notifyDataSetChanged();
            } else {
                for (int i = 0; i < docArray.length(); i++) {
                    JSONObject jsonObject = docArray.getJSONObject(i);
                    DocListModel model = new DocListModel();
                    model.setName(jsonObject.getString("type"));
                    model.setFileURL(jsonObject.getString("docUrl"));
                    model.setFormat(jsonObject.getString("format"));
                    documentList.add(model);
                }
                mAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        enterRemarksDialog = new EnterRemarksDialog(ViewIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                String answer = enterRemarksDialog.getSolution();
                // mQuestionList.get(selectedQuestionId).setMarkedAnswer(answer);
                if (answer.equals(""))
                    Utils.showToast(ViewIssue.this, "Please enter remarks!");
                else {
                    enterRemarksDialog.cancel();
                    Utils.hideKeyboard(ViewIssue.this);
                    remarkValue = answer;
                    clickSource = "APPROVE";
                    if (updateDone == true)
                        setUPData();
                    else
                        approveRejectCase(answer);
                }

            }

            @Override
            public void onNegativeButtonClick() {
                enterRemarksDialog.cancel();
            }
        });

        mDialogViewBeneficiary=new View_Beneficiary_Dialog(ViewIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {

            }

            @Override
            public void onNegativeButtonClick() {

            }
        });

        uploadImageDialog = new UploadImageDialog(ViewIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                cameraIntent();
            }

            @Override
            public void onNegativeButtonClick() {
                //uploadImageDilog.cancel();
                galleryIntent();
            }
        });

        financelabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewIssue.this, FinaceListActivity.class);
                intent.putExtra("ID", incidentId);
                startActivity(intent);
            }
        });

        mUpdateDamageDialog = new UpdateDamageDialog(ViewIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                DamageDetailModel model = mUpdateDamageDialog.getData();
                damageDetailModelList.get(selectedDamagePos).setQuantity(model.getQuantity());
                damageDetailModelList.get(selectedDamagePos).setDamageDetail(model.getDamageDetail());
                damageDetailModelList.get(selectedDamagePos).setPropertyType(model.getPropertyType());
                damageListAapter.notifyDataSetChanged();
                updateDone = true;
                mUpdateDamageDialog.cancel();

            }

            @Override
            public void onNegativeButtonClick() {
                mUpdateDamageDialog.cancel();
            }
        });
        if (Preferences.getInstance().level.equals("7"))
            cancel.setVisibility(View.VISIBLE);

        check_folder();
    }

    private void check_folder() {
        //  Log.e("PATH", "=== " + path);
        String path =
                ViewIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + AppConstants.IMAGE_FOLDER;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        outputDirectory= new File(
                ViewIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                AppConstants.IMAGE_FOLDER+File.separator+"sakoon_demo.jpg"
        );
    }

    private void setFormData(int pos) {
       /* int id = genderRadioGroup.getCheckedRadioButtonId();
        personName.setText("Name: " + listOfBeneficiary.get(pos).getName());
        adharAddress.setText(listOfBeneficiary.get(pos).getAddress());
        adharNumber.setText("Aadhaar Number: " + listOfBeneficiary.get(pos).getAdharNumber());
        if (listOfBeneficiary.get(pos).getGender().equals("Male")) {
            maleRg.setChecked(true);
            femaleRg.setChecked(false);
        } else if (listOfBeneficiary.get(pos).getGender().equals("Female")) {
            maleRg.setChecked(false);
            femaleRg.setChecked(true);
        } else {
            maleRg.setChecked(true);
            femaleRg.setChecked(false);
        }
        contactNumber.setText("Mobile: " + listOfBeneficiary.get(pos).getContactNumber());
        pinCode.setText("Pin-code: " + listOfBeneficiary.get(pos).getPinCode());
        percentageShare.setText("Share: " + listOfBeneficiary.get(pos).getShare() + "%");
        accountHolderName.setText("A/C Holder: " + listOfBeneficiary.get(pos).getAccountHolderName());
        applicantRelation.setText("Relation with claimant: " + listOfBeneficiary.get(pos).getRelation());
        accountNumber.setText("A/C No: " + listOfBeneficiary.get(pos).getAccountNumber());
        bankame.setText("Bank: " + listOfBeneficiary.get(pos).getBankName());
        branchName.setText("Branch: " + listOfBeneficiary.get(pos).getBranchName());
        ifscCode.setText("IFSC Code: " + listOfBeneficiary.get(pos).getIfscCode());
*/
    }


    public void showImage(String url, String title) {
        mDialog.show();
        mDialog.setData(url, title);

    }

    public void showUploadDialog(String fileType, String uploadType, int selecPos) {
        this.fileUploadType = fileType;
        this.uploadMode = uploadType;
        this.selePos = selecPos;
        if ((ContextCompat.checkSelfPermission(ViewIssue.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(ViewIssue.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(ViewIssue.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )) {
            Activity activity = (Activity) ViewIssue.this;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = ViewIssue.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            uploadImageDialog.show();
            uploadImageDialog.setDrawableLeft();
        }

    }

    @Override
    public void onBackPressed() {
        if (updateDone == true)
            setUPData();
        else
            finish();
    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.menu, R.id.approve, R.id.reject, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (updateDone == true)
                    setUPData();
                else
                    finish();
                break;
            case R.id.approve:
                validateMethod3("T");

                //approveRejectCase("APPROVE");
                break;
            case R.id.reject:
                approRejecType = "F_REJECT";
                enterRemarksDialog.show();
                //approveRejectCase("REJECT");
                break;
            case R.id.cancel:
                approRejecType = "F";
                enterRemarksDialog.show();
                //approveRejectCase("REJECT");
                break;

        }
    }

    private void validateMethod3(String sts) {
        for (int i = 0; i < documentList.size(); i++) {
            if (documentList.get(i).getFileURL().equals("")) {
                if (documentList.get(i).isOptionalStatus() == true) {

                } else {
                    Utils.showToast(ViewIssue.this, "Please upload " + documentList.get(i).getName());
                    break;
                }

            }
            if (i == documentList.size() - 1) {
                approRejecType = sts;
                enterRemarksDialog.show();
            }

        }
    }


    private void approveRejectCase(final String remark) {
        RequestQueue queue = VolleySingleton.getInstance(ViewIssue.this).getRequestQueue();
        String url = "";
        Preferences.getInstance().loadPreferences(ViewIssue.this);

        if (Preferences.getInstance().level.equals("7")) {
            if (approRejecType.equals("F_REJECT"))
                url = AppConstants.BASE_URL + "approveRejectIncident";
            else
                url = AppConstants.BASE_URL + "markIncidentCompletedCancelled";
        } else
            url = AppConstants.BASE_URL + "approveRejectIncident";
        Log.e("URL", url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        if (approRejecType.equals("F"))
                            Utils.showToast(ViewIssue.this, "Rejection successful!");
                        else
                            Utils.showToast(ViewIssue.this, "Approval successful!");
                        finish();
                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(ViewIssue.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewIssue.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(ViewIssue.this);
                if (Preferences.getInstance().level.equals("7")) {
                    if (approRejecType.equals("T")) {
                        params.put("status", "APPROVED");
                    } else if (approRejecType.equals("F")) {
                        params.put("status", "CANCELLED");
                    } else if (approRejecType.equals("F_REJECT")) {
                        params.put("status", "F");
                    }
                } else {
                    params.put("status", approRejecType);
                }
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

        if (Utils.isNetworkAvailable(ViewIssue.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ViewIssue.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void scrollToView(final NestedScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }


    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }


    //File Upload Code
    private void galleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_PICK_REQUEST_CODE);
    }

    private void cameraIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);

    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = null;
        Bitmap scaled = null;
        File storeFilename = null;

        //imageUri = data.getData();
        //storeFilename = compressImage(imageUri.toString());
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(ViewIssue.this.getContentResolver(), imageUri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
            String partFilename = currentDateFormat();
            storeFilename = storeCameraPhotoInSDCard(scaled, partFilename);
            //uploadImage(storeFilename);
            /*File outputDirectory = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "assessment_demo.jpg"
            );*/

            if (Utils.isNetworkAvailable(ViewIssue.this)) {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);
                UCrop.of(Uri.fromFile(storeFilename), Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);
                // uploadImage(storeFilename);
            } else {
                Utils.showToast(ViewIssue.this, "No internet present!");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      /*  Bitmap thumbnail = null;
        Bitmap scaled = null;
        String storeFilename = null;
       *//* try {
            thumbnail = MediaStore.Images.Media.getBitmap(ViewIssue.this.getContentResolver(), imageUri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(scaled, partFilename);
            storeFilename = Environment.getExternalStorageDirectory() + "/photo_" + partFilename + ".jpg";*//*
        storeFilename = compressImage(imageUri.toString());
        if (Utils.isNetworkAvailable(ViewIssue.this)) {
            uploadImage(storeFilename);
        } else {
            Utils.showToast(ViewIssue.this, "No internet present!");

        }

        *//*} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    @SuppressLint("SimpleDateFormat")
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private File storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        String path =
                ViewIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + AppConstants.IMAGE_FOLDER;
        File outputFile = new File(path, "photo_" + currentDate + ".jpeg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    @SuppressWarnings("unused")
    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void onSelectFromGalleryResult(Intent data) {

        try {
            Uri uri = data.getData();
            String path = ContentUriUtils.INSTANCE.getFilePath(ViewIssue.this, uri);
            if (Utils.isNetworkAvailable(ViewIssue.this)) {
                // uploadImage(path);
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);

                UCrop.of(uri, Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);


            } else {
                Utils.showToast(ViewIssue.this, "No internet present!");

            }

        } catch (URISyntaxException e) {

        }
        /*Bitmap thumbnail = null;
        Bitmap scaled = null;
        String storeFilename = null;
        //try {
        imageUri = data.getData();
            *//*thumbnail = MediaStore.Images.Media.getBitmap(ViewIssue.this.getContentResolver(), imageUri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = decodeSampledBitmapFromUri(ViewIssue.this, imageUri, 512, nh);
            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(scaled, partFilename);
            String storeFilename = Environment.getExternalStorageDirectory() + "/photo_" + partFilename + ".jpg";*//*
        storeFilename = compressImage(imageUri.toString());

        if (Utils.isNetworkAvailable(ViewIssue.this)) {
            uploadImage(storeFilename);
        } else {
            Utils.showToast(ViewIssue.this, "No internet present!");

        }


        *//*} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri imageUri, int reqWidth, int reqHeight)
            throws FileNotFoundException {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream iStream = context.getContentResolver().openInputStream(imageUri);
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(iStream, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            iStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iStream = context.getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(iStream, null, options);
    }

    private void uploadImage(String path) {
        progressBar.setVisibility(View.VISIBLE);
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        try {
            Log.e("FILE", "FILE====" + path);
            params.put("file", new File(path));
            //params.put("file", extension);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.BASE_URL + "uploadIncidentDoc", params,
                new AsyncHttpResponseHandler(Looper.getMainLooper()) {
                    @Override
                    public void onSuccess(int k, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        if (progressBar != null && progressBar.isShown())
                            progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(ViewIssue.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(bytes);
                        documentList.get(selePos).setFileURL(uploadedFilePath);
                        documentList.get(selePos).setUploadStatus(true);
                        documentList.get(selePos).setLon("");
                        documentList.get(selePos).setLat("");
                        mAdapter.notifyDataSetChanged();
                        updateDone = true;

                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        Utils.showToast(ViewIssue.this, "Error uploading doc");
                        progressBar.setVisibility(View.GONE);
                    }


                });
    }


    private void setUPData() {
        try {
            JSONArray docFileJSONArray = new JSONArray();
            JSONArray damageJSONArray = new JSONArray();
            for (int i = 0; i < documentList.size(); i++) {
                String extension = "";
                if (documentList.get(i).getFileURL().equals("")) {

                } else {
                    try {
                        int j = documentList.get(i).getFileURL().lastIndexOf('.');
                        if (j > 0)
                            extension = documentList.get(i).getFileURL().substring(j + 1);
                    } catch (IndexOutOfBoundsException e) {
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", documentList.get(i).getName());
                    jsonObject.put("docUrl", documentList.get(i).getFileURL());
                    jsonObject.put("format", extension);
                    jsonObject.put("lat",documentList.get(i).getLat());
                    jsonObject.put("lon", documentList.get(i).getLon());

                    docFileJSONArray.put(jsonObject);
                }


            }
            docUploadJSON.put("data", docFileJSONArray);
            for (int i = 0; i < damageDetailModelList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", damageDetailModelList.get(i).getPropertyType());
                jsonObject.put("description", damageDetailModelList.get(i).getDamageDetail());
                jsonObject.put("quantity", damageDetailModelList.get(i).getQuantity());

                damageJSONArray.put(jsonObject);
            }
            damageDetailsJSON.put("data", damageJSONArray);

            updateIncident();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateIncident() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(ViewIssue.this).getRequestQueue();
        String url = AppConstants.BASE_URL + "updateIncidentDocuments";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //9419796682
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Utils.showToast(ViewIssue.this, "Incident Updated successfully!");
                        if (clickSource.equals("")) {
                            finish();
                        } else
                            approveRejectCase(remarkValue);

                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(ViewIssue.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewIssue.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(ViewIssue.this);
                params.put("incidentId", incidentId);
                params.put("docUploadJSON", docUploadJSON.toString());
                params.put("damageDetailsJSON", damageDetailsJSON.toString());
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(ViewIssue.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ViewIssue.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void prepareDocData() {
        DocListModel model = new DocListModel();
        model.setName("Incident Photo 1");
        model.setOptionalStatus(false);
        model.setUploadStatus(false);

        DocListModel model2 = new DocListModel();
        model2.setName("Incident Photo 2");
        model2.setOptionalStatus(false);
        model2.setUploadStatus(false);

        DocListModel model3 = new DocListModel();
        model3.setName("Incident Photo 3");
        model3.setOptionalStatus(false);
        model3.setUploadStatus(false);

        DocListModel modelN = new DocListModel();
        modelN.setName("Rojnamcha");
        modelN.setOptionalStatus(true);
        modelN.setUploadStatus(false);
        DocListModel model4 = new DocListModel();
        model4.setName("Report");
        model4.setOptionalStatus(false);
        model4.setUploadStatus(false);
        //  model4.setFileURL("https://i.ytimg.com/vi/4viYzy1XCZU/maxresdefault.jpg");

        DocListModel model5 = new DocListModel();
        model5.setName("Aadhar Card");
        model5.setOptionalStatus(false);
        model5.setUploadStatus(false);
        // model5.setFileURL("https://i.ytimg.com/vi/4viYzy1XCZU/maxresdefault.jpg");

        DocListModel model6 = new DocListModel();
        model6.setName("DD Report");
        model6.setOptionalStatus(false);
        model6.setUploadStatus(false);


        documentList.add(model);
        documentList.add(model2);
        documentList.add(model3);
        documentList.add(modelN);
        documentList.add(model4);
        documentList.add(model5);
        documentList.add(model6);
        for (int i = 0; i < listOfBeneficiary.size(); i++) {
            int j = i + 1;
            DocListModel docModel = new DocListModel();
            docModel.setName("Beneficiary " + j + " Bank Passbook");
            docModel.setOptionalStatus(false);
            docModel.setUploadStatus(false);
            documentList.add(docModel);
        }

        if (Preferences.getInstance().level.equals("4")) {
            for (int i = 0; i < damaeType.size(); i++) {

                if (damaeType.get(i).equals("1")) {
                    DocListModel model7 = new DocListModel();
                    model7.setName("Upload Report from PWD");
                    model7.setOptionalStatus(false);
                    model7.setUploadStatus(false);
                    model7.setAllowUpdate(true);
                    documentList.add(model7);
                    break;
                }

            }
            for (int i = 0; i < damaeType.size(); i++) {
                if (damaeType.get(i).equals("2")) {
                    DocListModel model8 = new DocListModel();
                    model8.setName("Upload Report from CAHO");
                    model8.setOptionalStatus(false);
                    model8.setAllowUpdate(true);
                    model8.setUploadStatus(false);
                    documentList.add(model8);
                    break;
                }

            }

            for (int i = 0; i < damaeType.size(); i++) {
                if (damaeType.get(i).equals("3")) {
                    DocListModel model9 = new DocListModel();
                    model9.setName("Upload Report from CAO");
                    model9.setOptionalStatus(false);
                    model9.setAllowUpdate(true);
                    model9.setUploadStatus(false);
                    documentList.add(model9);
                    break;
                }

            }

        }

/*        DocListModel model7 = new DocListModel();
        DocListModel model8 = new DocListModel();
        DocListModel model9 = new DocListModel();


        model7.setName("Extra document 1");
        model7.setOptionalStatus(true);
        model7.setUploadStatus(false);

        model8.setName("Extra document 2");
        model8.setOptionalStatus(true);
        model8.setUploadStatus(false);

        model9.setName("Extra document 3");
        model9.setOptionalStatus(true);
        model9.setUploadStatus(false);


        DocListModel model10 = new DocListModel();
        model10.setName("Extra document 4");
        model10.setOptionalStatus(true);
        model10.setUploadStatus(false);
        documentList.add(model7);
        documentList.add(model8);
        documentList.add(model9);
        documentList.add(model10);*/
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST_CODE)
                onSelectFromGalleryResult(data);
            else if (requestCode == UCrop.REQUEST_CROP)
            {
                final Uri resultUri = UCrop.getOutput(data);
                String path = null;
                try {
                    path = ContentUriUtils.INSTANCE.getFilePath(ViewIssue.this, resultUri);
                    uploadImage(path);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = ViewIssue.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    uploadImageDialog.show();
                    uploadImageDialog.setDrawableLeft();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    Utils.showToast(ViewIssue.this, "Please got to Permission Tab to allow permisison for Camera and Storage!");

                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + ViewIssue.this.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(ViewIssue.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }
                break;
        }
    }


    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Sakoon/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
}

package com.mobiquel.udhampur.ui.addissue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.adapters.BeneficiaryListAdapter;
import com.mobiquel.udhampur.adapters.DamageListAdapter;
import com.mobiquel.udhampur.adapters.DocumentListAdapter;
import com.mobiquel.udhampur.base.BaseActivity;
import com.mobiquel.udhampur.dao.DAO;
import com.mobiquel.udhampur.databinding.ActivityAddIssuePendBinding;
import com.mobiquel.udhampur.dialogs.ConfirmationDialogBackPressed;
import com.mobiquel.udhampur.dialogs.UpdateDamageDialog;
import com.mobiquel.udhampur.dialogs.UploadImageDialog;
import com.mobiquel.udhampur.dialogs.View_Beneficiary_Dialog;
import com.mobiquel.udhampur.dialogs.View_Image_Dialog;
import com.mobiquel.udhampur.interfaces.DialogListener;
import com.mobiquel.udhampur.interfaces.DialogListenerBackPressed;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.BeneficiaryModel;
import com.mobiquel.udhampur.pojo.DamageDetailModel;
import com.mobiquel.udhampur.pojo.DocListModel;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.utils.AppConstants;
import com.mobiquel.udhampur.utils.GPSTracker;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobiquel.udhampur.databinding.ActivityAddIssuePendBinding;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import droidninja.filepicker.utils.ContentUriUtils;

public class AddIssue_Pend extends AppCompatActivity {

    // View Binding
    private ActivityAddIssuePendBinding binding;

    // Add missing fields
    private BeneficiaryListAdapter beneficaryAapter;

    private String[] descriptionData = {"Details", "Damage", "Beneficiary", "Upload"};
    private int i = 0;
    private List<DocListModel> documentList = new ArrayList<>();
    private DocumentListAdapter mAdapter;

    private View_Image_Dialog mDialog;
    private UploadImageDialog uploadImageDialog;
    private String fileUploadType = "";
    private String uploadMode = "";
    private int selePos = -1;
    private ConfirmationDialogBackPressed confirmationDialogOnBackPresssed;
    private int totalAmnt = 0;
    private ArrayAdapter<String> naturalCalamityAdapter;
    private JSONObject primaryDetails = new JSONObject();
    private JSONObject damageDetails = new JSONObject();
    private JSONObject beneficiaryDetails = new JSONObject();
    private JSONObject documentDetails = new JSONObject();
    private JSONArray docFileJSONArray = new JSONArray();
    private JSONArray beneficiaryJSONArray = new JSONArray();
    private int noOfBeneficiary = 1;
    private List<BeneficiaryModel> listOfBeneficiary = new ArrayList<>();
    private static final int MY_PERMISSIONS_CAMERA = 120;
    private final int PERMISSION_REQUEST = 0;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private Uri imageUri;
    private String uploadedFilePath;
    private int REQUEST_CAMERA = 0;
    private String generatedIssueId = "";
    private int curretBeneficiaryPos = 0;
    private List<DamageDetailModel> damageDetailModelList = new ArrayList<>();

    private JSONObject villageJSON;
    private List<String> villageIds = new ArrayList();
    private JSONObject dataJSON;
    private String saveDraftType = "";
    private String[] perceArray = new String[4];
    private String selectedAmnt, selectedDamage;
    private int unitLength = 0;
    private ArrayAdapter unitAdapter = null;
    private UpdateDamageDialog mUpdateDamageDialog;
    private DamageListAdapter damageListAapter;
    private String modeOfDamage;
    private int selectedDamagePos = -1;
    private View_Beneficiary_Dialog mViewBeneDialog;
    private String incidentId = "";

    private static final int PERMISSION_REQUEST_GPS = 4;
    private GPSTracker gpsTracker;
    private String lat = "", lon = "", address = "";
    public String incDate="";
    private  File outputDirectory;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddIssuePendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize views using View Binding instead of Butterknife
        binding.tabName.setText("Add Incident");
        binding.yourStateProgressBarId.setStateDescriptionData(descriptionData);
        binding.yourStateProgressBarId.setStateDescriptionTypeface("fonts/poppins_medium.ttf");
        binding.yourStateProgressBarId.setStateNumberTypeface("fonts/poppins_medium.ttf");
        
        // Setup click listeners to replace Butterknife @OnClick
        setupClickListeners();

        // ... rest of the onCreate method remains the same
    }

    // Setup click listeners to replace Butterknife @OnClick
    private void setupClickListeners() {
        binding.menu.setOnClickListener(this::onViewClicked);
        binding.addDamage.setOnClickListener(this::onViewClicked);
        binding.submit.setOnClickListener(this::onViewClicked);
        binding.prev.setOnClickListener(this::onViewClicked);
        binding.next.setOnClickListener(this::onViewClicked);
    }

    // Remove @OnClick annotations and replace with regular click listeners
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addDamage:
                modeOfDamage = "ADD";
                DamageDetailModel model = new DamageDetailModel();
                model.setQuantity("");
                mUpdateDamageDialog.show();
                mUpdateDamageDialog.setData(model);
                break;

            case R.id.menu:
                saveDraftType = "BACK_PRESSED";
                confirmationDialogOnBackPresssed.show();
                confirmationDialogOnBackPresssed.setMessage("All information will be lost. Do you want to save this as a Draft?");

                break;

            case R.id.prev:
                if (i == 3) {
                    binding.docUploadForm.setVisibility(View.GONE);
                    binding.beneficiaryForm.setVisibility(View.VISIBLE);
                    i = 2;
                    binding.next.setVisibility(View.VISIBLE);
                    binding.submit.setVisibility(View.GONE);
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                } else if (i == 2) {
                    binding.beneficiaryForm.setVisibility(View.GONE);
                    binding.damageForm.setVisibility(View.VISIBLE);
                    binding.totalCost.setVisibility(View.VISIBLE);
                    i = 1;
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                } else if (i == 1) {
                    binding.damageForm.setVisibility(View.GONE);
                    binding.incidentDescForm.setVisibility(View.VISIBLE);
                    binding.totalCost.setVisibility(View.GONE);
                    i = 0;
                    binding.prev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrayBorder)));
                    //#D9D9D9
                    binding.prev.setEnabled(false);
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                }
                break;
            case R.id.next:
                if (i == 0) {
                    step1Checks(view);

                } else if (i == 1) {
                    step2Checks(view);

                } else if (i == 2) {
                    binding.beneficiaryForm.setVisibility(View.GONE);
                    binding.docUploadForm.setVisibility(View.VISIBLE);
                    i = 3;
                    binding.submit.setVisibility(View.VISIBLE);
                    binding.next.setVisibility(View.GONE);
                    binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);

                }
                break;
            case R.id.submit:
                saveDraftType = "SUBMIT_TYPE";
                validateData1();
                break;
        }
    }

    private void step2Checks(View view) {

        i = 2;

        setBeneficiaryData();
        binding.damageForm.setVisibility(View.GONE);
        binding.totalCost.setVisibility(View.GONE);
        binding.beneficiaryForm.setVisibility(View.VISIBLE);
        binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

    }

    private void setBeneficiaryData() {
        //if()
        int count = Integer.parseInt(binding.beneficiaryCount.getSelectedItem().toString());
        if (listOfBeneficiary.size() > 0) {
            if (count != listOfBeneficiary.size()) {

                if (count < listOfBeneficiary.size()) {
                    int dec = listOfBeneficiary.size() - count;
                    for (int j = 0; j < dec; j++) {

                        int k = j + 1;
                        listOfBeneficiary.remove(listOfBeneficiary.size() - k);
                        documentList.remove(documentList.size() - k);
                    }
                } else {
                    int inc = count - listOfBeneficiary.size();
                    int len = listOfBeneficiary.size();
                    for (int j = 1; j <= inc; j++) {

                        int k = len + j;
                        BeneficiaryModel model = new BeneficiaryModel();
                        model.setTitle("Beneficiary #" + k);
                        listOfBeneficiary.add(model);

                        DocListModel docModel = new DocListModel();
                        docModel.setName("Beneficiary " + k + " Bank Passbook");
                        docModel.setOptionalStatus(false);
                        docModel.setUploadStatus(false);
                        documentList.add(docModel);
                    }
                }
                beneficaryAapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();

            } else {

            }

        } else {
            for (int i = 0; i < count; i++) {
                int j = i + 1;
                BeneficiaryModel model = new BeneficiaryModel();
                model.setTitle("Beneficiary No " + j);

                listOfBeneficiary.add(model);
                DocListModel docModel = new DocListModel();
                docModel.setName("Beneficiary " + j + " Bank Passbook");
                docModel.setOptionalStatus(false);
                docModel.setUploadStatus(false);
                documentList.add(docModel);

            }
            beneficaryAapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void step1Checks(View view) {
        if (binding.incidentDate.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter incident date");
        } else if (binding.applicantName.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter relief claimant");
            binding.applicantName.requestFocus();
        }
        else if (!Utils.validatePhoneNumber(binding.applicantMobile.getText().toString())) {
            Utils.showSnackBar(view, "Please enter valid relief claimant mobile number");
            binding.applicantName.requestFocus();
        }

        else if (binding.parentName.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter claimant's parent name");
            binding.parentName.requestFocus();
        } else {
            incDate=binding.incidentDate.getText().toString();
            binding.incidentDescForm.setVisibility(View.GONE);
            binding.damageForm.setVisibility(View.VISIBLE);
            binding.totalCost.setVisibility(View.VISIBLE);
            i = 1;
            binding.prev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_dark)));
            binding.prev.setEnabled(true);
            binding.yourStateProgressBarId.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        }

    }

    private void scanMethod() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(AddIssue_Pend.this);
        scanIntegrator.setPrompt("Scan");
        scanIntegrator.setBeepEnabled(true);
        //The following line if you want QR code
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setRequestCode(3);
        scanIntegrator.initiateScan();
    }

    private void validateData1() {

        validateMethod2();

    }

    private void validateMethod2() {
        float shareSum = 0;

        for (int i = 0; i < listOfBeneficiary.size(); i++) {
            try {
                if (listOfBeneficiary.get(i).getShare().equals("")) {
                } else {
                    shareSum = shareSum + Float.parseFloat(listOfBeneficiary.get(i).getShare());
                }
            } catch (NumberFormatException e) {

            }

            if (listOfBeneficiary.get(i).getName().equals("")) {
                int j = i + 1;
                toastMessage("Please enter name for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getAdharNumber().equals("")) {
                int j = i + 1;
                toastMessage("Please enter aadharnumber for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getAdharNumber().length() != 12) {
                int j = i + 1;
                toastMessage("Please enter valid aadharnumber for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getAddress().equals("")) {
                int j = i + 1;
                toastMessage("Please enter address for Beneficiary " + j);
                break;
            }
            else if (listOfBeneficiary.get(i).getContactNumber().equals("")) {
                int j = i + 1;
                toastMessage("Please enter contact number for Beneficiary " + j);
                break;
            }
            else if (listOfBeneficiary.get(i).getPinCode().equals("")) {
                int j = i + 1;
                toastMessage("Please enter Pin Code for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getShare().equals("")) {
                int j = i + 1;
                toastMessage("Please enter share amount for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getAccountHolderName().equals("")) {
                int j = i + 1;
                toastMessage("Please enter account holder name for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getAccountNumber().equals("")) {
                int j = i + 1;
                toastMessage("Please enter Account number for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getBankName().equals("")) {
                int j = i + 1;
                toastMessage("Please enter bank name for Beneficiary " + j);
                break;
            } else if (listOfBeneficiary.get(i).getIfscCode().equals("")) {
                int j = i + 1;
                toastMessage("Please enter IFSC Code for Beneficiary " + j);
                break;
            } else {
                if (i == listOfBeneficiary.size() - 1) {
                    if (shareSum == 100) {
                        validateMethod3();
                    } else {
                        Utils.showToast(AddIssue_Pend.this, "Percentage Share is not summing up to 100 %. Please enter correct share amount.");
                    }

                }
            }
        }
    }

    private void validateMethod3() {
        for (int i = 0; i < documentList.size(); i++) {
            if (documentList.get(i).getFileURL().equals("")) {
                if (documentList.get(i).isOptionalStatus() == true) {

                } else {
                    Utils.showToast(AddIssue_Pend.this, "Please upload " + documentList.get(i).getName());
                    break;
                }

            } else {
                if (i == documentList.size() - 1) {
                    if (Utils.isNetworkAvailable(AddIssue_Pend.this)) {
                        checkAllFileUpload();
                    } else {
                        generateData();
                    }
                }
            }

        }
    }

    private void toastMessage(String msg) {
        Utils.showToast(AddIssue_Pend.this, msg);
    }

    private void checkAllFileUpload() {

        for (int i = 0; i < documentList.size(); i++) {

            if (documentList.get(i).getFileURL().equals("")) {
                if (documentList.get(i).isOptionalStatus() == true) {

                } else {
                    Utils.showToast(AddIssue_Pend.this, "Please upload " + documentList.get(i).getName());
                    break;
                }

            } else {
                if (documentList.get(i).isUploadStatus() == false) {
                    uploadImageCheckFileUpload(documentList.get(i).getFileURL());
                    break;
                } else {
                    if (i == documentList.size() - 1) {
                        if (damageDetailModelList.size() > 0)
                            submitForm();
                        else
                            Utils.showToast(AddIssue_Pend.this, "Please enter any one damage details! It's mandatory.");

                    }
                }
            }

        }
    }

    private void submitForm() {
        if (Utils.isNetworkAvailable(AddIssue_Pend.this)) {

            try {
                primaryDetails.put("incidentDate", binding.incidentDate.getText().toString());
                primaryDetails.put("applicantName", binding.applicantName.getText().toString());
                primaryDetails.put("parentName", binding.parentName.getText().toString());
                primaryDetails.put("calamityType", binding.naturalCalamity.getSelectedItem().toString());
                primaryDetails.put("beneficiaryCount", binding.beneficiaryCount.getSelectedItem().toString());
                primaryDetails.put("mobile", binding.applicantMobile.getText().toString());
                primaryDetails.put("firstAid", binding.firstAid.getText().toString());
                primaryDetails.put("villageId", villageIds.get(binding.villageName.getSelectedItemPosition()));

                JSONArray damageArray = new JSONArray();

                for (int i = 0; i < damageDetailModelList.size(); i++) {
                    JSONObject damaJSON = new JSONObject();
                    damaJSON.put("type", damageDetailModelList.get(i).getPropertyType());
                    damaJSON.put("categoryName", damageDetailModelList.get(i).getCategory());
                    damaJSON.put("description", damageDetailModelList.get(i).getDamageDetail());
                    damaJSON.put("quantity", damageDetailModelList.get(i).getQuantity());
                    damaJSON.put("amount", damageDetailModelList.get(i).getTotalAmnt());
                    damageArray.put(damaJSON);
                }

                damageDetails.put("data", damageArray);
                JSONArray benArray = new JSONArray();
                for (int i = 0; i < listOfBeneficiary.size(); i++) {
                    JSONObject benArrayJSONObject = new JSONObject();
                    benArrayJSONObject.put("name", listOfBeneficiary.get(i).getName());
                    benArrayJSONObject.put("gender", listOfBeneficiary.get(i).getGender());
                    benArrayJSONObject.put("aadharNumber", listOfBeneficiary.get(i).getAdharNumber());
                    benArrayJSONObject.put("address", listOfBeneficiary.get(i).getAddress());
                    benArrayJSONObject.put("contactNumber", listOfBeneficiary.get(i).getContactNumber());
                    benArrayJSONObject.put("pincode", listOfBeneficiary.get(i).getPinCode());
                    benArrayJSONObject.put("percentageShare", listOfBeneficiary.get(i).getShare());
                    benArrayJSONObject.put("accountNo", listOfBeneficiary.get(i).getAccountNumber());

                    benArrayJSONObject.put("accountHolder", listOfBeneficiary.get(i).getAccountHolderName());
                    benArrayJSONObject.put("relation", listOfBeneficiary.get(i).getRelation());
                    benArrayJSONObject.put("bankName", listOfBeneficiary.get(i).getBankName());
                    benArrayJSONObject.put("branchName", listOfBeneficiary.get(i).getBranchName());
                    benArrayJSONObject.put("ifscCode", listOfBeneficiary.get(i).getIfscCode());
                    benArray.put(benArrayJSONObject);
                }

                beneficiaryDetails.put("data", benArray);
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
                        if(gpsTracker.getIsGPSEnable()){
                            lat=String.valueOf(gpsTracker.getLatitude());
                            lon=String.valueOf(gpsTracker.getLongitude());
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", documentList.get(i).getName());
                        jsonObject.put("docUrl", documentList.get(i).getFileURL());
                        jsonObject.put("format", extension);
                        jsonObject.put("lat", lat);
                        jsonObject.put("lon", lon);
                        docFileJSONArray.put(jsonObject);
                    }

                }
                documentDetails.put("data", docFileJSONArray);
                submitIssueAPI();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            confirmationDialogOnBackPresssed.show();
            confirmationDialogOnBackPresssed.setMessage("No internet present. Do you want to save this as a Draft and update it later when in presence of internet?");

        }

    }

    private void generateData() {
        try {

            primaryDetails.put("incidentDate", binding.incidentDate.getText().toString());
            primaryDetails.put("applicantName", binding.applicantName.getText().toString());
            primaryDetails.put("parentName", binding.parentName.getText().toString());
            primaryDetails.put("calamityType", binding.naturalCalamity.getSelectedItem().toString());
            primaryDetails.put("beneficiaryCount", binding.beneficiaryCount.getSelectedItem().toString());
            primaryDetails.put("mobile", binding.applicantMobile.getText().toString());
            primaryDetails.put("firstAid", binding.firstAid.getText().toString());
            primaryDetails.put("villageId", villageIds.get(binding.villageName.getSelectedItemPosition()));

            JSONArray damageArray = new JSONArray();

            for (int i = 0; i < damageDetailModelList.size(); i++) {
                JSONObject damaJSON = new JSONObject();
                damaJSON.put("type", damageDetailModelList.get(i).getPropertyType());
                damaJSON.put("categoryName", damageDetailModelList.get(i).getCategory());
                damaJSON.put("description", damageDetailModelList.get(i).getDamageDetail());
                damaJSON.put("quantity", damageDetailModelList.get(i).getQuantity());
                damaJSON.put("amount", damageDetailModelList.get(i).getTotalAmnt());
                damageArray.put(damaJSON);
            }

            damageDetails.put("data", damageArray);
            JSONArray benArray = new JSONArray();
            for (int i = 0; i < listOfBeneficiary.size(); i++) {
                JSONObject benArrayJSONObject = new JSONObject();
                benArrayJSONObject.put("name", listOfBeneficiary.get(i).getName());
                benArrayJSONObject.put("gender", listOfBeneficiary.get(i).getGender());
                benArrayJSONObject.put("aadharNumber", listOfBeneficiary.get(i).getAdharNumber());
                benArrayJSONObject.put("address", listOfBeneficiary.get(i).getAddress());
                benArrayJSONObject.put("contactNumber", listOfBeneficiary.get(i).getContactNumber());
                benArrayJSONObject.put("pincode", listOfBeneficiary.get(i).getPinCode());
                benArrayJSONObject.put("percentageShare", listOfBeneficiary.get(i).getShare());
                benArrayJSONObject.put("accountNo", listOfBeneficiary.get(i).getAccountNumber());

                benArrayJSONObject.put("accountHolder", listOfBeneficiary.get(i).getAccountHolderName());
                benArrayJSONObject.put("relation", listOfBeneficiary.get(i).getRelation());
                benArrayJSONObject.put("bankName", listOfBeneficiary.get(i).getBankName());
                benArrayJSONObject.put("branchName", listOfBeneficiary.get(i).getBranchName());
                benArrayJSONObject.put("ifscCode", listOfBeneficiary.get(i).getIfscCode());
                benArray.put(benArrayJSONObject);
            }

            beneficiaryDetails.put("data", benArray);
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
                    jsonObject.put("status", documentList.get(i).isUploadStatus());
                    docFileJSONArray.put(jsonObject);
                }

            }
            documentDetails.put("data", docFileJSONArray);

            saveAsDraft();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    path = ContentUriUtils.INSTANCE.getFilePath(AddIssue_Pend.this, resultUri);
                    uploadImage(path);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == 3) {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(resultCode, data);
                if (scanningResult != null) {
                    String scanContent = "", scanFormat = "";
                    if (scanningResult.getContents() != null) {
                        XmlPullParserFactory pullParserFactory = null;
                        XmlPullParser parser;
                        try {
                            scanContent = scanningResult.getContents().toString();
                            scanFormat = scanningResult.getFormatName().toString();
                            pullParserFactory = XmlPullParserFactory.newInstance();
                            parser = pullParserFactory.newPullParser();
                            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                            parser.setInput(new StringReader(scanContent));

                            int eventType = parser.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_DOCUMENT) {
                                    Log.d("Rajdeol", "Start document");
                                } else if (eventType == XmlPullParser.START_TAG && AppConstants.AADHAAR_DATA_TAG.equals(parser.getName())) {
                                    // extract data from tag
                                    //uid
                                    String uid = parser.getAttributeValue(null, AppConstants.AADHAR_UID_ATTR);
                                    //name
                                    String name = parser.getAttributeValue(null, AppConstants.AADHAR_NAME_ATTR);
                                    //gender
                                    String genderValue = parser.getAttributeValue(null, AppConstants.AADHAR_GENDER_ATTR);
                                    // year of birth
                                    String yearOfBirth = parser.getAttributeValue(null, AppConstants.AADHAR_YOB_ATTR);
                                    // care of
                                    String careOf = parser.getAttributeValue(null, AppConstants.AADHAR_CO_ATTR);
                                    // village Tehsil
                                    String villageTehsil = parser.getAttributeValue(null, AppConstants.AADHAR_VTC_ATTR);
                                    // Post Office
                                    String house = parser.getAttributeValue(null, AppConstants.AADHAR_HOUSE);
                                    // district
                                    String district = parser.getAttributeValue(null, AppConstants.AADHAR_DIST_ATTR);
                                    // state
                                    String state = parser.getAttributeValue(null, AppConstants.AADHAR_STATE_ATTR);
                                    // Post Code
                                    String postCode = parser.getAttributeValue(null, AppConstants.AADHAR_PC_ATTR);
                                   /* personName.setText(name);
                                    if (genderValue.equalsIgnoreCase("M")) {
                                        maleRg.setChecked(true);
                                        femaleRg.setChecked(false);
                                    } else {
                                        maleRg.setChecked(false);
                                        femaleRg.setChecked(true);
                                    }
                                    adharNumber.setText(uid);
                                    adharAddress.setText(careOf + "\n" + house + "\n" + villageTehsil + "\n" + district + "\n" + state);
                                    pinCode.setText(postCode);*/
                                    listOfBeneficiary.get(curretBeneficiaryPos).setName(name);
                                    if (genderValue.equalsIgnoreCase("M")) {
                                        listOfBeneficiary.get(curretBeneficiaryPos).setGender("Male");
                                    } else {
                                        listOfBeneficiary.get(curretBeneficiaryPos).setGender("Female");
                                    }
                                    listOfBeneficiary.get(curretBeneficiaryPos).setAdharNumber(uid);
                                    listOfBeneficiary.get(curretBeneficiaryPos).setAddress(careOf + "\n" + house + "\n" + villageTehsil + "\n" + district + "\n" + state);
                                    listOfBeneficiary.get(curretBeneficiaryPos).setPinCode(postCode);
                                    beneficaryAapter.notifyDataSetChanged();
                                    Log.e("ADHAR_CARD_DATA", uid + "\n" + name + "\n" + genderValue + "\n" + yearOfBirth + "\n" + villageTehsil + "\n");
                                } else if (eventType == XmlPullParser.END_TAG) {

                                } else if (eventType == XmlPullParser.TEXT) {

                                }
                                // update eventType
                                eventType = parser.next();
                            }
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // get the parser
                    }

                    Log.e("SCAN", scanContent + "   type:" + scanFormat);

                } else {
                    Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
                }
            }

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
            case PERMISSION_REQUEST_GPS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {

                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(AddIssue_Pend.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                }
                return;
            }
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = AddIssue_Pend.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    uploadImageDialog.show();
                    uploadImageDialog.setDrawableLeft();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    Utils.showToast(AddIssue_Pend.this, "Please got to Permission Tab to allow permisison for Camera and Storage!");

                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + AddIssue_Pend.this.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(AddIssue_Pend.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }
                break;
        }
    }

    public void datepick(final EditText editText) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String d = "", m = "";
                        if (String.valueOf(dayOfMonth).length() > 1) {
                            d = String.valueOf(dayOfMonth);
                        } else {
                            d = "0" + String.valueOf(dayOfMonth);
                        }
                        if (String.valueOf(monthOfYear + 1).length() > 1) {
                            m = String.valueOf(monthOfYear + 1);
                        } else {
                            m = "0" + String.valueOf(monthOfYear + 1);
                        }
                        editText.setText(d + "-" + m + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //File Upload Code
    private void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                return;
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }
        launchGalleryPicker();
    }
    private void launchGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        launchCameraPicker();
    }
    private void launchCameraPicker() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    private void onSelectFromGalleryResult(Uri uri) {

        try {
            String path = ContentUriUtils.INSTANCE.getFilePath(AddIssue_Pend.this, uri);
            if (Utils.isNetworkAvailable(AddIssue_Pend.this)) {
                // uploadImage(path);
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);

                UCrop.of(uri, Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);

            } else {
                Utils.showToast(AddIssue_Pend.this, "No internet present! File is saved offline");
                documentList.get(selePos).setFileURL(path);
                documentList.get(selePos).setUploadStatus(false);
                mAdapter.notifyDataSetChanged();
            }

        } catch (URISyntaxException e) {

        }

    }

    private void onCaptureImageResult(Uri uri) {
        Bitmap thumbnail = null;
        Bitmap scaled = null;
        File storeFilename = null;

        //imageUri = data.getData();
        //storeFilename = compressImage(imageUri.toString());
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(AddIssue_Pend.this.getContentResolver(), uri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
            String partFilename = currentDateFormat();
            storeFilename = storeCameraPhotoInSDCard(scaled, partFilename);
            //uploadImage(storeFilename);
            /*File outputDirectory = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "assessment_demo.jpg"
            );*/

            if (Utils.isNetworkAvailable(AddIssue_Pend.this)) {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);
                UCrop.of(Uri.fromFile(storeFilename), Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);
                // uploadImage(storeFilename);
            } else {
                Utils.showToast(AddIssue_Pend.this, "No internet present! File is saved offline");
                documentList.get(selePos).setFileURL(storeFilename.toString());
                documentList.get(selePos).setUploadStatus(false);
                mAdapter.notifyDataSetChanged();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private File storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        String path =
                AddIssue_Pend.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
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

    private void uploadImage(String path) {
        binding.progressBar.setVisibility(View.VISIBLE);
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
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        if (binding.progressBar != null && binding.progressBar.isShown())
                            binding.progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(AddIssue_Pend.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(bytes);
                        documentList.get(selePos).setFileURL(uploadedFilePath);
                        documentList.get(selePos).setUploadStatus(true);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        Utils.showToast(AddIssue_Pend.this, "Error uploading doc");
                        binding.progressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void uploadImageCheckFileUpload(String path) {
        binding.progressBar.setVisibility(View.VISIBLE);
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
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        if (binding.progressBar != null && binding.progressBar.isShown())
                            binding.progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(AddIssue_Pend.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(bytes);
                        documentList.get(selePos).setFileURL(uploadedFilePath);
                        documentList.get(selePos).setUploadStatus(true);
                        mAdapter.notifyDataSetChanged();
                        checkAllFileUpload();

                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        Utils.showToast(AddIssue_Pend.this, "Error uploading doc");
                        binding.progressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void submitIssueAPI() {
        binding.progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(AddIssue_Pend.this).getRequestQueue();
        String url = AppConstants.BASE_URL + "updateIncident";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        Utils.showToast(AddIssue_Pend.this, "Incident updated successfully!");
                        finish();
                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(AddIssue_Pend.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (binding.progressBar.isShown()) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddIssue_Pend.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                if (binding.progressBar.isShown()) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(AddIssue_Pend.this);
/*              params.put("officialId", Preferences.getInstance().officialId);
                params.put("primaryDetailsJSON", primaryDetails.toString());
                params.put("damageDetailsJSON", damageDetails.toString());
                params.put("beneficiaryDetailsJSON", beneficiaryDetails.toString());
                params.put("docUploadJSON", documentDetails.toString());
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);*/

                params.put("incidentId", incidentId);
                params.put("docUploadJSON", documentDetails.toString());
                params.put("damageDetailsJSON", damageDetails.toString());
                params.put("primaryDetailsJSON", primaryDetails.toString());
                params.put("beneficiaryDetailsJSON", beneficiaryDetails.toString());
                params.put("tokenUserId", Preferences.getInstance().officialId);
                params.put("tokenUserType", "official");
                params.put("token", Preferences.getInstance().token);

                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Utils.isNetworkAvailable(AddIssue_Pend.this)) {
            queue.add(requestObject);
        } else {
            if (binding.progressBar != null && binding.progressBar.isShown()) {
                binding.progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(AddIssue_Pend.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
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
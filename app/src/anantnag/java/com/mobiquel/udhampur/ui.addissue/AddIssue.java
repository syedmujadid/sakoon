
package com.mobiquel.udhampur.ui.addissue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.dao.DAO;
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
import com.mobiquel.udhampur.utils.GPSTracker;
import com.mobiquel.udhampur.utils.Preferences;
import com.mobiquel.udhampur.utils.Utils;
import com.mobiquel.udhampur.utils.VolleySingleton;
import com.mobiquel.udhampur.utils.AppConstants;
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

import droidninja.filepicker.utils.ContentUriUtils;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class AddIssue extends AppCompatActivity {

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
    private IssueListModel getIssuesListModel;
    private String saveDraftType = "";
    private String[] perceArray = new String[4];
    private String selectedAmnt, selectedDamage;
    private int unitLength = 0;
    private ArrayAdapter unitAdapter = null;
    private UpdateDamageDialog mUpdateDamageDialog;
    private DamageListAdapter damageListAapter;
    private String modeOfDamage;
    private int selectedDamagePos = -1;
    private View_Beneficiary_Dialog mBenefiDialog;

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
        setContentView(R.layout.activity_add_issue);
        tabName.setText("Add Incident");
        stateProgBar.setStateDescriptionData(descriptionData);
        stateProgBar.setStateDescriptionTypeface("fonts/poppins_medium.ttf");
        stateProgBar.setStateNumberTypeface("fonts/poppins_medium.ttf");
        mAdapter = new DocumentListAdapter(documentList, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                showImage(documentList.get(position).getFileURL(), documentList.get(position).getName());
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                showUploadDialog(documentList.get(position).getName(), "EDIT", position);
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                showUploadDialog(documentList.get(position).getName(), "UPLOAD", position);
            }
        });
        gpsTracker = new GPSTracker(AddIssue.this);
        docRecylerView.setLayoutManager(new LinearLayoutManager(AddIssue.this));
        docRecylerView.setAdapter(mAdapter);
        mDialog = new View_Image_Dialog(AddIssue.this);
        beneficaryAapter = new BeneficiaryListAdapter(listOfBeneficiary, "ADD", new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                curretBeneficiaryPos = position;
                if (ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    scanMethod();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                }
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                curretBeneficiaryPos = position;
                int i = position + 1;
                mBenefiDialog.show();
                mBenefiDialog.setData(listOfBeneficiary.get(position), "ADD", i);
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                curretBeneficiaryPos = position;
                listOfBeneficiary.remove(position);
                int k = i + 1;
                for (int i = 0; i < documentList.size(); i++) {
                    if (documentList.get(i).getName().contains("Beneficiary " + k)) {
                        documentList.remove(i);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                beneficaryAapter.notifyDataSetChanged();

            }
        });
        damageList.setVisibility(View.VISIBLE);
        perceArray[0] = "100";
        perceArray[1] = "75";
        perceArray[2] = "50";
        perceArray[3] = "25";

        damageListAapter = new DamageListAdapter(damageDetailModelList, "ADD_ISSUE", new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                selectedDamagePos = position;
                modeOfDamage = "UPDATE";
                DamageDetailModel model = damageDetailModelList.get(position);
                mUpdateDamageDialog.show();
                mUpdateDamageDialog.setData(model);
            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                damageDetailModelList.remove(position);
                damageListAapter.notifyDataSetChanged();
                updateToatlCost();
                if (damageDetailModelList.size() > 0)
                    totalCost.setVisibility(View.VISIBLE);
                else
                    totalCost.setVisibility(View.GONE);

            }
        });

        Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_date);
        incidentDate.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);

        beneficiaryList.setLayoutManager(new LinearLayoutManager(AddIssue.this));
        beneficiaryList.setAdapter(beneficaryAapter);

        damageList.setLayoutManager(new LinearLayoutManager(AddIssue.this));
        damageList.setAdapter(damageListAapter);

        confirmationDialogOnBackPresssed = new ConfirmationDialogBackPressed(AddIssue.this, new DialogListenerBackPressed() {
            @Override
            public void onPositiveButtonClick() {
                generateData();
            }

            @Override
            public void onNegativeButtonClick() {

                finish();
                overridePendingTransition(R.anim.right_out, R.anim.left_in);
            }

            @Override
            public void onNeutralButtonClick() {
                confirmationDialogOnBackPresssed.cancel();
            }
        });
        uploadImageDialog = new UploadImageDialog(AddIssue.this, new DialogListener() {
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

        mBenefiDialog = new View_Beneficiary_Dialog(AddIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                BeneficiaryModel model = mBenefiDialog.getData();
                listOfBeneficiary.set(curretBeneficiaryPos, model);
                beneficaryAapter.notifyDataSetChanged();
                mBenefiDialog.cancel();
            }

            @Override
            public void onNegativeButtonClick() {

            }
        });
        prepareDocData();
        incidentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datepick(incidentDate);
            }
        });
        if (!getIntent().getExtras().getString("SOURCE").equals("UPDATE")) {
            //  addDamageDynamicLayout();
            // updateToatlCost();

        }

        try {
            Preferences.getInstance().loadPreferences(AddIssue.this);
            if (Preferences.getInstance().villageName.equals("")) {

            } else {
                villageJSON = new JSONObject(Preferences.getInstance().villageName);
                JSONArray villArray = villageJSON.getJSONArray("data");
                String cityArray[] = new String[villArray.length()];

                for (int i = 0; i < villArray.length(); i++) {
                    cityArray[i] = villArray.getJSONObject(i).getString("name");
                    villageIds.add(villArray.getJSONObject(i).getString("villageId"));
                }
                ArrayAdapter cityAdapter = new ArrayAdapter<String>(AddIssue.this,
                        android.R.layout.simple_list_item_1, cityArray);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                villageName.setAdapter(cityAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getIntent().getExtras().getString("SOURCE").equals("UPDATE")) {
            getIssuesListModel = (IssueListModel) getIntent().getSerializableExtra("DATA_MODEL");
            try {
                JSONObject issueDetail = new JSONObject(getIssuesListModel.getIssueDetails());
                JSONObject damageDetail = new JSONObject(getIssuesListModel.getDamageDetails());
                JSONObject beneficDetail = new JSONObject(getIssuesListModel.getBenefeciaryDetails());
                JSONObject docDetail = new JSONObject(getIssuesListModel.getDocDetails());
                incDate=issueDetail.getString("incidentDate");

                incidentDate.setText(issueDetail.getString("incidentDate"));
                applicantName.setText(issueDetail.getString("applicantName"));
                parentName.setText(issueDetail.getString("parentName"));
                String[] naturalCalamityArray = getResources().getStringArray(R.array.calamityType);
                String[] countArray = getResources().getStringArray(R.array.noArray);
                for (int i = 0; i < naturalCalamityArray.length; i++) {
                    if (naturalCalamityArray[i].equals(issueDetail.getString("calamityType"))) {
                        naturalCalamity.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < countArray.length; i++) {
                    if (countArray[i].equals(issueDetail.getString("beneficiaryCount"))) {
                        beneficiaryCount.setSelection(i);
                        break;
                    }
                }

                applicantMobile.setText(issueDetail.getString("mobile"));
                firstAid.setText(issueDetail.getString("firstAid"));
                villageName.setSelection(0);

                JSONArray damageArray = damageDetail.getJSONArray("data");
                JSONArray docArray = docDetail.getJSONArray("data");
                JSONArray beneficiaryArray = beneficDetail.getJSONArray("data");

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
                    int j = i + 1;
                    DocListModel docModel = new DocListModel();
                    docModel.setName("Beneficiary " + j + " Bank Passbook");
                    docModel.setOptionalStatus(false);
                    docModel.setUploadStatus(false);
                    documentList.add(docModel);
                }
                mAdapter.notifyDataSetChanged();

                beneficaryAapter.notifyDataSetChanged();
                if (damageArray.length() > 1)
                    for (int i = 0; i < damageArray.length(); i++) {

                        JSONObject jsonObject = damageArray.getJSONObject(i);
                        DamageDetailModel model = new DamageDetailModel();
                        model.setPropertyType(jsonObject.getString("type"));
                        model.setDamageDetail(jsonObject.getString("description"));
                        model.setQuantity(jsonObject.getString("quantity"));
                        model.setCategory(jsonObject.getString("categoryName"));
                        model.setTotalAmnt(jsonObject.getString("amount"));
                        damageDetailModelList.add(model);
                        updateToatlCost();
                    }
                damageListAapter.notifyDataSetChanged();
                for (int i = 0; i < docArray.length(); i++) {
                    JSONObject jsonObject = docArray.getJSONObject(i);

                    for (int j = 0; j < documentList.size(); j++) {
                        if (jsonObject.getString("type").equals(documentList.get(j).getName())) {
                            documentList.get(j).setFileURL(jsonObject.getString("docUrl"));
                            documentList.get(j).setUploadStatus(true);
                            break;
                        }
                    }

                }
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }

        mUpdateDamageDialog = new UpdateDamageDialog(AddIssue.this, new DialogListener() {
            @Override
            public void onPositiveButtonClick() {
                DamageDetailModel model = mUpdateDamageDialog.getData();
                if (modeOfDamage.equals("ADD")) {

                    damageDetailModelList.add(model);
                } else {

                    damageDetailModelList.get(selectedDamagePos).setQuantity(model.getQuantity());
                    damageDetailModelList.get(selectedDamagePos).setDamageDetail(model.getDamageDetail());
                    damageDetailModelList.get(selectedDamagePos).setPropertyType(model.getPropertyType());
                    damageDetailModelList.get(selectedDamagePos).setCategory(model.getCategory());
                    damageDetailModelList.get(selectedDamagePos).setTotalAmnt(model.getTotalAmnt());
                }

                damageListAapter.notifyDataSetChanged();
                updateToatlCost();
                if (damageDetailModelList.size() > 0)
                    totalCost.setVisibility(View.VISIBLE);
                else
                    totalCost.setVisibility(View.GONE);
                mUpdateDamageDialog.setDataEmpty();
                mUpdateDamageDialog.cancel();

            }

            @Override
            public void onNegativeButtonClick() {

                mUpdateDamageDialog.cancel();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AddIssue.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_GPS);
            } else {

            }

        } else {

        }

        check_folder();

        // 3. Register launchers
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    // Handle the image URI (e.g., display or upload)
                    onSelectFromGalleryResult(selectedImageUri);
                }
            }
        );
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle the image URI (e.g., display or upload)
                    onCaptureImageResult(imageUri);
                }
            }
        );
        requestGalleryPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchGalleryPicker();
                } else {
                    Utils.showToast(this, "Permission denied for gallery access");
                }
            }
        );
        requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCameraPicker();
                } else {
                    Utils.showToast(this, "Permission denied for camera access");
                }
            }
        );
    }

    private void check_folder() {
        //  Log.e("PATH", "=== " + path);
        String path =
                AddIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + AppConstants.IMAGE_FOLDER;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        outputDirectory= new File(
                AddIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                AppConstants.IMAGE_FOLDER+File.separator+"sakoon_demo.jpg"
        );
    }

    private void updateToatlCost() {
        totalAmnt = 0;
        for (int i = 0; i < damageDetailModelList.size(); i++) {
            totalAmnt = totalAmnt + Integer.parseInt(damageDetailModelList.get(i).getTotalAmnt().trim());

        }
        totalCost.setText("Total damage Cost: \u20B9" + totalAmnt);
    }

    private void saveAsDraft() {
        IssueListModel model = new IssueListModel();
        Preferences.getInstance().loadPreferences(AddIssue.this);
        int id = Integer.parseInt(Preferences.getInstance().randomNumber) + 1;
        generatedIssueId = String.valueOf(id);
        Preferences.getInstance().randomNumber = generatedIssueId;
        Preferences.getInstance().savePreferences(AddIssue.this);
        if (getIntent().getExtras().getString("SOURCE").equals("UPDATE"))
            model.setCaseId(getIntent().getExtras().getString("CASE_ID"));
        else
            model.setCaseId(generatedIssueId);
        model.setOnlineStatus("F");
        model.setIssueDetails(primaryDetails.toString());
        model.setDamageDetails(damageDetails.toString());
        model.setBenefeciaryDetails(beneficiaryDetails.toString());
        model.setDocDetails(documentDetails.toString());
        model.setName(applicantName.getText().toString());
        model.setServerStatus("F");
        DAO dao = new DAO(AddIssue.this);
        if (getIntent().getExtras().getString("SOURCE").equals("UPDATE"))
            dao.updateIssue(model);
        else
            dao.addIssue(model);
        Utils.showToast(AddIssue.this, "Incident saved as Draft successfully!");
        finish();
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }

    public void showUploadDialog(String fileType, String uploadType, int selecPos) {
        this.fileUploadType = fileType;
        this.uploadMode = uploadType;
        this.selePos = selecPos;
        if ((ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(AddIssue.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )) {
            Activity activity = (Activity) AddIssue.this;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = AddIssue.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            uploadImageDialog.show();
            uploadImageDialog.setDrawableLeft();

        }

    }

    public void showImage(String url, String title) {
        mDialog.show();
        mDialog.setData(url, title);

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

        DocListModel model4 = new DocListModel();
        model4.setName("Report");
        model4.setOptionalStatus(false);
        model4.setUploadStatus(false);

        DocListModel modelN = new DocListModel();
        modelN.setName("Rojnamcha");
        modelN.setOptionalStatus(true);
        modelN.setUploadStatus(false);
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
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        saveDraftType = "BACK_PRESSED";
        confirmationDialogOnBackPresssed.show();
        confirmationDialogOnBackPresssed.setMessage("All information will be lost. Do you want to save this as a Draft?");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.right_out, R.anim.left_in);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    R.id.menu, R.id.addDamage, R.id.submit, R.id.prev, R.id.next
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
                    docUploadForm.setVisibility(View.GONE);
                    beneficiaryForm.setVisibility(View.VISIBLE);
                    i = 2;
                    next.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                    stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                } else if (i == 2) {
                    beneficiaryForm.setVisibility(View.GONE);
                    damageForm.setVisibility(View.VISIBLE);
                    totalCost.setVisibility(View.VISIBLE);
                    i = 1;
                    stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                } else if (i == 1) {
                    damageForm.setVisibility(View.GONE);
                    incidentDescForm.setVisibility(View.VISIBLE);
                    totalCost.setVisibility(View.GONE);
                    i = 0;
                    prev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrayBorder)));
                    //#D9D9D9
                    prev.setEnabled(false);
                    stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                }
                break;
            case R.id.next:
                if (i == 0) {
                    step1Checks(view);

                } else if (i == 1) {
                    step2Checks(view);

                } else if (i == 2) {
                    beneficiaryForm.setVisibility(View.GONE);
                    docUploadForm.setVisibility(View.VISIBLE);
                    i = 3;
                    submit.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                    stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);

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
        damageForm.setVisibility(View.GONE);
        totalCost.setVisibility(View.GONE);
        beneficiaryForm.setVisibility(View.VISIBLE);
        stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

    }

    private void setBeneficiaryData() {
        //if()
        int count = Integer.parseInt(beneficiaryCount.getSelectedItem().toString());
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

    private void step1Checks(View view) {
        if (incidentDate.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter incident date");
        } else if (applicantName.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter relief claimant");
            applicantName.requestFocus();
        } else if (parentName.getText().toString().equals("")) {
            Utils.showSnackBar(view, "Please enter claimant's parent name");
            parentName.requestFocus();
        } else {
            incDate=incidentDate.getText().toString();

            incidentDescForm.setVisibility(View.GONE);
            damageForm.setVisibility(View.VISIBLE);
            totalCost.setVisibility(View.VISIBLE);
            i = 1;
            prev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_dark)));
            prev.setEnabled(true);
            stateProgBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        }

    }

    private void scanMethod() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(AddIssue.this);
        scanIntegrator.setPrompt("Scan");
        scanIntegrator.setBeepEnabled(true);
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
            } else if (listOfBeneficiary.get(i).getPinCode().equals("")) {
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
                        Utils.showToast(AddIssue.this, "Percentage Share is not summing up to 100 %. Please enter correct share amount.");
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
                    Utils.showToast(AddIssue.this, "Please upload " + documentList.get(i).getName());
                    break;
                }

            } else {
                if (i == documentList.size() - 1) {
                    if (Utils.isNetworkAvailable(AddIssue.this)) {
                        checkAllFileUpload();
                    } else {
                        generateData();
                    }
                }
            }

        }
    }

    private void toastMessage(String msg) {
        Utils.showToast(AddIssue.this, msg);
    }

    private void checkAllFileUpload() {

        for (int i = 0; i < documentList.size(); i++) {

            if (documentList.get(i).getFileURL().equals("")) {
                if (documentList.get(i).isOptionalStatus() == true) {

                } else {
                    Utils.showToast(AddIssue.this, "Please upload " + documentList.get(i).getName());
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
                            Utils.showToast(AddIssue.this, "Please enter any one damage details! It's mandatory.");

                    }
                }
            }

        }
    }

    private void submitForm() {
        if (Utils.isNetworkAvailable(AddIssue.this)) {

            try {
                primaryDetails.put("incidentDate", incidentDate.getText().toString());
                primaryDetails.put("applicantName", applicantName.getText().toString());
                primaryDetails.put("parentName", parentName.getText().toString());
                primaryDetails.put("calamityType", naturalCalamity.getSelectedItem().toString());
                primaryDetails.put("beneficiaryCount", beneficiaryCount.getSelectedItem().toString());
                primaryDetails.put("mobile", applicantMobile.getText().toString());
                primaryDetails.put("firstAid", firstAid.getText().toString());
                primaryDetails.put("villageId", villageIds.get(villageName.getSelectedItemPosition()));

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
                        if (gpsTracker.getIsGPSEnable()) {
                            lat = String.valueOf(gpsTracker.getLatitude());
                            lon = String.valueOf(gpsTracker.getLongitude());
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

            primaryDetails.put("incidentDate", incidentDate.getText().toString());
            primaryDetails.put("applicantName", applicantName.getText().toString());
            primaryDetails.put("parentName", parentName.getText().toString());
            primaryDetails.put("calamityType", naturalCalamity.getSelectedItem().toString());
            primaryDetails.put("beneficiaryCount", beneficiaryCount.getSelectedItem().toString());
            primaryDetails.put("mobile", applicantMobile.getText().toString());
            primaryDetails.put("firstAid", firstAid.getText().toString());
            primaryDetails.put("villageId", villageIds.get(villageName.getSelectedItemPosition()));

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
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == UCrop.REQUEST_CROP)
            {
                final Uri resultUri = UCrop.getOutput(data);
                String path = null;
                try {
                    path = ContentUriUtils.INSTANCE.getFilePath(AddIssue.this, resultUri);
                    uploadImage(path);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }

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
                    ActivityCompat.requestPermissions(AddIssue.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                }
                return;
            }
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = AddIssue.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    uploadImageDialog.show();
                    uploadImageDialog.setDrawableLeft();
                } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                    Utils.showToast(AddIssue.this, "Please got to Permission Tab to allow permisison for Camera and Storage!");

                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + AddIssue.this.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(AddIssue.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
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

    private void onCaptureImageResult(Uri uri) {
        Bitmap thumbnail = null;
        Bitmap scaled = null;
        File storeFilename = null;

        //imageUri = data.getData();
        //storeFilename = compressImage(imageUri.toString());
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(AddIssue.this.getContentResolver(), uri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
            String partFilename = currentDateFormat();
            storeFilename = storeCameraPhotoInSDCard(scaled, partFilename);
            //uploadImage(storeFilename);
            /*File outputDirectory = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "assessment_demo.jpg"
            );*/

            if (Utils.isNetworkAvailable(AddIssue.this)) {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);
                UCrop.of(Uri.fromFile(storeFilename), Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);
                // uploadImage(storeFilename);
            } else {
                Utils.showToast(AddIssue.this, "No internet present! File is saved offline");
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
                AddIssue.this.getExternalFilesDir(Environment.DIRECTORY_DCIM)
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

    private void onSelectFromGalleryResult(Uri uri) {
        try {
            String path = ContentUriUtils.INSTANCE.getFilePath(AddIssue.this, uri);
            if (Utils.isNetworkAvailable(AddIssue.this)) {
                // uploadImage(path);
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(100);
                options.setMaxBitmapSize(10000);

                UCrop.of(uri, Uri.fromFile(outputDirectory))
                        .withMaxResultSize(1000, 1000)
                        .withOptions(options)
                        .start(this);

            } else {
                Utils.showToast(AddIssue.this, "No internet present! File is saved offline");
                documentList.get(selePos).setFileURL(path);
                documentList.get(selePos).setUploadStatus(false);
                mAdapter.notifyDataSetChanged();
            }

        } catch (URISyntaxException e) {

        }

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
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        if (progressBar != null && progressBar.isShown())
                            progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(AddIssue.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(bytes);
                        documentList.get(selePos).setFileURL(uploadedFilePath);
                        documentList.get(selePos).setUploadStatus(true);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        Utils.showToast(AddIssue.this, "Error uploading doc");
                        progressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void uploadImageCheckFileUpload(String path) {
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
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        System.out.println("abc");
                        if (progressBar != null && progressBar.isShown())
                            progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(AddIssue.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(bytes);
                        documentList.get(selePos).setFileURL(uploadedFilePath);
                        documentList.get(selePos).setUploadStatus(true);
                        mAdapter.notifyDataSetChanged();
                        checkAllFileUpload();

                    }

                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        Utils.showToast(AddIssue.this, "Error uploading doc");
                        progressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void submitIssueAPI() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(AddIssue.this).getRequestQueue();
        String url = AppConstants.BASE_URL + "reportIncident";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    Log.e("RESPO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        DAO dao = new DAO(AddIssue.this);
                        if (getIntent().getExtras().getString("SOURCE").equals("UPDATE"))
                            dao.deleteCase(getIssuesListModel.getCaseId());

                        Utils.showToast(AddIssue.this, "Incident reported successfully!");
                        finish();
                    } else if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 2) {
                        Utils.logout(AddIssue.this);

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
                Toast.makeText(AddIssue.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(AddIssue.this);
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

        if (Utils.isNetworkAvailable(AddIssue.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(AddIssue.this, AppConstants.ENABLE_INTERNET_SETTING_MESSAGE);
        }
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
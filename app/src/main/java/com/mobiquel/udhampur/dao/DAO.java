package com.mobiquel.udhampur.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.mobiquel.udhampur.dbadapter.DBAdapter;
import com.mobiquel.udhampur.pojo.IssueListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DAO {
    private SQLiteDatabase database;
    private DBAdapter dbAdapter;
    private Context context;

    public DAO(Context context) {
        this.context = context;
        dbAdapter = new DBAdapter(this.context);
    }

    public void open() throws SQLException {
        database = dbAdapter.openDataBase();
    }

    public void close() {
        dbAdapter.close();
    }

    public boolean addIssue(IssueListModel issueListModel) {
        //    caseId,onlineStatus,issueDetails,damageDetails,benefeciaryDetails,docDetails,serverStatus

        open();
        ContentValues v = new ContentValues();
        v.put("caseId", issueListModel.getCaseId());
        v.put("onlineStatus", issueListModel.getOnlineStatus());
        v.put("name", issueListModel.getName());
        v.put("primaryDetails", issueListModel.getIssueDetails());
        v.put("damageDetails", issueListModel.getDamageDetails());
        v.put("beneficiaryDetails", issueListModel.getBenefeciaryDetails());
        v.put("docDetails", issueListModel.getDocDetails());
        v.put("serverStatus", issueListModel.getServerStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        v.put("dateOfCreation", currentDate);
        v.put("createdOn", currentDateandTime);
        Log.e("VALUES IN DB", "====" + v.toString());
        long l = database.insert(DBAdapter.ISSUE_LIST_TABLE, null, v);
        close();
        if (l != -1) {
            return true;
        } else {
            return false;
        }
    }
    public boolean updateIssue(IssueListModel issueListModel) {

        open();
        String sql = "UPDATE "+ DBAdapter.ISSUE_LIST_TABLE + " set onlineStatus=?,name=?,primaryDetails=?,damageDetails=?,beneficiaryDetails=?,docDetails=? where caseId=?";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();
        statement.bindString(1,issueListModel.getOnlineStatus());
        statement.bindString(2,issueListModel.getName());
        statement.bindString(3,issueListModel.getIssueDetails());
        statement.bindString(4,issueListModel.getDamageDetails());
        statement.bindString(5,issueListModel.getBenefeciaryDetails());
        statement.bindString(6,issueListModel.getDocDetails());
        statement.bindString(7,issueListModel.getCaseId());

        statement.execute();
        statement.clearBindings();
        database.setTransactionSuccessful();
        database.endTransaction();
        close();
        return true;
    }
    public boolean deleteCase(String caseid)
    {
        open();
        String sql = "DELETE from "+ DBAdapter.ISSUE_LIST_TABLE + " where caseId = '" + caseid + "'";
        database.execSQL(sql);
        close();
        return true;
    }
    public List<IssueListModel> getAllDraftIssues() {
        open();
        List<IssueListModel> issueList = new ArrayList<>();
        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + DBAdapter.ISSUE_LIST_TABLE+" where onlineStatus = '"+"F"+"'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                IssueListModel vo = new IssueListModel();
                vo.setCaseId(cursor.getString(cursor.getColumnIndex("caseId")));
                vo.setName(cursor.getString(cursor.getColumnIndex("name")));
                vo.setOnlineStatus(cursor.getString(cursor.getColumnIndex("onlineStatus")));
                vo.setIssueDetails(cursor.getString(cursor.getColumnIndex("primaryDetails")));
                vo.setDamageDetails(cursor.getString(cursor.getColumnIndex("damageDetails")));
                vo.setBenefeciaryDetails(cursor.getString(cursor.getColumnIndex("beneficiaryDetails")));
                vo.setDocDetails(cursor.getString(cursor.getColumnIndex("docDetails")));
                vo.setServerStatus(cursor.getString(cursor.getColumnIndex("serverStatus")));
                vo.setDateOfCreation(cursor.getString(cursor.getColumnIndex("dateOfCreation")));
                vo.setCreatedOn(cursor.getString(cursor.getColumnIndex("createdOn")));
                issueList.add(vo);
                cursor.moveToNext();
            }
            cursor.close();
            close();
        }
        return issueList;
    }

    public boolean deleteQsetData(String qsetId) {
        open();
        String sql = "DELETE from " + DBAdapter.ISSUE_LIST_TABLE + " where qsetId = '" + qsetId + "'";
        database.execSQL(sql);
        close();
        return true;
    }

    public boolean resetTopicTestData(String topicName) {
        open();
        String sql = "DELETE from " + DBAdapter.ISSUE_LIST_TABLE + " where topicName = '" + topicName + "'";
        database.execSQL(sql);
        close();
        return true;
    }
}
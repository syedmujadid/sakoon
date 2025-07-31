package com.mobiquel.udhampur.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.pojo.LogsListModel;
import com.mobiquel.udhampur.pojo.OfficialsListModel;
import com.mobiquel.udhampur.ui.home.listcase.ListOfLogsAdapter;
import com.mobiquel.udhampur.ui.home.listcase.ListOfOfficialsAdapter;

import java.util.List;

public class View_Logs_Dialog extends Dialog implements View.OnClickListener {

    private final Context context;
    public View_Logs_Dialog(Context context) {
        super(context, R.style.BottomSheetDialogStyle_Article);
        this.context = context;
        setContentView(R.layout.dialog_view_logs);

        initViews();
        setListeners();

    }

    private void initViews() {
        title = findViewById(R.id.title);
        close = findViewById(R.id.close);
        back = findViewById(R.id.back);
        bottom = findViewById(R.id.bottom);
        listOfLogs = findViewById(R.id.listOfLogs);

    }

    private void setListeners() {
        bottom.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void setData(List<LogsListModel> list, String id) {
        title.setText("Logs for Case Id #" + id);
        ListOfLogsAdapter mAdapter = new ListOfLogsAdapter(list);
        listOfLogs.setAdapter(mAdapter);
        listOfLogs.setLayoutManager(new LinearLayoutManager(context));

    }

    public void setOfficialData(List<OfficialsListModel> list, String id) {
        title.setText("Officials for Case Id #" + id);
        ListOfOfficialsAdapter mAdapter = new ListOfOfficialsAdapter(list);
        listOfLogs.setAdapter(mAdapter);
        listOfLogs.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom:
                dismiss();
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }

    R.id.back
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismiss();
                break;
        }
    }
}

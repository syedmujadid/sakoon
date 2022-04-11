package com.mobiquel.udhampur.ui.home.listcase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.base.BaseFragment;
import com.mobiquel.udhampur.dao.DAO;
import com.mobiquel.udhampur.interfaces.RecyclerItemClickListener;
import com.mobiquel.udhampur.pojo.IssueListModel;
import com.mobiquel.udhampur.ui.addissue.AddIssue;
import com.mobiquel.udhampur.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DraftCaseFragment extends BaseFragment implements ListDataView, Animation.AnimationListener {

    @BindView(R.id.noResult)
    TextView noResult;
    @BindView(R.id.recyclerView)
    RecyclerView listOfItem;
    private Unbinder unbinder;

    private ListDataPresenter mPresenter;
    private ListOfCaseAdapter_Draft mAdapter;
    private List<String> savedData = new ArrayList<>();
    private LinearLayoutManager manager;
    private List<IssueListModel> draftedList;
    private DAO dao;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new ListDataPresenter(this);
        dao = new DAO(getActivity());


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getDataFromDAO();
                pullToRefresh.setRefreshing(false);
                ((HomeActivity) getActivity()).updateDraftCase();
            }
        });

        return view;
    }

    public void refrehData() {
        mPresenter.getDataFromDAO();
        ((HomeActivity) getActivity()).updateDraftCase();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getDataFromDAO();
        //((HomeActivity)getActivity()).updateDraftCase();
    }

    public void getDataFromDAO() {
        draftedList.clear();
        draftedList = dao.getAllDraftIssues();
        if (draftedList.size() > 0)
            noResult.setVisibility(View.GONE);


        mAdapter = new ListOfCaseAdapter_Draft(draftedList, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(int position) {
                Intent intent = new Intent(getActivity(), AddIssue.class);
                intent.putExtra("SOURCE", "UPDATE");
                intent.putExtra("CASE_ID", draftedList.get(position).getCaseId());
                intent.putExtra("DATA_MODEL", draftedList.get(position));
                startActivity(intent);

            }
        }, new RecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dao.deleteCase( draftedList.get(position).getCaseId());
                                draftedList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                if(draftedList.size()>0){}
                                else
                                    noResult.setVisibility(View.VISIBLE);

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
                alert.setTitle("Delete drafted case");
                alert.show();

            }
        });

        manager = new LinearLayoutManager(getActivity());
        listOfItem.setLayoutManager(manager);
        listOfItem.setAdapter(mAdapter);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // mPresenter.getListOfUsers();


    }


    @Override
    public void initVariables() {
        draftedList = new ArrayList<>();

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
        getDataFromDAO();
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
}

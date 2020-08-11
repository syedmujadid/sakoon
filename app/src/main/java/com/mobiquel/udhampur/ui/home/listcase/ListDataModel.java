package com.mobiquel.udhampur.ui.home.listcase;

import com.mobiquel.udhampur.base.BaseModel;
import com.mobiquel.udhampur.data.preferences.PrefKeys;

import java.util.ArrayList;
import java.util.List;

public class ListDataModel extends BaseModel<ListDataModelListener> {

    public ListDataModel(ListDataModelListener listener) {
        super(listener);
    }

    @Override
    public void init() {

    }

    public void getListData(String startIndex) {




    }

    public  List<String> getPrefData(){
        List<String> data=new ArrayList<>();
        data.add(getDataManager().getStringFromPreference(PrefKeys.USER_ID));
        data.add(getDataManager().getStringFromPreference(PrefKeys.TOKEN));
        data.add(getDataManager().getStringFromPreference(PrefKeys.USER_ROLE));
        data.add(getDataManager().getStringFromPreference(PrefKeys.USER_NAME));
        data.add(getDataManager().getStringFromPreference(PrefKeys.MOBILE_NUMBER));
        data.add(getDataManager().getStringFromPreference(PrefKeys.LOCATION_ID));
        data.add(getDataManager().getStringFromPreference(PrefKeys.LOCATION_NAME));
        return  data;
    }
}
